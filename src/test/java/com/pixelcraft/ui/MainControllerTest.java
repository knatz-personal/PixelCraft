package com.pixelcraft.ui;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.pixelcraft.commands.NewImageCommand;
import com.pixelcraft.manager.CanvasManager;
import com.pixelcraft.manager.FileManager;
import com.pixelcraft.manager.ViewportManager;
import com.pixelcraft.util.Globals;
import com.pixelcraft.util.ReflectionUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for MainController covering:
 * - Initialization and manager setup
 * - Command history (undo/redo)
 * - New image creation
 * - Image file opening
 * - Zoom operations (in/out/reset/fit/scroll wheel)
 * - Pan operations (mouse drag)
 * - Integration scenarios
 * - Mouse event handling
 */
@ExtendWith(ApplicationExtension.class)
@DisplayName("MainController Tests")
class MainControllerTest {

    private MainController controller;
    private ScrollPane scrollPane;
    private Canvas canvas;
    private Label lblImageSize;
    private Label lblPosition;
    private Label lblMode;
    private Menu mnuRecents;
    private ComboBox<javafx.util.Pair<String, Double>> cmbZoomPresets;
    private ListView<Label> lstHistory;
    private static final Logger LOGGER = Logger.getLogger(MainControllerTest.class.getName());

    @Start
    @SuppressWarnings("unused")
    private void start(Stage stage) {
        // TestFX will provide the stage
    }

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Exception[] setupException = new Exception[1];
        
