package com.pixelcraft.commands;

import java.io.File;

import com.pixelcraft.manager.FileManager;
import com.pixelcraft.util.logging.Logger;
import com.pixelcraft.util.logging.LoggerFactory;

public class OpenRecentCommand extends  CommandBase {
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenRecentCommand.class);

    private final String filePath;

    /**
     * Creates a new command to open a recent file.
     * 
     * @param fileManager the file manager to use for loading images
     * @param filePath the path to the file to open
     * @throws IllegalArgumentException if fileManager is null or filePath is null/empty
     */
    public OpenRecentCommand(FileManager fileManager, String filePath) {
        super(fileManager);
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
                
        this.filePath = filePath.trim();
    }

    /**
     * Executes the OpenRecentCommand to load a previously opened file.
     * <p>
     * This method performs the following operations:
     * <ol>
     *   <li>Validates that the specified file exists and is readable</li>
     *   <li>Saves the current editor state for potential undo operations</li>
     *   <li>Attempts to load the image file through the file manager</li>
     *   <li>Restores the previous state if loading fails</li>
     * </ol>
     * </p>
     * <p>
     * The execution result is tracked via the {@code executionSuccessful} flag,
     * which can be queried after execution to determine if the operation completed
     * successfully.
     * </p>
     * <p>
     * If an exception occurs during execution, the previous state is restored
     * and the error is logged. The previous image clone is retained for undo
     * functionality when execution succeeds.
     * </p>
     */
    @Override
    public void execute() {
        LOG.info("Executing OpenRecentCommand for: " + filePath);
        
        // Validate file before proceeding
        File file = new File(filePath);
        if (!validateFile(file)) {
            LOG.error("File validation failed for: " + filePath);
            return;
        }
        
        try {
            takeSnapshot();
            
            // Load the file
            boolean loaded = fileManager.loadImage(file);
                        
            if (loaded) {
                LOG.info("Successfully opened recent file: " + file.getName());
            } else {
                LOG.warn("Failed to load image from: " + filePath);
                restoreSnapshot();
            }
            
        } catch (Exception e) {
            LOG.error("Error executing OpenRecentCommand: " + e.getMessage(), e);
            restoreSnapshot();
        } 
    }

    @Override
    public String getDescription() {
        File file = new File(filePath);
        return "Open Recent: " + file.getName();
    }
    
    /**
     * Validates that the file exists, is readable, and is a regular file.
     * 
     * @param file the file to validate
     * @return true if file is valid, false otherwise
     */
    private boolean validateFile(File file) {
        if (file == null) {
            LOG.error("File is null");
            return false;
        }
        
        if (!file.exists()) {
            LOG.error("File does not exist: " + file.getAbsolutePath());
            return false;
        }
        
        if (!file.isFile()) {
            LOG.error("Path is not a regular file: " + file.getAbsolutePath());
            return false;
        }
        
        if (!file.canRead()) {
            LOG.error("File is not readable: " + file.getAbsolutePath());
            return false;
        }
        
        // Check file size (warn if very large)
        long sizeInMB = file.length() / (1024 * 1024);
        if (sizeInMB > 100) {
            LOG.warn("Large file detected: " + sizeInMB + "MB - " + file.getName());
        }
        
        // Validate file extension (optional - could support more formats)
        String name = file.getName().toLowerCase();
        if (!hasValidImageExtension(name)) {
            LOG.warn("File may not be a supported image format: " + name);
            // Don't fail - let RasterImage attempt to load it
        }
        
        return true;
    }
    
    /**
     * Checks if the filename has a valid image extension.
     * 
     * @param filename the filename to check
     * @return true if extension is recognized
     */
    private boolean hasValidImageExtension(String filename) {
        return filename.endsWith(".png") || 
               filename.endsWith(".jpg") || 
               filename.endsWith(".jpeg") || 
               filename.endsWith(".gif") || 
               filename.endsWith(".bmp") ||
               filename.endsWith(".tiff") ||
               filename.endsWith(".tif");
    }
    
}
