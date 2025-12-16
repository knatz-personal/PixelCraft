package com.pixelcraft;

import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pixelcraft.manager.RecentFilesManager;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Pane canvasPane;
    
    @FXML
    private Menu mnuRecents;

    private Canvas canvas;
    private GraphicsContext gc;
    private Image loadedImage;
    private File currentFile = null;
    private boolean isModified = false;

    private static final boolean MAINTAIN_ASPECT_RATIO = true;
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    private final RecentFilesManager recentsManager = new RecentFilesManager();

    private void buildRecentsMenu() {
        // Clear existing items from the ui collection
        mnuRecents.getItems().clear();
        // if no recents, show disabled item
        if (recentsManager.isEmpty()) {
            MenuItem none = new MenuItem("(No recent files)");
            none.setDisable(true);
            mnuRecents.getItems().add(none);
        } else {
            // populate ui collection with recent files menu items
            for (String path : recentsManager.getRecents()) {
                MenuItem mi = new MenuItem(Paths.get(path).getFileName().toString());
                mi.setOnAction(e -> openImage(path));
                mi.setText(path);
                mnuRecents.getItems().add(mi);
            }
            mnuRecents.getItems().add(new SeparatorMenuItem());
            MenuItem clear = new MenuItem("Clear Recent");
            clear.setOnAction(e -> {
                recentsManager.clear();
                buildRecentsMenu();
            });
            mnuRecents.getItems().add(clear);
        }
    }

    @FXML
    private void initialize() {

        recentsManager.load();
        recentsManager.getRecents().addListener((ListChangeListener<String>) ch -> buildRecentsMenu());
        buildRecentsMenu();

        // Bind its size to the pane and redraw on resize.
        canvas = new Canvas(Math.max(800, canvasPane.getPrefWidth()), Math.max(600, canvasPane.getPrefHeight()));

        // Bind canvas size to pane size so it resizes with the window
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        // Redraw whenever size changes
        canvas.widthProperty().addListener((obs, oldV, newV) -> draw());
        canvas.heightProperty().addListener((obs, oldV, newV) -> draw());

        // add canvas to canvas pane
        canvasPane.getChildren().add(canvas);

        // set the graphics context 2d for later use
        gc = canvas.getGraphicsContext2D();

        // draw the initial canvas image
        drawCheckerBoard();
    }

    private void drawCheckerBoard() {
        // Clear the canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw a checkerboard pattern
        double cellSize = 10.0;
        int rows = (int) Math.ceil(canvas.getHeight() / cellSize);
        int cols = (int) Math.ceil(canvas.getWidth() / cellSize);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if ((row + col) % 2 == 0) {
                    gc.setFill(Color.LIGHTGRAY);
                } else {
                    gc.setFill(Color.WHITE);
                }
                gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
            }
        }
    }

    private void drawImage() {
        if (loadedImage == null) {
            return;
        }

        Image fxImage = loadedImage;
        double canvasW = canvas.getWidth();
        double canvasH = canvas.getHeight();
        double imgW = fxImage.getWidth();
        double imgH = fxImage.getHeight();

        if (imgW > 0 && imgH > 0) {
            double scale = Math.min(canvasW / imgW, canvasH / imgH);
            double drawW = imgW * scale;
            double drawH = imgH * scale;
            double drawX = (canvasW - drawW) / 2.0;
            double drawY = (canvasH - drawH) / 2.0;
            gc.drawImage(fxImage, drawX, drawY, drawW, drawH);
        } else {
            gc.drawImage(fxImage, 0, 0);
        }
        if (!MAINTAIN_ASPECT_RATIO) {
            // stretch to fill the canvas
            gc.drawImage(fxImage, 0, 0, canvasW, canvasH);
            return;
        }

        if (imgW > 0 && imgH > 0) {
            double scale = Math.min(canvasW / imgW, canvasH / imgH);
            double drawW = imgW * scale;
            double drawH = imgH * scale;
            double drawX = (canvasW - drawW) / 2.0;
            double drawY = (canvasH - drawH) / 2.0;
            gc.drawImage(fxImage, drawX, drawY, drawW, drawH);
        } else {
            gc.drawImage(fxImage, 0, 0);
        }
    }

    private void draw() {
        drawCheckerBoard();
        // Draw loaded image (if any), scaled to fit canvas while preserving aspect ratio and centered
        if (loadedImage == null) {
            return;
        }
        drawImage();
    }

    private void updateTitle() {
        String title = "PixelCraft - Image Editor";
        if (currentFile != null) {
            title += " - " + currentFile.getName();
        }
        if (isModified) {
            title += " *";
        }
        // stage.setTitle(title);
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            return name.substring(lastDot + 1).toLowerCase();
        }
        return "png"; // default
    }

    @FXML
    private void onOpenImage(ActionEvent event) {
        openImage();
    }

    private void openImage(String... filePath) {
        File file;

        if (filePath.length > 0) {
            file = new File(filePath[0]);
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            fileChooser.getExtensionFilters()
                    .addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
            file = fileChooser.showOpenDialog(canvasPane.getScene().getWindow());
        }

        if (file == null) {
            return;
        }

        loadImageFile(file);

        recentsManager.add(file.getAbsolutePath());
        buildRecentsMenu();
    }

    private void loadImageFile(File file) {

        currentFile = file;
        isModified = false;
        // Load as JavaFX Image (no java.desktop / AWT required)
        Image img = new Image(file.toURI().toString());
        if (!img.isError()) {
            loadedImage = img;
            draw();
        } else {
            LOGGER.log(Level.SEVERE, "Unable to read image: {0}", file);
        }
    }

    @FXML
    private void onSaveImage(ActionEvent event) {
        //TODO: Implement saving the current canvas image to the opened file
    }

    @FXML
    private void onSaveAsImage(ActionEvent event) {
        //TODO: Implement saving the current canvas image to a new file
    }

    @FXML
    private void onNewImage(ActionEvent event) {
        Dialog<javafx.util.Pair<String, String>> dlg = new Dialog<>();
        dlg.setTitle("New Image");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField widthField = new TextField("800");
        TextField heightField = new TextField("640");

        grid.add(new javafx.scene.control.Label("Width:"), 0, 0);
        grid.add(widthField, 1, 0);
        grid.add(new javafx.scene.control.Label("Height:"), 0, 1);
        grid.add(heightField, 1, 1);

        dlg.getDialogPane().setContent(grid);

        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) return new javafx.util.Pair<>(widthField.getText(), heightField.getText());
            return null;
        });

        java.util.Optional<javafx.util.Pair<String, String>> res = dlg.showAndWait();
        if (!res.isPresent()) return;

        try {
            int w = Integer.parseInt(res.get().getKey());
            int h = Integer.parseInt(res.get().getValue());
            if (w <= 0 || h <= 0) throw new NumberFormatException();

            // Create temporary canvas, fill background and snapshot to an image
            Canvas tmp = new Canvas(w, h);
            GraphicsContext gtmp = tmp.getGraphicsContext2D();
            gtmp.setFill(Color.WHITE);
            gtmp.fillRect(0, 0, w, h);

            javafx.scene.image.WritableImage newImg = new javafx.scene.image.WritableImage(w, h);
            tmp.snapshot(null, newImg);

            // Adopt the new image into the controller state and redraw
            loadedImage = newImg;
            currentFile = null;       // new unsaved image
            isModified = true;        // mark as modified (unsaved)
            draw();
            updateTitle();
        } catch (NumberFormatException ex) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Invalid dimensions. Please enter positive integers for width and height.");
            alert.showAndWait();
        }
    }

    @FXML
    private void onRecents(ActionEvent event) {
        buildRecentsMenu();
    }

    @FXML
    private void onUndo(ActionEvent event) {
        // TODO: Implement undo functionality
    }

    @FXML
    private void onRedo(ActionEvent event) {
        // TODO: Implement redo functionality
    }

    @FXML
    private void onZoomIn(ActionEvent event) {
    }

    @FXML
    private void onZoomOut(ActionEvent event) {
    }

    @FXML
    private void onZoomReset(ActionEvent event) {
    }

    @FXML
    private void onZoomFitWindow(ActionEvent event) {
    }

    @FXML
    private void onExit(ActionEvent event) {
        Stage stage = (Stage) canvasPane.getScene().getWindow();

        // TODO: Check for unsaved changes here (not implemented yet)
        stage.close();
    }

    @FXML
    private void onGenericClick(ActionEvent event) {
    }
}