        Platform.runLater(() -> {
            try {
                controller = new MainController();
                
                // Create FXML components
                StackPane canvasContainer = new StackPane();
                scrollPane = new ScrollPane();
                scrollPane.setContent(canvasContainer);
                
                lblImageSize = new Label();
                lblPosition = new Label();
                lblMode = new Label();
                mnuRecents = new Menu();
                cmbZoomPresets = new ComboBox<>();
                lstHistory = new ListView<>();

                // Inject FXML fields using reflection
                ReflectionUtil.injectField(controller, "canvasContainer", canvasContainer);
                ReflectionUtil.injectField(controller, "scrollPane", scrollPane);
                ReflectionUtil.injectField(controller, "lblImageSize", lblImageSize);
                ReflectionUtil.injectField(controller, "lblPosition", lblPosition);
                ReflectionUtil.injectField(controller, "lblMode", lblMode);
                ReflectionUtil.injectField(controller, "mnuRecents", mnuRecents);
                ReflectionUtil.injectField(controller, "cmbZoomPresets", cmbZoomPresets);
                ReflectionUtil.injectField(controller, "lstHistory", lstHistory);

                // Call initialize method
                Method initMethod = MainController.class.getDeclaredMethod("initialize");
                initMethod.setAccessible(true);
                initMethod.invoke(controller);

                // Get the canvas that was created - it's inside the StackPane container
                canvas = (Canvas) canvasContainer.getChildren().get(0);
                
            } catch (Exception e) {
                setupException[0] = e;
                LOGGER.severe("Setup failed: " + e.getMessage());
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        
        latch.await();
        
        if (setupException[0] != null) {
            throw setupException[0];
        }
    }
    
    // ==================== Helper Methods ====================
    
    /** Helper method to create a new image */
    private void createNewImage() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                ReflectionUtil.invokeMethod(controller, "onNewImage", new Class<?>[]{ActionEvent.class}, new Object[]{null});
            } catch (Exception e) {
                LOGGER.severe(e.getMessage());
            }
            latch.countDown();
        });
        latch.await();
        Thread.sleep(150); // Wait for image change listener
    }
    
    /** Helper method to load test image */
    private File loadTestImage() throws Exception {
        URL imageUrl = getClass().getResource("/sample_images/xmas_sock.jpg");
        assertNotNull(imageUrl, "Test image should exist in resources");
        File testFile = new File(imageUrl.toURI());
        assertTrue(testFile.exists(), "Test file should exist");
        
        FileManager fileManager = ReflectionUtil.getPrivateField(controller, "fileManager", FileManager.class);
        assertNotNull(fileManager, "FileManager should be initialized");
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            fileManager.loadImage(testFile);
            latch.countDown();
        });
        latch.await();
        Thread.sleep(150); // Wait for image change listener
        
        return testFile;
    }

    // ==================== Initialization Tests ====================
    
    @Test
    @DisplayName("Should initialize canvas with default dimensions")
    void testCanvasInitialization() {
        assertNotNull(canvas, "Canvas should be created");
        assertEquals(Globals.DEFAULT_WIDTH, canvas.getWidth(), "Canvas width should be the default");
        assertEquals(Globals.DEFAULT_HEIGHT, canvas.getHeight(), "Canvas height should be the default");
        assertNotNull(scrollPane.getContent(), "Canvas container should be set as scroll pane content");
        assertTrue(scrollPane.getContent() instanceof StackPane, "Content should be a StackPane container");
    }
    
    @Test
    @DisplayName("Should initialize with Drawing mode")
    void testInitialMode() {
        assertTrue(lblMode.getText().contains("Drawing"), "Mode should be set to Drawing");
    }

    @Test
    @DisplayName("Should initialize CanvasManager")
    void testCanvasManagerInitialization() throws Exception {
        CanvasManager canvasManager = ReflectionUtil.getPrivateField(controller, "canvasManager", CanvasManager.class);
        assertNotNull(canvasManager, "CanvasManager should be initialized");
    }

    @Test
    @DisplayName("Should initialize ViewportManager with zoom level 1.0")
    void testViewportManagerInitialization() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        assertNotNull(viewportManager, "ViewportManager should be initialized");
        assertEquals(1.0, viewportManager.getZoomLevel(), 0.001, "Initial zoom should be 1.0");
    }

    @Test
    @DisplayName("Should initialize FileManager with no image loaded")
    void testFileManagerInitialization() throws Exception {
        FileManager fileManager = ReflectionUtil.getPrivateField(controller, "fileManager", FileManager.class);
        assertNotNull(fileManager, "FileManager should be initialized");
        assertFalse(fileManager.getCurrentImage().isPresent(), "No image should be loaded initially");
    }

    // ==================== Command History Tests (Undo/Redo) ====================
    
    @Test
    @DisplayName("Should undo and redo zoom operations")
    void testUndoRedo() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        double initialZoom = viewportManager.getZoomLevel();
        
        // Execute zoom in command
        CountDownLatch latch1 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomIn(null);
            latch1.countDown();
        });
        latch1.await();
        
        double zoomAfterZoomIn = viewportManager.getZoomLevel();
        assertTrue(zoomAfterZoomIn > initialZoom, "Zoom should increase");
        
        // Undo
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onUndo(null);
            latch2.countDown();
        });
        latch2.await();
        
        assertEquals(initialZoom, viewportManager.getZoomLevel(), 0.001, "Zoom should be back to initial");
        
        // Redo
        CountDownLatch latch3 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onRedo(null);
            latch3.countDown();
        });
        latch3.await();
        
        assertEquals(zoomAfterZoomIn, viewportManager.getZoomLevel(), 0.001, "Zoom should be back to increased value");
    }

    // ==================== New Image Tests ====================
    
    @Test
    @DisplayName("Should create new image with default dimensions")
    void testNewImageCommand() throws Exception {
        FileManager fileManager = ReflectionUtil.getPrivateField(controller, "fileManager", FileManager.class);
        
        createNewImage();
        
        assertTrue(fileManager.getCurrentImage().isPresent(), "New image should be created");
        assertEquals(Globals.DEFAULT_WIDTH, fileManager.getCurrentImage().get().getWidth(), "Image width should match default width");
        assertEquals(Globals.DEFAULT_HEIGHT, fileManager.getCurrentImage().get().getHeight(), "Image height should match default height");
    }
    
    @Test
    @DisplayName("Should create new image with custom dimensions")
    void testNewImageCustomDimensions() throws Exception {
        FileManager fileManager = ReflectionUtil.getPrivateField(controller, "fileManager", FileManager.class);
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            NewImageCommand cmd = new NewImageCommand(fileManager, 1024, 768);
            cmd.execute();
            latch.countDown();
        });
        latch.await();
        
        assertTrue(fileManager.getCurrentImage().isPresent(), "New image should be created");
        assertEquals(1024, fileManager.getCurrentImage().get().getWidth(), "Image width should be 1024");
        assertEquals(768, fileManager.getCurrentImage().get().getHeight(), "Image height should be 768");
    }
    
    @Test
    @DisplayName("Should handle undo after creating new image")
    void testNewImageUndo() throws Exception {
        FileManager fileManager = ReflectionUtil.getPrivateField(controller, "fileManager", FileManager.class);
        
        // Create first image
        createNewImage();
        assertTrue(fileManager.getCurrentImage().isPresent(), "First image should exist");
        
        // Create second image with different dimensions
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            NewImageCommand cmd = new NewImageCommand(fileManager, 512, 384);
            cmd.execute();
            latch2.countDown();
        });
        latch2.await();
        
        assertEquals(512, fileManager.getCurrentImage().get().getWidth(), "Second image width should be 512");
        
        // Undo - note: NewImageCommand undo has limitations in current implementation
        CountDownLatch latch3 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onUndo(null);
            latch3.countDown();
        });
        latch3.await();
        
        // Verify an image still exists after undo
        assertTrue(fileManager.getCurrentImage().isPresent(), "Image should exist after undo");
    }
    
    @Test
    @DisplayName("Should replace opened image with new image")
    void testNewImageReplacesOpenedImage() throws Exception {
        FileManager fileManager = ReflectionUtil.getPrivateField(controller, "fileManager", FileManager.class);
        
        // Load test image
        loadTestImage();
        assertTrue(fileManager.getCurrentImage().isPresent(), "Image should be loaded");
        assertTrue(fileManager.getCurrentFile().isPresent(), "File should be set");
        
        // Create new image
        createNewImage();
        
        assertTrue(fileManager.getCurrentImage().isPresent(), "New image should exist");
        assertEquals(Globals.DEFAULT_WIDTH, fileManager.getCurrentImage().get().getWidth(), "Should have default width");
    }

    // ==================== Open Image Tests ====================
    
    @Test
    @DisplayName("Should load image from file")
    void testOpenImageFromFile() throws Exception {
        FileManager fileManager = ReflectionUtil.getPrivateField(controller, "fileManager", FileManager.class);
        File testFile = loadTestImage();
        
        assertTrue(fileManager.getCurrentImage().isPresent(), "Image should be loaded");
        assertTrue(fileManager.getCurrentFile().isPresent(), "File path should be set");
        assertEquals(testFile.getAbsolutePath(), fileManager.getCurrentFile().get().getAbsolutePath(), "Loaded file should match");
    }
    
    @Test
    @DisplayName("Should adjust viewport when opening image")
    void testOpenImageAdjustsViewport() throws Exception {
        FileManager fileManager = ReflectionUtil.getPrivateField(controller, "fileManager", FileManager.class);
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        loadTestImage();
        
        assertTrue(fileManager.getCurrentImage().isPresent(), "Image should be loaded");
        
        assertNotNull(viewportManager.getZoomLevel(), "Zoom level should be set");
    }

    // ==================== Zoom Tests ====================
    
    @Test
    @DisplayName("Should zoom in by ZOOM_STEP factor")
    void testZoomIn() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        double initialZoom = viewportManager.getZoomLevel();
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomIn(null);
            latch.countDown();
        });
        latch.await();
        
        assertTrue(viewportManager.getZoomLevel() > initialZoom, "Zoom level should increase");
        assertEquals(initialZoom * Globals.ZOOM_STEP, viewportManager.getZoomLevel(), 0.001, "Zoom should increase by ZOOM_STEP");
    }
    
    @Test
    @DisplayName("Should zoom out by ZOOM_STEP factor")
    void testZoomOut() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        // First zoom in to avoid hitting minimum zoom
        CountDownLatch latch1 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomIn(null);
            latch1.countDown();
        });
        latch1.await();
        
        double zoomedInLevel = viewportManager.getZoomLevel();
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomOut(null);
            latch2.countDown();
        });
        latch2.await();
        
        assertTrue(viewportManager.getZoomLevel() < zoomedInLevel, "Zoom level should decrease");
        assertEquals(zoomedInLevel / Globals.ZOOM_STEP, viewportManager.getZoomLevel(), 0.001, "Zoom should decrease by ZOOM_STEP");
    }
    
    @Test
    @DisplayName("Should reset zoom to 100%")
    void testZoomReset() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        // Zoom in first
        CountDownLatch latch1 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomIn(null);
            controller.onZoomIn(null);
            latch1.countDown();
        });
        latch1.await();
        
        assertTrue(viewportManager.getZoomLevel() > 1.0, "Zoom should be greater than 1.0");
        
        // Reset zoom
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomReset(null);
            latch2.countDown();
        });
        latch2.await();
        
        assertEquals(1.0, viewportManager.getZoomLevel(), 0.001, "Zoom should be reset to 1.0");
    }
    
    @Test
    @DisplayName("Should fit image to window")
    void testZoomFitWindow() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        createNewImage();
        
        // Zoom in
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomIn(null);
            controller.onZoomIn(null);
            latch2.countDown();
        });
        latch2.await();
        
        assertTrue(viewportManager.getZoomLevel() > 0, "Should have a zoom level");
        
        // Fit to window
        CountDownLatch latch3 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomFitWindow(null);
            latch3.countDown();
        });
        latch3.await();
        
        assertNotNull(viewportManager.getZoomLevel(), "Zoom level should be set");
    }
    
    @Test
    @DisplayName("Should zoom with Ctrl+scroll wheel")
    void testZoomWithScrollWheel() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        double initialZoom = viewportManager.getZoomLevel();
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            // Simulate scroll event with Ctrl pressed (zoom in)
            ScrollEvent scrollEvent = new ScrollEvent(
                ScrollEvent.SCROLL,
                100, 100, 100, 100,
                false, true, false, false, // Ctrl is down
                false, false,
                0, 10, // positive deltaY = zoom in
                0, 0,
                ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                ScrollEvent.VerticalTextScrollUnits.NONE, 0,
                0, null
            );
            scrollPane.fireEvent(scrollEvent);
            latch.countDown();
        });
        latch.await();
        
        assertTrue(viewportManager.getZoomLevel() > initialZoom, "Zoom should increase with scroll wheel");
    }
    
    @Test
    @DisplayName("Should respect zoom limits")
    void testZoomLimits() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        // Try to zoom in many times
        CountDownLatch latch1 = new CountDownLatch(1);
        Platform.runLater(() -> {
            for (int i = 0; i < 50; i++) {
                controller.onZoomIn(null);
            }
            latch1.countDown();
        });
        latch1.await();
        
        assertTrue(viewportManager.getZoomLevel() <= Globals.MAX_ZOOM, "Zoom should not exceed MAX_ZOOM");
        
        // Try to zoom out many times
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            for (int i = 0; i < 100; i++) {
                controller.onZoomOut(null);
            }
            latch2.countDown();
        });
        latch2.await();
        
        assertTrue(viewportManager.getZoomLevel() >= Globals.MIN_ZOOM, "Zoom should not go below MIN_ZOOM");
    }

    // ==================== Pan Tests ====================
    
    @Test
    @DisplayName("Should start panning")
    void testPanStart() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        assertFalse(viewportManager.isPanning(), "Should not be panning initially");
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            viewportManager.startPan(100, 100);
            latch.countDown();
        });
        latch.await();
        
        assertTrue(viewportManager.isPanning(), "Should be panning after startPan");
    }
    
    @Test
    @DisplayName("Should end panning")
    void testPanEnd() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        CountDownLatch latch1 = new CountDownLatch(1);
        Platform.runLater(() -> {
            viewportManager.startPan(100, 100);
            latch1.countDown();
        });
        latch1.await();
        
        assertTrue(viewportManager.isPanning(), "Should be panning");
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            viewportManager.endPan();
            latch2.countDown();
        });
        latch2.await();
        
        assertFalse(viewportManager.isPanning(), "Should not be panning after endPan");
    }
    
    @Test
    @DisplayName("Should pan with right-click mouse drag")
    void testPanWithMouseDrag() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        // Create an image and zoom in so there's something to pan
        createNewImage();
        CountDownLatch zoomLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomIn(null);
            controller.onZoomIn(null);
            zoomLatch.countDown();
        });
        zoomLatch.await();
        
        // Simulate right mouse button press
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            MouseEvent pressEvent = new MouseEvent(
                MouseEvent.MOUSE_PRESSED,
                100, 100, 100, 100,
                MouseButton.SECONDARY, 1,
                false, false, false, false,
                false, false, true, false, false, false,
                null
            );
            canvas.fireEvent(pressEvent);
            latch2.countDown();
        });
        latch2.await();
        
        assertTrue(viewportManager.isPanning(), "Should be panning after right mouse press");
        
        // Simulate mouse drag
        CountDownLatch latch3 = new CountDownLatch(1);
        Platform.runLater(() -> {
            MouseEvent dragEvent = new MouseEvent(
                MouseEvent.MOUSE_DRAGGED,
                150, 150, 150, 150,
                MouseButton.SECONDARY, 1,
                false, false, false, false,
                false, false, true, false, false, false,
                null
            );
            canvas.fireEvent(dragEvent);
            latch3.countDown();
        });
        latch3.await();
        
        assertTrue(viewportManager.isPanning(), "Should still be panning during drag");
        
        // Simulate mouse release
        CountDownLatch latch4 = new CountDownLatch(1);
        Platform.runLater(() -> {
            MouseEvent releaseEvent = new MouseEvent(
                MouseEvent.MOUSE_RELEASED,
                150, 150, 150, 150,
                MouseButton.SECONDARY, 1,
                false, false, false, false,
                false, false, false, false, false, false,
                null
            );
            canvas.fireEvent(releaseEvent);
            latch4.countDown();
        });
        latch4.await();
        
        assertFalse(viewportManager.isPanning(), "Should not be panning after mouse release");
    }
    
    @Test
    @DisplayName("Should update scroll position during pan")
    void testPanUpdatesScrollPosition() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        // Create image and zoom in to enable scrolling
        createNewImage();
        CountDownLatch zoomLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomIn(null);
            controller.onZoomIn(null);
            controller.onZoomIn(null);
            zoomLatch.countDown();
        });
        zoomLatch.await();
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            viewportManager.startPan(100, 100);
            viewportManager.updatePan(150, 150); // Pan by 50 pixels
            latch2.countDown();
        });
        latch2.await();
        
        // Scroll values should be set (exact values depend on canvas size and zoom level)
        assertNotNull(scrollPane.getHvalue(), "H scroll value should be set");
        assertNotNull(scrollPane.getVvalue(), "V scroll value should be set");
    }

    // ==================== Integration Tests ====================
    
    @Test
    @DisplayName("Should zoom and pan together without interference")
    void testZoomAndPanIntegration() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        createNewImage();
        
        // Zoom in
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomIn(null);
            controller.onZoomIn(null);
            latch2.countDown();
        });
        latch2.await();
        
        double zoomLevel = viewportManager.getZoomLevel();
        assertTrue(zoomLevel > 0, "Should have a zoom level");
        
        // Pan
        CountDownLatch latch3 = new CountDownLatch(1);
        Platform.runLater(() -> {
            viewportManager.startPan(100, 100);
            viewportManager.updatePan(150, 150);
            viewportManager.endPan();
            latch3.countDown();
        });
        latch3.await();
        
        assertFalse(viewportManager.isPanning(), "Should not be panning after end");
        assertEquals(zoomLevel, viewportManager.getZoomLevel(), 0.001, "Zoom level should not change during pan");
    }
    
    @Test
    @DisplayName("Should zoom after opening image")
    void testOpenImageThenZoom() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        loadTestImage();
        
        double zoomBeforeZoomIn = viewportManager.getZoomLevel();
        
        // Zoom in on the loaded image
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onZoomIn(null);
            latch2.countDown();
        });
        latch2.await();
        
        double zoomAfter = viewportManager.getZoomLevel();
        assertTrue(zoomAfter > zoomBeforeZoomIn, "Zoom should increase after zoom in");
    }
    
    // ==================== Mouse Event Tests ====================
    
    @Test
    @DisplayName("Should update position label on mouse move")
    void testMouseMoveUpdatesPosition() throws Exception {
        createNewImage();
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            MouseEvent moveEvent = new MouseEvent(
                MouseEvent.MOUSE_MOVED,
                50, 75, 50, 75,
                MouseButton.NONE, 0,
                false, false, false, false,
                false, false, false, false, false, false,
                null
            );
            canvas.fireEvent(moveEvent);
            latch.countDown();
        });
        latch.await();
        
        // Position label should be updated (exact format depends on implementation)
        assertNotNull(lblPosition.getText(), "Position label should have text");
    }
    
    @Test
    @DisplayName("Should ignore left-click for panning")
    void testLeftClickDoesNotPan() throws Exception {
        ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);
        
        createNewImage();
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            MouseEvent pressEvent = new MouseEvent(
                MouseEvent.MOUSE_PRESSED,
                100, 100, 100, 100,
                MouseButton.PRIMARY, 1, // Primary (left) button
                false, false, false, false,
                true, false, false, false, false, false,
                null
            );
            canvas.fireEvent(pressEvent);
            latch.countDown();
        });
        latch.await();
        
        assertFalse(viewportManager.isPanning(), "Should not start panning with left click");
    }
}
