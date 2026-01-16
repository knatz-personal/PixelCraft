package com.pixelcraft.commands;

import com.pixelcraft.manager.FileManager;

public class NewImageCommand extends CommandBase {

    private final int width;
    private final int height;

    public NewImageCommand(FileManager fileManager, int width, int height) {
        super(fileManager);
        this.width = width;
        this.height = height;
    }

    @Override
    public void execute() {
        takeSnapshot();

        // Create new image with specified dimensions
        fileManager.createNewImage(width, height);
    }

    @Override
    public String getDescription() {
        return String.format("New Image (%dx%d)", width, height);
    }
}
