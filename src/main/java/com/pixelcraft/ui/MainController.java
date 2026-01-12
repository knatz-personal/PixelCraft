package com.pixelcraft.ui;

import java.io.File;
import java.util.Optional;

import com.pixelcraft.builders.RecentsMenuBuilder;
import com.pixelcraft.commands.CommandHistory;
import com.pixelcraft.commands.ICommand;
import com.pixelcraft.commands.NewImageCommand;
import com.pixelcraft.commands.OpenImageCommand;
import com.pixelcraft.commands.OpenRecentCommand;
import com.pixelcraft.commands.ZoomFitToViewportCommand;
import com.pixelcraft.commands.ZoomInCommand;
import com.pixelcraft.commands.ZoomOutCommand;
import com.pixelcraft.commands.ZoomResetCommand;
import com.pixelcraft.commands.ZoomSetCommand;
import com.pixelcraft.event.IImageChangeListener;
import com.pixelcraft.event.IViewportChangeListener;
import com.pixelcraft.manager.CanvasManager;
import com.pixelcraft.manager.FileManager;
import com.pixelcraft.manager.HistoryDisplayManager;
import com.pixelcraft.manager.KeyboardShortcutManager;
import com.pixelcraft.manager.RecentFilesManager;
import com.pixelcraft.manager.StatusBarManager;
import com.pixelcraft.manager.ViewportManager;
import com.pixelcraft.model.RasterImage;
import com.pixelcraft.util.Globals;
import com.pixelcraft.util.IconUtil;
import com.pixelcraft.util.logging.Logger;
import com.pixelcraft.util.logging.ObservableListLogTarget;
import com.pixelcraft.util.logging.eLogLevel;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

public class MainController {

    //#region FXM Refs
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private StackPane canvasContainer;
    @FXML
    private Label lblImageSize;
    @FXML
    private Label lblFileSize;
    @FXML
    private Label lblPosition;
    @FXML
    private Label lblMode;
    @FXML
    private Menu mnuRecents;
    @FXML
    private ComboBox<Pair<String, Double>> cmbZoomPresets;
    @FXML
    private ListView<Label> lstHistory;
    @FXML
    private ListView<String> lstActivityLog;
    //#endregion

    private Canvas canvas;
    private boolean keyboardShortcutsSetup = false;
    private boolean isApplyingZoom = false;

    //#region Managers
    private final CommandHistory commandHistory = new CommandHistory();
    private final RecentFilesManager recentsManager = new RecentFilesManager();
    private final PauseTransition zoomDebouncer = new PauseTransition(Duration.millis(100));
    private Logger logger;
    private CanvasManager canvasManager;
    private ViewportManager viewportManager;
    private FileManager fileManager;
    private StatusBarManager statusBarManager;
    private HistoryDisplayManager historyManager;
    //#endregion

    //#region Activity Logging
    private void log(String message) {
        if (logger != null) {
            logger.info(message);
        }
    }
    //#endregion

    //#region FXML events
    @FXML
    @SuppressWarnings("unused")
    public void initialize() {
        log("MainController.initialize() started");
        
        // Load icon font
        IconUtil.loadIconFont();
        
        // init and bind canvas to scroll pane
        canvas = new Canvas(Globals.DEFAULT_WIDTH, Globals.DEFAULT_HEIGHT);

        // Configure the StackPane container
        canvasContainer.setAlignment(Pos.CENTER);
        // Initialize logger with ObservableList target for activity log
        logger = Logger.getLogger(MainController.class)
            .addTarget(new ObservableListLogTarget(lstActivityLog.getItems(), eLogLevel.INFO, 5_000_000, true));
        
        canvasContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        canvasContainer.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        canvasContainer.setStyle("-fx-background-color: transparent;");
        canvasContainer.getChildren().add(canvas);

        // init managers 
        canvasManager = new CanvasManager(canvas);
        viewportManager = new ViewportManager(scrollPane, canvas);
        fileManager = new FileManager();
        statusBarManager = new StatusBarManager(lblImageSize, lblFileSize, lblPosition, lblMode, cmbZoomPresets);
        historyManager = new HistoryDisplayManager(lstHistory, commandHistory);

        // init listeners 
        setupFileListener();
        setupViewportListener();
        setupEventHandlers();
        setupRecentsMenu();

        // Initial render
        canvasManager.drawCheckerboard();
        statusBarManager.updateMode("Drawing");
        log("MainController.initialize() completed");

        // Setup keyboard shortcuts when scene is available
        canvas.sceneProperty().addListener((obs, old, scene) -> {
            if (scene != null) {
                setupKeyboardShortcuts(scene);
            }
        });
    }

