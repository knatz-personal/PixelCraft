
package com.pixelcraft.manager;

import com.pixelcraft.model.RasterImage;
import com.pixelcraft.util.logging.Logger;
import com.pixelcraft.util.logging.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

public class CanvasManager {

    private static final Logger LOG = LoggerFactory.getLogger(CanvasManager.class);
    private static final double CHECKER_CELL_SIZE = 10.0;
    
    private final Canvas canvas;
    private final GraphicsContext gc;
    private ImagePattern checkerPattern;

    public CanvasManager(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        // Avoid node cache to prevent offscreen RTTexture issues.
        this.canvas.setCache(false);
        if (this.canvas.getParent() != null) {
            this.canvas.getParent().setCache(false);
        }
        // Pre-create checkerboard pattern for fast fills
        createCheckerPattern();
    }
    
    private void createCheckerPattern() {
        // Create a 2x2 cell checkerboard tile
        int tileSize = (int) (CHECKER_CELL_SIZE * 2);
        WritableImage tile = new WritableImage(tileSize, tileSize);
        var writer = tile.getPixelWriter();
        
        int lightGray = 0xFFD3D3D3; // Color.LIGHTGRAY
        int white = 0xFFFFFFFF;     // Color.WHITE
        
        for (int y = 0; y < tileSize; y++) {
            for (int x = 0; x < tileSize; x++) {
                int cellX = x / (int) CHECKER_CELL_SIZE;
                int cellY = y / (int) CHECKER_CELL_SIZE;
                int color = ((cellX + cellY) & 1) == 0 ? lightGray : white;
                writer.setArgb(x, y, color);
            }
        }
        
        checkerPattern = new ImagePattern(tile, 0, 0, tileSize, tileSize, false);
    }

    private boolean isRenderable() {
        if (canvas.getScene() == null || canvas.getScene().getWindow() == null) return false;
        double w = canvas.getWidth(), h = canvas.getHeight();
        return w > 0 && h > 0
                && !Double.isNaN(w) && !Double.isNaN(h)
                && !Double.isInfinite(w) && !Double.isInfinite(h);
    }

    public void render(RasterImage rasterImage) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> render(rasterImage));
            return;
        }

        if (!isRenderable()) {
            return;
        }

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.save();
        try {
            gc.clearRect(0, 0, w, h);
            drawCheckerboard(w, h);

            if (rasterImage != null && rasterImage.isValid()) {
                drawImage(rasterImage);
            }
        } finally {
            gc.restore();
        }
    }

    public void drawCheckerboard() {
        drawCheckerboard(canvas.getWidth(), canvas.getHeight());
    }

    private void drawCheckerboard(double width, double height) {
        if (width <= 0 || height <= 0) return;

        final double cellSize = 10.0;
        final int rows = (int) Math.ceil(height / cellSize);
        final int cols = (int) Math.ceil(width / cellSize);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                gc.setFill(((row + col) & 1) == 0 ? Color.LIGHTGRAY : Color.WHITE);
                gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
            }
        }
    }

    public void drawImage(RasterImage rasterImage) {
        if (rasterImage == null) {
            return;
        }
        Image fxImage = rasterImage.toFXImage();
        if (fxImage == null) {
            return;
        }

        // Draw at position (0,0) with natural image dimensions
        // Zoom is handled by canvas scale transform, not by image render scale
        double dstW = rasterImage.getWidth();
        double dstH = rasterImage.getHeight();

        if (dstW <= 0 || dstH <= 0 || Double.isNaN(dstW) || Double.isNaN(dstH)) {
            return;
        }

        gc.drawImage(fxImage, 0, 0, dstW, dstH);
    }

    /**
     * Renders the image scaled to fit the specified dimensions.
     * This avoids using Canvas scale transforms which cause GPU texture allocation issues.
     */
    public void renderScaled(RasterImage rasterImage, double targetWidth, double targetHeight) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> renderScaled(rasterImage, targetWidth, targetHeight));
            return;
        }

        if (!isRenderable()) {
            return;
        }

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.save();
        try {
            gc.clearRect(0, 0, w, h);
            drawCheckerboard(w, h);

            if (rasterImage != null && rasterImage.isValid()) {
                drawImageScaled(rasterImage, targetWidth, targetHeight);
            }
        } finally {
            gc.restore();
        }
    }

    private void drawImageScaled(RasterImage rasterImage, double targetWidth, double targetHeight) {
        Image fxImage = rasterImage.toFXImage();
        if (fxImage == null) {
            return;
        }

        // Draw the image scaled to target dimensions
        // Source is the full image, destination is the scaled size
        double srcW = fxImage.getWidth();
        double srcH = fxImage.getHeight();
        
        gc.drawImage(fxImage, 0, 0, srcW, srcH, 0, 0, targetWidth, targetHeight);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
