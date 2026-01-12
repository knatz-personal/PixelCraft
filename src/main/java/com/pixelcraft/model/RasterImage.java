package com.pixelcraft.model;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

import javax.imageio.ImageIO;

import com.pixelcraft.util.logging.Logger;
import com.pixelcraft.util.logging.LoggerFactory;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Represents a raster (bitmap) image with support for non-destructive viewing
 * and destructive editing operations.
 *
 * <p>
 * This class maintains two internal BufferedImage instances:
 * <ul>
 * <li>{@code originalImage} - An immutable source image that is never mutated,
 * used as the basis for high-quality resampling operations.</li>
 * <li>{@code image} - The current working bitmap used for editing and
 * preview.</li>
 * </ul>
 *
 * <p>
 * The class supports both pixel art workflows (nearest-neighbor scaling) and
 * photography workflows (bicubic interpolation) through the
 * {@code pixelArtMode} setting.
 *
 * <p>
 * View state properties ({@code renderScale}, {@code positionX},
 * {@code positionY}) are non-destructive and do not modify the underlying image
 * data. These are intended for controlling how the image is displayed without
 * altering pixel content.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * // Load from file
 * RasterImage img = new RasterImage(new File("photo.png"));
 *
 * // Create blank canvas
 * RasterImage canvas = new RasterImage(800, 600);
 *
 * // Modify pixels
 * canvas.setPixel(10, 10, 0xFFFF0000); // Set red pixel
 *
 * // Adjust view (non-destructive)
 * img.setRenderScale(2.0);
 * img.setPosition(100, 50);
 *
 * // Convert for JavaFX rendering
 * Image fxImage = img.toFXImage();
 * }</pre>
 *
 * @see java.awt.image.BufferedImage
 * @see javafx.embed.swing.SwingFXUtils
 */
public final class RasterImage {
    private static final Logger LOG = LoggerFactory.getLogger(RasterImage.class);

    private BufferedImage originalImage;
    private BufferedImage image;
    private boolean modified;

    private transient Image cachedFxImage;
    private transient boolean fxImageDirty = true;

    // View state (non-destructive)
    private double renderScale = 1.0;
    private double positionX;
    private double positionY;

    // Optional: user preference to treat scaling like pixel art (snap to integer scale)
    private boolean pixelArtMode = false;

    /**
     * Constructs a RasterImage by loading image data from the specified file.
     * The image is positioned at the origin (0.0, 0.0) with a default render
     * scale of 1.0.
     *
     * @param file the file from which to load the image data
     */
    public RasterImage(File file) {
        loadImageFromFile(file);
        this.positionX = 0.0;
        this.positionY = 0.0;
        this.renderScale = 1.0;
    }

    /**
     * Constructs a new RasterImage with a blank image of the specified
     * dimensions. The image is initialized with ARGB color type and no
     * modifications. Position is set to origin (0,0) with a render scale of
     * 1.0.
     *
     * @param width the width of the new image in pixels
     * @param height the height of the new image in pixels
     */
    public RasterImage(int width, int height) {
        // Generate a new blank image; no original available yet.
        this.originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.image = deepCopyBuffered(this.originalImage);
        this.modified = false;
        this.positionX = 0.0;
        this.positionY = 0.0;
        this.renderScale = 1.0;
    }

    public int getPixel(int x, int y) {
        return image.getRGB(x, y);
    }

    public void setPixel(int x, int y, int color) {
        // Copy-on-write: create working copy before first modification
        if (image == originalImage && originalImage != null) {
            image = deepCopyBuffered(originalImage);
        }
        image.setRGB(x, y, color);
        modified = true;
        fxImageDirty = true;
    }

    public int getWidth() {
        return image != null ? image.getWidth() : 0;
    }

    public int getHeight() {
        return image != null ? image.getHeight() : 0;
    }

    public int getOriginalWidth() {
        return originalImage != null ? originalImage.getWidth() : getWidth();
    }

    public int getOriginalHeight() {
        return originalImage != null ? originalImage.getHeight() : getHeight();
    }

    public double getRenderWidth() {
        return getOriginalWidth() * renderScale;
    }

