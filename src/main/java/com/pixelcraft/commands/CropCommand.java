package com.pixelcraft.commands;


import com.pixelcraft.manager.FileManager;
import com.pixelcraft.model.RasterImage;

import javafx.scene.shape.Rectangle;

public final class CropCommand extends CommandBase {

    private final Rectangle cropBounds;

    public CropCommand(FileManager fileManager, Rectangle cropBounds) {
        super(fileManager);
        this.cropBounds = cropBounds;
    }

    @Override
    public void execute() {
        takeSnapshot();

        if (snapshot == null) {
            return;
        }

        if (cropBounds.getX() < 0 || cropBounds.getY() < 0
                || cropBounds.getX() + cropBounds.getWidth() > snapshot.getWidth()
                || cropBounds.getY() + cropBounds.getHeight() > snapshot.getHeight()) {
            throw new IllegalArgumentException("Crop bounds exceed image dimensions");
        }

        // Use RasterImage.crop() for cleaner implementation
        RasterImage cropped = snapshot.crop(cropBounds);
        fileManager.setCurrentImage(cropped);
    }

    @Override
    public String getDescription() {
        if (cropBounds == null) {
            return "Crop (no bounds)";
        }
        return String.format("Crop [(%d,%d) %dx%d]", (int)cropBounds.getX(), (int)cropBounds.getY(), (int)cropBounds.getWidth(), (int)cropBounds.getHeight());
    }
}