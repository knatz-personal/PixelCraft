package com.pixelcraft.commands;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import com.pixelcraft.manager.FileManager;
import com.pixelcraft.model.RasterImage;
import com.pixelcraft.util.logging.Logger;
import com.pixelcraft.util.logging.LoggerFactory;
import com.pixelcraft.util.logging.eLogLevel;

/**
 * Command for opening a recently accessed image file.
 * Supports undo by preserving the previous image state.
 * 
 * <p>This command validates file existence and readability before execution,
 * and includes proper error handling and logging.</p>
 */
/**
 * Command implementation for opening a recently accessed image file.
 * 
 * <p>This command implements the {@link ICommand} interface to provide
 * undoable file opening operations within the application's command pattern.
 * It handles loading image files from the file system through the FileManager
 * and maintains state for undo functionality.</p>
 * 
 * <h2>Features:</h2>
 * <ul>
 *   <li>File validation including existence, readability, and size checks</li>
 *   <li>Support for common image formats (PNG, JPG, JPEG, GIF, BMP, TIFF)</li>
 *   <li>Full undo support with deep cloning to prevent memory reference issues</li>
 *   <li>Automatic state restoration on failed load attempts</li>
 *   <li>Resource cleanup to prevent memory leaks</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * FileManager fileManager = new FileManager();
 * ICommand openCommand = new OpenRecentCommand(fileManager, "/path/to/image.png");
 * openCommand.execute();
 * 
 * // To undo the operation
 * openCommand.undo();
 * }</pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>This class is not thread-safe. External synchronization is required
 * if instances are accessed from multiple threads.</p>
 * 
 * @author PixelCraft Team
 * @version 1.0
 * @see ICommand
 * @see FileManager
 * @see RasterImage
 */
public class OpenRecentCommand implements ICommand {
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenRecentCommand.class);
    
    private final FileManager fileManager;
    private final String filePath;
    
    // State for undo - using deep clone to avoid memory issues
    private RasterImage previousImageClone;
    private File previousFile;
    private boolean hadPreviousImage;
    private boolean executionSuccessful;

    /**
     * Creates a new command to open a recent file.
     * 
     * @param fileManager the file manager to use for loading images
     * @param filePath the path to the file to open
     * @throws IllegalArgumentException if fileManager is null or filePath is null/empty
     */
    public OpenRecentCommand(FileManager fileManager, String filePath) {
        Objects.requireNonNull(fileManager, "FileManager cannot be null");
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        this.fileManager = fileManager;
        this.filePath = filePath.trim();
        this.executionSuccessful = false;
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
            executionSuccessful = false;
            return;
        }
        
        try {
            // Save current state for undo (deep clone to avoid memory references)
            saveCurrentState();
            
            // Load the file
            boolean loaded = fileManager.loadImage(file);
            executionSuccessful = loaded;
            
            if (loaded) {
                LOG.info("Successfully opened recent file: " + file.getName());
            } else {
                LOG.warn("Failed to load image from: " + filePath);
                // Restore previous state if load failed
                restorePreviousState();
            }
            
        } catch (Exception e) {
            LOG.error("Error executing OpenRecentCommand: " + e.getMessage(), e);
            executionSuccessful = false;
            // Attempt to restore previous state
            restorePreviousState();
        } finally {
            // Clean up previous image clone if execution was successful
            if (executionSuccessful && previousImageClone != null) {
                // Keep the clone for undo, but log the memory usage
                if (LOG.isEnabled(eLogLevel.DEBUG)) {
                    LOG.debug("Stored previous image clone for undo (" + 
                             previousImageClone.getWidth() + "x" + 
                             previousImageClone.getHeight() + ")");
                }
            }
        }
    }

    @Override
    public void undo() {
        if (!executionSuccessful) {
            LOG.warn("Cannot undo - command execution was not successful");
            return;
        }
        
        LOG.info("Undoing OpenRecentCommand");
        
        try {
            restorePreviousState();
            LOG.info("Successfully undid OpenRecentCommand");
        } catch (Exception e) {
            LOG.error("Error during undo: " + e.getMessage(), e);
        } finally {
            // Clean up clone after undo
            cleanup();
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
    
    /**
     * Saves the current state for undo operations.
     * Uses deep cloning to avoid memory reference issues.
     */
    private void saveCurrentState() {
        Optional<RasterImage> currentImage = fileManager.getCurrentImage();
        hadPreviousImage = currentImage.isPresent();
        
        if (hadPreviousImage) {
            try {
                // Deep clone to avoid holding references to the original
                previousImageClone = currentImage.get().deepClone();
                previousFile = fileManager.getCurrentFile().orElse(null);
                
                LOG.debug("Saved previous state for undo");
            } catch (Exception e) {
                LOG.error("Failed to clone previous image: " + e.getMessage(), e);
                previousImageClone = null;
                previousFile = null;
            }
        } else {
            previousImageClone = null;
            previousFile = null;
        }
    }
    
    /**
     * Restores the previous state saved before execution.
     */
    private void restorePreviousState() {
        if (!hadPreviousImage) {
            LOG.debug("No previous image to restore");
            return;
        }
        
        try {
            if (previousFile != null && previousFile.exists()) {
                // Reload from file if available
                fileManager.loadImage(previousFile);
                LOG.debug("Restored previous file: " + previousFile.getName());
            } else if (previousImageClone != null) {
                // Restore from cloned image
                RasterImage restored = fileManager.createNewImage(
                    previousImageClone.getWidth(), 
                    previousImageClone.getHeight()
                );
                restored.copyPixelsFrom(previousImageClone);
                LOG.debug("Restored previous image from clone");
            } else {
                LOG.warn("No previous state available to restore");
            }
        } catch (Exception e) {
            LOG.error("Failed to restore previous state: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cleans up resources held by this command.
     * Should be called after undo or when command is no longer needed.
     */
    private void cleanup() {
        if (previousImageClone != null) {
            previousImageClone.clearCache();
            previousImageClone = null;
        }
        previousFile = null;
        LOG.debug("Cleaned up OpenRecentCommand resources");
    }
}