    public double getRenderHeight() {
        return getOriginalHeight() * renderScale;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public void setPosition(double x, double y) {
        this.positionX = x;
        this.positionY = y;
    }

    public double getRenderScale() {
        return renderScale;
    }

    /**
     * Calculates the approximate size of the image in bytes.
     * <p>
     * This calculation is based on the image dimensions and color model.
     * For TYPE_INT_ARGB and TYPE_INT_RGB images, each pixel uses 4 bytes.
     * For TYPE_3BYTE_BGR, each pixel uses 3 bytes.
     * For TYPE_BYTE_GRAY, each pixel uses 1 byte.
     * </p>
     *
     * @return the approximate memory size of the image in bytes
     */
    public long getSizeInBytes() {
        if (image == null) {
            return 0L;
        }
        
        int width = image.getWidth();
        int height = image.getHeight();
        int bytesPerPixel;
        
        // Determine bytes per pixel based on image type
        switch (image.getType()) {
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
            case BufferedImage.TYPE_INT_RGB:
            case BufferedImage.TYPE_INT_BGR:
                bytesPerPixel = 4;
                break;
            case BufferedImage.TYPE_3BYTE_BGR:
                bytesPerPixel = 3;
                break;
            case BufferedImage.TYPE_BYTE_GRAY:
            case BufferedImage.TYPE_BYTE_BINARY:
            case BufferedImage.TYPE_BYTE_INDEXED:
                bytesPerPixel = 1;
                break;
            case BufferedImage.TYPE_USHORT_GRAY:
            case BufferedImage.TYPE_USHORT_565_RGB:
            case BufferedImage.TYPE_USHORT_555_RGB:
                bytesPerPixel = 2;
                break;
            default:
                // For custom types, use data buffer size
                int dataTypeSize = DataBufferInt.class.isAssignableFrom(
                    image.getRaster().getDataBuffer().getClass()) ? 4 : 1;
                return (long) image.getRaster().getDataBuffer().getSize() * dataTypeSize;
        }
        
        return (long) width * height * bytesPerPixel;
    }

    private void markPixelsDirty() {
        fxImageDirty = true;
    }

    /**
     * Sets the render scale for displaying the image.
     * <p>
     * This method adjusts how the image is drawn without modifying the
     * underlying image data. In pixel art mode, the scale is snapped to integer
     * values (minimum 1.0) to preserve the pixel grid alignment. Otherwise, the
     * scale is clamped to a minimum of 0.01.
     * </p>
     * <p>
     * Note: This is a non-destructive operation and does not mark the image as
     * modified.
     * </p>
     *
     * @param scale the desired render scale factor; values below the minimum
     * threshold will be clamped appropriately based on the current mode
     */
    public void setRenderScale(double scale) {
        double s = Math.max(0.01, scale);
        if (pixelArtMode) {
            // Snap to integer scale to preserve pixel grid
            s = Math.max(1.0, Math.round(s));
        }
        this.renderScale = s;
        // Non-destructive: do not set modified; this changes only how we draw.
    }

    public boolean isPixelArtMode() {
        return pixelArtMode;
    }

    public void setPixelArtMode(boolean pixelArtMode) {
        this.pixelArtMode = pixelArtMode;
    }

    /**
     * Converts the internal BufferedImage to a JavaFX Image with caching.
     */
    public Image toFXImage() {
        if (image == null) {
            return null;
        }
        if (cachedFxImage == null || fxImageDirty) {
            // Use SwingFXUtils which handles all formats efficiently
            cachedFxImage = SwingFXUtils.toFXImage(image, null);
            fxImageDirty = false;
        }
        return cachedFxImage;
    }

    /**
     * Clears cached resources.
     */
    public void clearCache() {
        cachedFxImage = null;
        fxImageDirty = true;
    }

    public boolean isModified() {
        return modified;
    }

    public boolean isValid() {
        return this.image != null;
    }

    /**
     * Loads an image from the specified file and initializes the image buffers.
     */
    private void loadImageFromFile(File file) {
        try {
            BufferedImage loaded = ImageIO.read(file);
            if (loaded != null && loaded.getWidth() > 0 && loaded.getHeight() > 0) {
                // Keep the original format - don't convert unless necessary
                this.originalImage = loaded;
                this.image = this.originalImage;
                this.modified = false;
            } else {
                this.originalImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                this.image = this.originalImage;
                this.modified = false;
            }
        } catch (Exception e) {
            this.originalImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            this.image = this.originalImage;
            this.modified = false;
        }
        this.cachedFxImage = null;
        this.fxImageDirty = true;
    }

    /**
     * Simple ARGB conversion.
     */
    private static BufferedImage convertToArgb(BufferedImage src) {
        // Fast path for INT_RGB - just add alpha channel
        if (src.getType() == BufferedImage.TYPE_INT_RGB) {
            int w = src.getWidth();
            int h = src.getHeight();
            BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            int[] srcData = ((DataBufferInt) src.getRaster().getDataBuffer()).getData();
            int[] destData = ((DataBufferInt) dest.getRaster().getDataBuffer()).getData();
            for (int i = 0; i < srcData.length; i++) {
                destData[i] = srcData[i] | 0xFF000000;
            }
            return dest;
        }
        
        // General case
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dest.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return dest;
    }

    /**
     * Resizes the image to the specified dimensions.
     * <p>
     * This method creates a new scaled version of the original image using
     * different interpolation strategies based on the current mode:
     * <ul>
     * <li><b>Pixel art mode:</b> Uses nearest-neighbor interpolation to
     * preserve sharp edges and pixel boundaries without anti-aliasing.</li>
     * <li><b>Photography mode:</b> Uses bicubic interpolation with
     * anti-aliasing for smoother, higher quality results.</li>
     * </ul>
     * <p>
     * The resizing always operates on the original image to prevent cumulative
     * quality degradation from repeated resize operations.
     * <p>
     * The method performs no operation if:
     * <ul>
     * <li>The original image is null</li>
     * <li>The current image already matches the requested dimensions</li>
     * </ul>
     *
     * @param width the desired width in pixels; values less than 1 are clamped
     * to 1
     * @param height the desired height in pixels; values less than 1 are
     * clamped to 1
     */
    public void resize(int width, int height) {
        if (this.originalImage == null) {
            return;
        }

        int newWidth = Math.max(1, width);
        int newHeight = Math.max(1, height);

        if (this.image != null
                && newWidth == image.getWidth()
                && newHeight == image.getHeight()) {
            return; // No-op
        }

        BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setComposite(AlphaComposite.Src);

        if (pixelArtMode) {
            // Pixel art: nearest-neighbor
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        } else {
            // Photography: bicubic for quality (or bilinear for speed)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        // Always resample from original to avoid compounding artifacts
        g.drawImage(this.originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        this.image = scaled;
        this.modified = true;
        // Invalidate cached FX image
        this.cachedFxImage = null;
        this.fxImageDirty = true;
    }

    /**
     * Resizes the original image to the specified dimensions using an affine
     * transformation.
     *
     * <p>
     * This method scales the original image to the new width and height while
     * preserving the aspect ratio is not enforced (the image will be stretched
     * to fit the exact dimensions). The interpolation type depends on the
     * current pixel art mode setting:</p>
     * <ul>
     * <li>Pixel art mode enabled: Uses nearest neighbor interpolation to
     * preserve hard edges</li>
     * <li>Pixel art mode disabled: Uses bicubic interpolation for smoother
     * results</li>
     * </ul>
     *
     * <p>
     * The method ensures minimum dimensions of 1x1 pixel. After resizing, the
     * internal image reference is updated and the modified flag is set to
     * true.</p>
     *
     * @param width the desired width of the resized image in pixels (minimum
     * value: 1)
     * @param height the desired height of the resized image in pixels (minimum
     * value: 1)
     */
    public void resizeUsingAffine(int width, int height) {
        if (this.originalImage == null) {
            return;
        }

        int newWidth = Math.max(1, width);
        int newHeight = Math.max(1, height);

        double sx = (double) newWidth / originalImage.getWidth();
        double sy = (double) newHeight / originalImage.getHeight();

        AffineTransform at = AffineTransform.getScaleInstance(sx, sy);
        int type = pixelArtMode ? AffineTransformOp.TYPE_NEAREST_NEIGHBOR : AffineTransformOp.TYPE_BICUBIC;

        AffineTransformOp op = new AffineTransformOp(at, type);
        BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        op.filter(originalImage, scaled);

        this.image = scaled;
        this.modified = true;
        // Invalidate cached FX image
        this.cachedFxImage = null;
        this.fxImageDirty = true;
    }

    /**
     * Creates a deep copy of a BufferedImage.
     *
     * This method creates a new BufferedImage with the same dimensions as the
     * source image, using ARGB color model, and copies all pixel data from the
     * source image to the new image while preserving alpha channel information.
     *
     * @param src the source BufferedImage to be copied
     * @return a new BufferedImage that is a deep copy of the source image
     */
    private static BufferedImage deepCopyBuffered(BufferedImage src) {
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return copy;
    }

    /**
     * Ensures that the given BufferedImage is of type TYPE_INT_ARGB. If the
     * source image is already in ARGB format, it is returned as-is. Otherwise,
     * a deep copy of the image is created and returned in ARGB format.
     *
     * @param src the source BufferedImage to check and potentially convert
     * @return the original image if already ARGB, or a deep copy converted to
     * ARGB format
     */
    private static BufferedImage ensureArgb(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_ARGB) {
            return src;
        }
        return deepCopyBuffered(src);
    }

    /**
     * Copies pixels from another RasterImage to this image. The copy operation
     * is limited to the overlapping area of both images, using the minimum
     * width and height of the two images.
     *
     * @param other the source RasterImage from which pixels will be copied
     */
    public void copyPixelsFrom(RasterImage other) {
        int w = Math.min(getWidth(), other.getWidth());
        int h = Math.min(getHeight(), other.getHeight());
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                setPixel(x, y, other.getPixel(x, y));
            }
        }
    }

    /**
     * Creates a deep clone of this RasterImage object.
     * <p>
     * This method creates a new RasterImage instance with the same dimensions
     * and copies all properties including deep copies of both the original and
     * current image buffers.
     * </p>
     *
     * @return a new RasterImage object that is a deep copy of this instance,
     * with independent copies of all mutable fields
     */
    public RasterImage deepClone() {
        RasterImage clone = new RasterImage(getOriginalWidth(), getOriginalHeight());
        clone.originalImage = deepCopyBuffered(this.originalImage != null ? this.originalImage : this.image);
        clone.image = deepCopyBuffered(this.image);
        clone.modified = this.modified;
        clone.renderScale = this.renderScale;
        clone.positionX = this.positionX;
        clone.positionY = this.positionY;
        clone.pixelArtMode = this.pixelArtMode;
        return clone;
    }
}