    @FXML
    @SuppressWarnings("unused")
    public void onNewImage(ActionEvent event) {
        log("onNewImage() called");
        //TODO: Show dialog to get image dimensions
        ICommand cmd = new NewImageCommand(fileManager, Globals.DEFAULT_WIDTH, Globals.DEFAULT_HEIGHT);
        executeCommand(cmd);
    }

    @FXML
    @SuppressWarnings("unused")
    public void onOpenImage(ActionEvent event) {
        log("onOpenImage() called - creating OpenImageCommand");
        ICommand cmd = new OpenImageCommand(fileManager, canvas);
        executeCommand(cmd);
        log("onOpenImage() - command executed");
    }

    @FXML
    @SuppressWarnings("unused")
    public void onSaveImage(ActionEvent event) {
        //TODO: Implement saving the current canvas image to the opened file
        fileManager.save();
    }

    @FXML
    @SuppressWarnings("unused")
    public void onSaveAsImage(ActionEvent event) {
        //TODO: Implement saving the current canvas image to a new file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image As");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Image", "*.png")
        );

        File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());
        if (file != null) {
            fileManager.saveAs(file);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void onRecents(ActionEvent event) {
        buildRecentsMenu();
    }

    @FXML
    public void onUndo(ActionEvent event) {
        commandHistory.undo();
        historyManager.update();
    }

    @FXML
    public void onRedo(ActionEvent event) {
        commandHistory.redo();
        historyManager.update();
    }

    @FXML
    public void onZoomIn(ActionEvent event) {
        ICommand cmd = new ZoomInCommand(
                scrollPane,
                canvas,
                viewportManager::getZoomLevel,
                viewportManager::setZoom,
                this::updateStatusBar
        );
        executeCommand(cmd);
    }

    @FXML
    public void onZoomOut(ActionEvent event) {
        ICommand cmd = new ZoomOutCommand(
                scrollPane,
                canvas,
                viewportManager::getZoomLevel,
                viewportManager::setZoom,
                this::updateStatusBar
        );
        executeCommand(cmd);
    }

    @FXML
    public void onZoomReset(ActionEvent event) {
        ICommand cmd = new ZoomResetCommand(
                scrollPane,
                canvas,
                viewportManager::getZoomLevel,
                viewportManager::setZoom,
                this::updateStatusBar
        );
        executeCommand(cmd);
    }

    @FXML
    public void onZoomFitWindow(ActionEvent event) {
        ICommand cmd = new ZoomFitToViewportCommand(
                scrollPane,
                canvas,
                viewportManager::getZoomLevel,
                viewportManager::setZoom,
                this::updateStatusBar
        );
        executeCommand(cmd);
    }

    @FXML
    @SuppressWarnings("unused")
    public void onZoomPresetSelected() {
        SingleSelectionModel<Pair<String, Double>> selectionModel = cmbZoomPresets.getSelectionModel();
        if (selectionModel == null) {
            return;
        }
        Pair<String, Double> selected = selectionModel.getSelectedItem();
        if (selected == null) {
            return;
        }
        double zoomValue = selected.getValue();
        ICommand cmd = new ZoomSetCommand(
                scrollPane,
                canvas,
                viewportManager::getZoomLevel,
                viewportManager::setZoom,
                this::updateStatusBar,
                zoomValue
        );
        executeCommand(cmd);
    }

    @FXML
    @SuppressWarnings("unused")
    public void onExit(ActionEvent event) {
        Stage stage = (Stage) canvas.getScene().getWindow();

        // TODO: Check for unsaved changes here
        if (fileManager.isModified()) {
            // Show confirmation dialog
        }

        stage.close();
    }

    @FXML
    @SuppressWarnings("unused")
    public void onGenericClick(ActionEvent event) {
        // Placeholder for generic actions
    }
    //#endregion

    //#region Event Listeners
    private void setupViewportListener() {
        viewportManager.setListener(new IViewportChangeListener() {
            @Override
            public void onZoomChanged(double newZoom) {

                zoomDebouncer.stop();
                zoomDebouncer.setOnFinished(e -> applyZoom());
                zoomDebouncer.playFromStart();

                // Update status bar immediately
                statusBarManager.updateZoom(newZoom);
            }

            @Override
            public void onPanChanged() {
                // Update any pan-related UI if needed
            }
        });
    }

    private void setupEventHandlers() {
        // Track the actual viewport (window) size for resize detection
        // We need to distinguish between:
        // 1. Window resize (should trigger zoomToFit for initial fit)
        // 2. Canvas content resize due to zoom (should NOT trigger zoomToFit)
        final double[] lastViewportSize = {0, 0};
        
        scrollPane.viewportBoundsProperty().addListener((obs, oldB, newB) -> {
            // Skip if we're already applying zoom (prevents re-entrancy)
            if (isApplyingZoom) {
                return;
            }
            
            if (newB == null) return;
            
            double newWidth = newB.getWidth();
            double newHeight = newB.getHeight();
            
            // Only react when the actual viewport (window) dimensions change significantly
            // This happens on window resize, NOT on canvas content changes
            final double EPS = 5.0; // Need larger epsilon to ignore minor layout adjustments
            boolean viewportSizeChanged = Math.abs(newWidth - lastViewportSize[0]) > EPS
                    || Math.abs(newHeight - lastViewportSize[1]) > EPS;
            
            if (!viewportSizeChanged) {
                return;
            }
            
            // Update tracked viewport size
            lastViewportSize[0] = newWidth;
            lastViewportSize[1] = newHeight;
            
            // Don't auto-fit on every resize - only on initial load
            // User manual zoom should be preserved
        });
        scrollPane.setOnScroll(this::handleScroll);
        setupCanvasEventHandlers();
    }

    private void setupCanvasEventHandlers() {
        canvas.setOnMouseMoved(this::handleMouseMove);
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
        canvas.setOnMouseExited(e -> statusBarManager.clearPosition());
    }

    private void setupFileListener() {
        fileManager.setListener(new IImageChangeListener() {
            @Override
            public void onImageChanged(Optional<RasterImage> image) {
                log("onImageChanged() triggered - image present: " + image.isPresent());
                
                // Update file size from actual file when image is loaded
                fileManager.getCurrentFile().ifPresent(file -> {
                    if (file.exists()) {
                        statusBarManager.updateFileSize(file.length());
                    }
                });
                
                image.ifPresent(img -> {
                    log("  Image dimensions: " + img.getWidth() + "x" + img.getHeight() + ", valid: " + img.isValid());
                    // Fit to viewport and render immediately
                    log("  Calling zoomToFit...");
                    viewportManager.zoomToFit(img.getWidth(), img.getHeight());
                    log("  zoomToFit done, zoom level: " + viewportManager.getZoomLevel());
                    log("  Calling applyZoom...");
                    applyZoom();
                    log("  applyZoom done");
                    // Center the canvas
                    Platform.runLater(() -> {
                        log("  Centering canvas (runLater)");
                        centerCanvas();
                    });
                });
            }

            @Override
            public void onFileChanged(Optional<File> file) {
                log("onFileChanged() - file: " + file.map(f -> f.getName()).orElse("none"));
                updateWindowTitle();
                file.ifPresent(f -> {
                    recentsManager.add(f.getAbsolutePath());
                    buildRecentsMenu();
                });
            }

            @Override
            public void onModificationStateChanged(boolean modified) {
                updateWindowTitle();
            }
        });
    }
    //#endregion

    //#region Event Handlers
    private void handleScroll(ScrollEvent event) {
        if (!event.isControlDown()) {
            return;
        }
        event.consume();

        double currentZoom = viewportManager.getZoomLevel();
        double delta = event.getDeltaY() > 0 ? Globals.ZOOM_STEP : (1.0 / Globals.ZOOM_STEP);
        double newZoom = currentZoom * delta;
        
        // Prevent command execution if zoom won't change (at limits)
        double clampedZoom = Math.clamp(newZoom, Globals.MIN_ZOOM, Globals.MAX_ZOOM);
        if (Math.abs(clampedZoom - currentZoom) < 0.0001) {
            return; // Already at limit, don't create command
        }

        ICommand cmd = new ZoomSetCommand(
                scrollPane, canvas,
                viewportManager::getZoomLevel,
                viewportManager::setZoom,
                this::updateStatusBar,
                newZoom
        );
        executeCommand(cmd);
    }

    private void handleMouseMove(MouseEvent event) {
        double imageX = event.getX() / viewportManager.getZoomLevel();
        double imageY = event.getY() / viewportManager.getZoomLevel();
        statusBarManager.updatePosition(imageX, imageY);
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            // Use local mouse coordinates for pan start
            viewportManager.startPan(event.getX(), event.getY());
            canvas.setCursor(Cursor.CLOSED_HAND);
            event.consume();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (viewportManager.isPanning() && event.isSecondaryButtonDown()) {
            // Use local mouse coordinates for pan update
            viewportManager.updatePan(event.getX(), event.getY());
            handleMouseMove(event);
            event.consume();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (viewportManager.isPanning()) {
            viewportManager.endPan();
            canvas.setCursor(Cursor.DEFAULT);
            event.consume();
        }
    }
    //#endregion

    //#region Private Methods
    private void executeCommand(ICommand cmd) {
        commandHistory.execute(cmd);
        historyManager.update();
    }

    private void buildRecentsMenu() {
        RecentsMenuBuilder.build(mnuRecents, recentsManager, this::openRecentFile);
    }

    private void updateWindowTitle() {
        if (canvas.getScene() != null && canvas.getScene().getWindow() != null) {
            Stage stage = (Stage) canvas.getScene().getWindow();
            stage.setTitle(fileManager.getDisplayTitle());
        }
    }

    private void updateStatusBar() {
        statusBarManager.updateZoom(viewportManager.getZoomLevel());
    }

    private void openRecentFile(String path) {
        log("openRecentFile() - path: " + path);
        ICommand cmd = new OpenRecentCommand(fileManager, path);
        executeCommand(cmd);
    }

    private void applyZoom() {
        log("applyZoom() called, isApplyingZoom=" + isApplyingZoom);
        if (isApplyingZoom) {
            log("  SKIPPED - already applying zoom");
            return;
        }
        isApplyingZoom = true;
        
        try {
            fileManager.getCurrentImage().ifPresent(img -> {
                log("  Image found: " + img.getWidth() + "x" + img.getHeight() + ", valid=" + img.isValid());
                // Validate image is valid
                if (!img.isValid() || img.getWidth() <= 0 || img.getHeight() <= 0) {
                    log("  SKIPPED - image invalid");
                    return;
                }

                // Unbind canvas from pane temporarily
                canvas.widthProperty().unbind();
                canvas.heightProperty().unbind();

                double zoomLevel = viewportManager.getZoomLevel();
                double imageWidth = img.getWidth();
                double imageHeight = img.getHeight();
                log("  zoomLevel=" + zoomLevel + ", imageSize=" + imageWidth + "x" + imageHeight);
                statusBarManager.updateImageSize(imageWidth, imageHeight);
                
                // Calculate the scaled canvas size
                double scaledWidth = imageWidth * zoomLevel;
                double scaledHeight = imageHeight * zoomLevel;
                
                // CRITICAL: Clamp the canvas size to prevent GPU texture overflow
                // JavaFX Canvas allocates a GPU texture matching its size - too big = NPE
                double maxCanvasSize = Math.min(Globals.MAX_TEXTURE_SIZE, 4096.0); // Conservative limit
                if (scaledWidth > maxCanvasSize || scaledHeight > maxCanvasSize) {
                    double scale = maxCanvasSize / Math.max(scaledWidth, scaledHeight);
                    scaledWidth *= scale;
                    scaledHeight *= scale;
                    log("  Canvas clamped to: " + scaledWidth + "x" + scaledHeight);
                }

                // Reset scale transforms - we don't use them anymore
                canvas.setScaleX(1.0);
                canvas.setScaleY(1.0);

                if (Double.isFinite(scaledWidth) && Double.isFinite(scaledHeight)
                        && scaledWidth > 0 && scaledHeight > 0) {
                    log("  Setting canvas size to: " + scaledWidth + "x" + scaledHeight);
                    
                    // Clear the canvas BEFORE resizing to prevent flicker
                    // (old content getting stretched to new size)
                    canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    
                    // Set canvas to the scaled size (clamped)
                    canvas.setWidth(scaledWidth);
                    canvas.setHeight(scaledHeight);

                    // Update container size for proper scrolling
                    log("  Container size: " + scaledWidth + "x" + scaledHeight);
                    canvasContainer.setMinSize(scaledWidth, scaledHeight);
                    canvasContainer.setPrefSize(scaledWidth, scaledHeight);

                    log("  Calling canvasManager.render()...");
                    canvasManager.renderScaled(img, scaledWidth, scaledHeight);
                    log("  Render complete");
                } else {
                    log("  SKIPPED - dimensions out of bounds");
                }
            });
        } finally {
            isApplyingZoom = false;
            log("applyZoom() finished");
        }
    }

    private void centerCanvas() {
        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);
    }

    private void setupRecentsMenu() {
        recentsManager.load();
        recentsManager.getRecents().addListener(
                (ListChangeListener<String>) ch -> buildRecentsMenu()
        );
        buildRecentsMenu();
    }

    private void setupKeyboardShortcuts(Scene scene) {
        if (keyboardShortcutsSetup) {
            return;
        }
        keyboardShortcutsSetup = true;

        KeyboardShortcutManager.setup(scene, this);
    }

    //#endregion
}
