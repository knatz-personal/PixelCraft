package com.pixelcraft.manager;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.pixelcraft.event.IImageChangeListener;
import com.pixelcraft.model.RasterImage;
import com.pixelcraft.util.logging.LoggerFactory;

import javafx.application.Platform;

public final class FileManager {
    private static final com.pixelcraft.util.logging.Logger LOG = LoggerFactory.getLogger(FileManager.class);

    private RasterImage currentImage;
    private File currentFile;
    private boolean isModified;
    private IImageChangeListener listener;
    private volatile boolean loading = false;

    public void setListener(IImageChangeListener listener) {
        this.listener = listener;
    }

    /**
     * Loads an image asynchronously to avoid blocking the UI thread.
     * Returns a CompletableFuture that completes when loading is done.
     */
    public CompletableFuture<Boolean> loadImageAsync(File file) {
        loading = true;
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                RasterImage newImage = new RasterImage(file);
                
                // Switch to FX thread to update state and notify
                Platform.runLater(() -> {
                    if (currentImage != null) {
                        currentImage.clearCache();
                    }
                    currentFile = file;
                    currentImage = newImage;
                    isModified = false;
                    loading = false;
                    notifyChanges();
                });
                
                return true;
            } catch (Exception e) {
                LOG.error("Error loading image: " + e.getMessage());
                loading = false;
                return false;
            }
        });
    }

    public boolean loadImage(File file) {
        if (currentImage != null) {
            currentImage.clearCache();
        }
        
        currentFile = file;
        currentImage = new RasterImage(file);
        isModified = false;
        notifyChanges();
        return true;
    }
    
    public boolean isLoading() {
        return loading;
    }

    public RasterImage createNewImage(int width, int height) {
        currentImage = new RasterImage(width, height);
        currentFile = null;
        isModified = true;

        // Fill with white
        int white = 0xFFFFFFFF;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                currentImage.setPixel(x, y, white);
            }
        }

        notifyChanges();
        return currentImage;
    }
    
    public boolean save() {
        if (currentFile == null) {
            return false; // Need to use saveAs
        }
        // TODO: Implement actual save logic
        isModified = false;
        notifyModificationChanged();
        return true;
    }
    
    public boolean saveAs(File file) {
        currentFile = file;
        // TODO: Implement actual save logic
        isModified = false;
        notifyChanges();
        return true;
    }
    
    public void markModified() {
        if (!isModified) {
            isModified = true;
            notifyModificationChanged();
        }
    }
    
    public Optional<RasterImage> getCurrentImage() {
        return Optional.ofNullable(currentImage);
    }
    
    public Optional<File> getCurrentFile() {
        return Optional.ofNullable(currentFile);
    }
    
    public boolean isModified() {
        return isModified || (currentImage != null && currentImage.isModified());
    }
    
    public String getDisplayTitle() {
        StringBuilder title = new StringBuilder("PixelCraft");
        if (currentFile != null) {
            title.append(" - ").append(currentFile.getName());
        }
        if (isModified()) {
            title.append(" *");
        }
        return title.toString();
    }
    
    private void notifyChanges() {
        Runnable r = () -> {
            if (listener != null) {
                listener.onImageChanged(Optional.ofNullable(currentImage));
                listener.onFileChanged(Optional.ofNullable(currentFile));
                listener.onModificationStateChanged(isModified);
            }
        };

        // Check if toolkit is available and we're not on FX thread
        try {
            if (Platform.isFxApplicationThread()) {
                r.run();
            } else {
                Platform.runLater(r);
            }
        } catch (IllegalStateException e) {
            // Toolkit not initialized (e.g., in unit tests without JavaFX)
            // Just run directly
            r.run();
        }
    }
    
    private void notifyModificationChanged() {
        Runnable r = () -> {
            if (listener != null) {
                listener.onModificationStateChanged(isModified);
            }
        };

        // Check if toolkit is available and we're not on FX thread
        try {
            if (Platform.isFxApplicationThread()) {
                r.run();
            } else {
                Platform.runLater(r);
            }
        } catch (IllegalStateException e) {
            // Toolkit not initialized (e.g., in unit tests without JavaFX)
            // Just run directly
            r.run();
        }
    }

    public String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            return name.substring(lastDot + 1).toLowerCase();
        }
        return "bmp"; // default
    }

}
