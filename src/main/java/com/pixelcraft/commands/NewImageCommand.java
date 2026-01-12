package com.pixelcraft.commands;

import java.io.File;

import com.pixelcraft.manager.FileManager;
import com.pixelcraft.model.RasterImage;

public class NewImageCommand implements ICommand {

    private final FileManager fileManager;
    private final int width;
    private final int height;

    // State for undo
    private RasterImage previousImage;
    private File previousFile;

    public NewImageCommand(FileManager fileManager, int width, int height) {
        this.fileManager = fileManager;
        this.width = width;
        this.height = height;
    }

    @Override
    public void execute() {
        // Save current state for undo
        previousImage = fileManager.getCurrentImage().orElse(null);
        previousFile = fileManager.getCurrentFile().orElse(null);

        // Create new image with specified dimensions
        fileManager.createNewImage(width, height);
    }

    @Override
    public void undo() {
        // Restore previous state
        if (previousImage == null) {
            return;
        }

        // Restore the previous image and file
        if (previousFile != null) {
            fileManager.loadImage(previousFile);
        } 
    }

    @Override
    public String getDescription() {
        return String.format("New Image (%dx%d)", width, height);
    }
}
