package com.pixelcraft.commands;

import java.io.File;
import java.util.Optional;

import com.pixelcraft.manager.FileManager;
import com.pixelcraft.manager.UserPreferenceManager;
import com.pixelcraft.model.RasterImage;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.stage.FileChooser;

public class OpenImageCommand extends CommandBase {

    private final Canvas canvas;
    private final UserPreferenceManager userPreferences;

    //#region Snapshot for Undo
    private File previousFile;
    private String lastDescription = "Open: ";
    private File selectedFile; // Track the file opened for redo
    //#endregion

    public OpenImageCommand(FileManager fileManager, Canvas canvas, UserPreferenceManager userPreferences) {
        super(fileManager);
        this.canvas = canvas;
        this.userPreferences = userPreferences;
    }

    @Override
    public void execute() {
        // Check if already loading
        if (fileManager.isLoading()) {
            lastDescription = "Open Image (busy)";
            return;
        }
        
        // Capture previous state for undo - prefer file reference over deep clone for performance
        previousFile = fileManager.getCurrentFile().orElse(null);
        Optional<RasterImage> currentOpt = fileManager.getCurrentImage();
        if (currentOpt.isPresent()) {
            RasterImage current = currentOpt.get();
            previousPositionX = current.getPositionX();
            previousPositionY = current.getPositionY();
            // Only deep clone if there's no file to restore from (in-memory image)
            snapshot = (previousFile == null) ? current.deepClone() : null;
        } else {
            snapshot = null;
        }

        // If we already have a selected file (redo case), load it directly
        if (selectedFile != null) {
            Runnable task = () -> loadSelectedFile(selectedFile);
            if (Platform.isFxApplicationThread()) {
                task.run();
            } else {
                Platform.runLater(task);
            }
            return;
        }

        // Open file chooser on FX thread and continue logic there
        Runnable task = () -> chooseFile();

        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }

    @Override
    public void undo() {
        Runnable task = () -> {
            if (previousFile != null) {
                boolean loaded = fileManager.loadImage(previousFile);
                if (!loaded) {
                    // Fall back to snapshot if load failed
                    restoreSnapshot();
                    return;
                }
                fileManager.getCurrentImage().ifPresent(img
                        -> img.setPosition(previousPositionX, previousPositionY)
                );
                lastDescription = "Undo open: restored " + previousFile.getName();
            } else {
                // No previous file; restore snapshot
                restoreSnapshot();
                lastDescription = "Undo open: restored in-memory image";
            }
        };

        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }

    @Override
    public String getDescription() {
        return lastDescription;
    }

    //#region Helpers
    
    /**
     * Opens a file chooser dialog for the user to select an image file.
     * <p>
     * This method displays a file chooser dialog filtered for common image formats
     * (PNG, JPG, JPEG, BMP, GIF). If a previous directory was used, it will be
     * set as the initial directory for the dialog.
     * </p>
     * <p>
     * After a file is selected, the method attempts to load the image asynchronously
     * using the file manager. The UI remains responsive during loading.
     * </p>
     * <p>
     * The method updates {@code lastDescription} to reflect the operation result:
     * <ul>
     *   <li>"Open Image (no window)" - if no valid window is available</li>
     *   <li>"Open Image (cancelled)" - if the user cancels the dialog</li>
     *   <li>"Loading: [filename]" - while the image is being loaded</li>
     *   <li>"Open: [filename]" - if the image is successfully loaded</li>
     * </ul>
     * </p>
     */
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );
        
        // Use injected UserPreferences for last directory
        userPreferences.getLastOpenDirectory().ifPresent(fileChooser::setInitialDirectory);

        var scene = canvas.getScene();
        if (scene == null || scene.getWindow() == null) {
            lastDescription = "Open Image (no window)";
            return;
        }

        File tempSelectedFile = fileChooser.showOpenDialog(scene.getWindow());
        if (tempSelectedFile == null) {
            lastDescription = "Open Image (cancelled)";
            return; // User cancelled
        }

        // Persist the directory for future use
        userPreferences.setLastOpenDirectory(tempSelectedFile.getParentFile());
        this.selectedFile = tempSelectedFile; // Store for redo
        loadSelectedFile(selectedFile);
    }

    /**
     * Loads the specified image file asynchronously.
     * 
     * @param file The file to load
     */
    private void loadSelectedFile(File file) {
        lastDescription = "Loading: " + file.getName();
        
        // Load asynchronously - UI stays responsive
        fileManager.loadImageAsync(file).thenAccept(loaded -> {
            if (loaded) {
                lastDescription = "Open: " + file.getName();
            } else {
                lastDescription = "Failed to open: " + file.getName();
            }
        });
        // Note: MainController.onImageChanged handles zoomToFit and rendering
    }
   //#endregion
}
