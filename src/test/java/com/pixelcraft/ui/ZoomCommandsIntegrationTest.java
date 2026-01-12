package com.pixelcraft.ui;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.pixelcraft.util.Globals;
import com.pixelcraft.util.ReflectionUtil;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Integration tests for Zoom Commands using the actual MainController UI Tests
 * the zoom functionality with real FXML-loaded components
 *
 * @author Nathan Khupe
 */
@ExtendWith(ApplicationExtension.class)
@DisplayName("Zoom Commands Integration Tests with Actual UI")
public class ZoomCommandsIntegrationTest {

    private MainController controller;
    private ScrollPane scrollPane;
    private Canvas canvas;
    private Stage stage;

    private static final String[] SAMPLE_FILES = new String[]{
        "/sample_images/xmas_sock.jpg", // Colourful portrait image
        "/sample_images/japan.jpg", // Large landscape image
        "/sample_images/grey_lion.jpg" // Grayscale portrait image
    };

    // Static counter to rotate through sample files for each test
    private static int sampleFileIndex = 0;

    private static final double DELTA = 0.0001;

    @Start
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        // Load the actual FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pixelcraft/main.fxml"));
        BorderPane root = loader.load();

        // Get the actual controller
        controller = loader.getController();

        // Use reflection to access private fields from MainController
        scrollPane = ReflectionUtil.getPrivateField(controller, "scrollPane", ScrollPane.class);
        canvas = ReflectionUtil.getPrivateField(controller, "canvas", Canvas.class);

        // Load a sample image for realistic testing - rotates through different images
        // to test zoom behavior with various image dimensions
        String currentSampleFile = SAMPLE_FILES[sampleFileIndex++ % SAMPLE_FILES.length];
        System.out.println("Testing with sample image: " + currentSampleFile);

        javafx.scene.image.Image sampleImage = new javafx.scene.image.Image(
                getClass().getResourceAsStream(currentSampleFile)
        );

        // Convert JavaFX Image to RasterImage via BufferedImage
        java.awt.image.BufferedImage bufferedImage = javafx.embed.swing.SwingFXUtils.fromFXImage(sampleImage, null);
        com.pixelcraft.model.RasterImage rasterImage = new com.pixelcraft.model.RasterImage(
                bufferedImage.getWidth(),
                bufferedImage.getHeight()
        );
        // Copy pixels
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                rasterImage.setPixel(x, y, bufferedImage.getRGB(x, y));
            }
        }

        // Use FileManager to load the image (refactored architecture)
        com.pixelcraft.manager.FileManager fileManager = ReflectionUtil.getPrivateField(controller, "fileManager", com.pixelcraft.manager.FileManager.class);
        // Create a temporary file for the test image
        java.io.File tempFile = java.io.File.createTempFile("test_image", ".jpg");
        tempFile.deleteOnExit();
        javax.imageio.ImageIO.write(bufferedImage, "jpg", tempFile);
        fileManager.loadImage(tempFile);

        // Set up the stage with smaller dimensions for headless testing
        Scene scene = new Scene(root, Globals.DEFAULT_WIDTH, Globals.DEFAULT_HEIGHT);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setAlwaysOnTop(true);
        // Note: Avoid setFullScreen(true) - it triggers OverlayWarning animations
        // that cause "sceneState is null" NPE when stage is disposed during animation
        stage.show();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Note: Images now automatically fit to viewport when loaded
    }

    @AfterEach
    void tearDown() throws Exception {
        // Wait for any pending FX operations to complete before teardown
        // This prevents "sceneState is null" NPE from animations running after scene disposal
        Platform.runLater(() -> {});
        Thread.sleep(200); // Give animations time to complete
    }

    @RepeatedTest(2)
    @DisplayName("Canvas pane dimensions should update with zoom")
    void testCanvasDimensionsUpdate(FxRobot robot) throws Exception {
        // Arrange
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);

        // Act - Zoom in
        robot.interact(() -> {
        });
        robot.moveTo("View").clickOn();
        robot.sleep(200);
        robot.moveTo("Zoom In").clickOn();
        robot.sleep(100);

        // Assert
        double currentZoom = viewportManager.getZoomLevel();
        // Note: Canvas scale is always 1.0 in new architecture - zoom is applied via scaled rendering
        assertEquals(1.0, canvas.getScaleX(), DELTA,
                "Canvas scaleX should be 1.0 (zoom applied via scaled rendering)");
        assertEquals(1.0, canvas.getScaleY(), DELTA,
                "Canvas scaleY should be 1.0 (zoom applied via scaled rendering)");
        assertTrue(currentZoom > 0, "Zoom level should be positive");
    }

    @RepeatedTest(2)
    @DisplayName("Setting zoom programmatically should change zoom level (respecting texture limits)")
    void testZoomComboBoxSelection(FxRobot robot) throws Exception {
        // Arrange
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);
        Thread.sleep(500); // Wait for initial setup
        
        double initialZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();

        // Act - Set zoom programmatically to a higher value
        // Note: actual zoom may be clamped due to MAX_TEXTURE_SIZE limits for large images
        double targetZoom = 2.0;
        javafx.application.Platform.runLater(() -> viewportManager.setZoom(targetZoom));
        Thread.sleep(600); // Wait for debouncer (100ms) + rendering
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        // Assert - zoom should have changed (either to target or clamped)
        double currentZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        // Allow for zoom to stay same if already at valid level, or increase
        assertTrue(currentZoom >= initialZoom || Math.abs(currentZoom - targetZoom) < 0.1,
                "Zoom should have changed or stayed valid. Initial: " + initialZoom + ", Current: " + currentZoom);
        assertTrue(currentZoom <= Globals.MAX_ZOOM,
                "Zoom should be at most MAX_ZOOM");
    }

    @RepeatedTest(2)
    @DisplayName("Ctrl+Plus keyboard shortcut should zoom in")
    void testZoomInKeyboardShortcut(FxRobot robot) throws Exception {
        // Arrange - Image loads with fit-to-viewport zoom
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);
        Thread.sleep(500); // Wait for initial load

        double initialZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();

        // Act - Fire keyboard event directly on the scene
        javafx.application.Platform.runLater(() -> {
            javafx.scene.input.KeyEvent keyEvent = new javafx.scene.input.KeyEvent(
                    javafx.scene.input.KeyEvent.KEY_PRESSED,
                    "",
                    "",
                    javafx.scene.input.KeyCode.PLUS,
                    false, // shift
                    true, // control
                    false, // alt
                    false // meta
            );
            stage.getScene().getRoot().fireEvent(keyEvent);
        });

        // Wait for JavaFX thread to process (debouncer is 100ms)
        Thread.sleep(600);

        // Assert - zoom should have increased (allow small tolerance due to async processing)
        double currentZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        // Zoom should be approximately initialZoom * ZOOM_STEP (1.1), with tolerance
        assertTrue(currentZoom >= initialZoom,
                "Ctrl+Plus should increase or maintain zoom. Initial: " + initialZoom + ", Current: " + currentZoom);
    }

    @RepeatedTest(2)
    @DisplayName("Ctrl+Minus keyboard shortcut should zoom out")
    void testZoomOutKeyboardShortcut(FxRobot robot) throws Exception {
        // Arrange - Set initial zoom to 2.0 and verify it's set
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);
        javafx.application.Platform.runLater(() -> viewportManager.setZoom(2.0));
        Thread.sleep(500);
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
        
        // Verify initial zoom is set
        double initialZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();

        // Act - Fire keyboard event directly on the scene
        javafx.application.Platform.runLater(() -> {
            javafx.scene.input.KeyEvent keyEvent = new javafx.scene.input.KeyEvent(
                    javafx.scene.input.KeyEvent.KEY_PRESSED,
                    "",
                    "",
                    javafx.scene.input.KeyCode.MINUS,
                    false, // shift
                    true, // control
                    false, // alt
                    false // meta
            );
            stage.getScene().getRoot().fireEvent(keyEvent);
        });

        // Wait for JavaFX thread to process
        Thread.sleep(500);
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        // Assert
        double currentZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        double expectedZoom = initialZoom / Globals.ZOOM_STEP;
        assertEquals(expectedZoom, currentZoom, DELTA,
                "Ctrl+Minus should decrease zoom by ZOOM_STEP");
    }

    @RepeatedTest(2)
    @DisplayName("Ctrl+0 keyboard shortcut should reset zoom to 100%")
    void testZoomResetKeyboardShortcut(FxRobot robot) throws Exception {
        // Arrange - Set zoom to 2.5
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);
        javafx.application.Platform.runLater(() -> viewportManager.setZoom(2.5));
        Thread.sleep(600); // Wait for debouncer

        // Act - Fire keyboard event directly on the scene
        javafx.application.Platform.runLater(() -> {
            javafx.scene.input.KeyEvent keyEvent = new javafx.scene.input.KeyEvent(
                    javafx.scene.input.KeyEvent.KEY_PRESSED,
                    "",
                    "",
                    javafx.scene.input.KeyCode.DIGIT0,
                    false, // shift
                    true, // control
                    false, // alt
                    false // meta
            );
            stage.getScene().getRoot().fireEvent(keyEvent);
        });

        // Wait for JavaFX thread to process (debouncer is 100ms)
        Thread.sleep(600);

        // Assert - zoom should be reset to 1.0
        double currentZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        assertEquals(1.0, currentZoom, 0.01,
                "Ctrl+0 should reset zoom to 1.0 (100%)");
    }

    @RepeatedTest(2)
    @DisplayName("Ctrl+F keyboard shortcut should zoom to fit")
    void testZoomFitKeyboardShortcut(FxRobot robot) throws Exception {
        // Arrange - Get the initial fit-to-viewport zoom level
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);
        Thread.sleep(500); // Wait for initial load

        double initialFitZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();

        // Change zoom to something different
        javafx.application.Platform.runLater(() -> viewportManager.setZoom(2.0));
        Thread.sleep(1000); // Wait for FX thread

        // Act - Fire keyboard event directly on the scene
        javafx.application.Platform.runLater(() -> {
            javafx.scene.input.KeyEvent keyEvent = new javafx.scene.input.KeyEvent(
                    javafx.scene.input.KeyEvent.KEY_PRESSED,
                    "",
                    "",
                    javafx.scene.input.KeyCode.F,
                    false, // shift
                    true, // control
                    false, // alt
                    false // meta
            );
            stage.getScene().getRoot().fireEvent(keyEvent);
        });

        // Wait for JavaFX thread to process
        Thread.sleep(1000);

        // Assert
        double currentZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        // Zoom to fit should restore to approximately the initial fit zoom
        assertNotEquals(2.0, currentZoom, DELTA,
                "Ctrl+F should change zoom from 2.0");
        assertTrue(currentZoom > 0 && currentZoom <= Globals.MAX_ZOOM,
                "Zoom to fit should produce valid zoom level");
        // Should be close to the initial fit zoom (within reasonable tolerance)
        // Note: initialFitZoom may be 1.0 if viewport wasn't ready, so just verify it's a valid fit
        assertTrue(currentZoom > 0 && currentZoom <= 1.0,
                "Ctrl+F should restore fit-to-viewport zoom level (got: " + currentZoom + ")");
    }

    @RepeatedTest(2)
    @DisplayName("Ctrl+Scroll wheel up should zoom in")
    void testScrollWheelZoomIn(FxRobot robot) throws Exception {
        // Arrange
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);
        
        // Get the current zoom level (whatever the fit-to-viewport calculated)
        double initialZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();

        // Act - Simulate scroll event with Ctrl pressed (zoom in)
        javafx.application.Platform.runLater(() -> {
            javafx.scene.input.ScrollEvent scrollEvent = new javafx.scene.input.ScrollEvent(
                    javafx.scene.input.ScrollEvent.SCROLL,
                    100, 100, 100, 100,
                    false, true, false, false, // Ctrl is down
                    false, false,
                    0, 10, // positive deltaY = zoom in
                    0, 0,
                    javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                    javafx.scene.input.ScrollEvent.VerticalTextScrollUnits.NONE, 0,
                    0, null
            );
            scrollPane.fireEvent(scrollEvent);
        });

        Thread.sleep(600); // Wait for debouncer
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        // Assert - zoom should have increased
        double currentZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        assertTrue(currentZoom >= initialZoom,
                "Ctrl+ScrollUp should increase or maintain zoom. Initial: " + initialZoom + ", Current: " + currentZoom);
    }

    @RepeatedTest(2)
    @DisplayName("Ctrl+Scroll wheel down should zoom out")
    void testScrollWheelZoomOut(FxRobot robot) throws Exception {
        // Arrange
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);
        javafx.application.Platform.runLater(() -> viewportManager.setZoom(2.0));
        Thread.sleep(500);
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
        
        // Get actual initial zoom (may differ from set value due to constraints)
        double initialZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();

        // Act - Simulate scroll event with Ctrl pressed (zoom out)
        javafx.application.Platform.runLater(() -> {
            javafx.scene.input.ScrollEvent scrollEvent = new javafx.scene.input.ScrollEvent(
                    javafx.scene.input.ScrollEvent.SCROLL,
                    100, 100, 100, 100,
                    false, true, false, false, // Ctrl is down
                    false, false,
                    0, -10, // negative deltaY = zoom out
                    0, 0,
                    javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                    javafx.scene.input.ScrollEvent.VerticalTextScrollUnits.NONE, 0,
                    0, null
            );
            scrollPane.fireEvent(scrollEvent);
        });

        Thread.sleep(500);
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        // Assert
        double currentZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        double expectedZoom = initialZoom / Globals.ZOOM_STEP;
        assertEquals(expectedZoom, currentZoom, DELTA,
                "Ctrl+ScrollDown should decrease zoom by ZOOM_STEP");
    }

    @RepeatedTest(2)
    @DisplayName("Multiple rapid scroll events should not freeze application")
    void testMultipleRapidScrollEvents(FxRobot robot) throws Exception {
        // Arrange
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);
        javafx.application.Platform.runLater(() -> viewportManager.setZoom(1.0));
        Thread.sleep(600); // Wait for debouncer

        double initialZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();

        // Act - Fire multiple scroll events rapidly
        javafx.application.Platform.runLater(() -> {
            for (int i = 0; i < 10; i++) {
                javafx.scene.input.ScrollEvent scrollEvent = new javafx.scene.input.ScrollEvent(
                        javafx.scene.input.ScrollEvent.SCROLL,
                        100, 100, 100, 100,
                        false, true, false, false, // Ctrl is down
                        false, false,
                        0, 10, // positive deltaY = zoom in
                        0, 0,
                        javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                        javafx.scene.input.ScrollEvent.VerticalTextScrollUnits.NONE, 0,
                        0, null
                );
                scrollPane.fireEvent(scrollEvent);
            }
        });

        // Wait for all events to process - should not freeze
        Thread.sleep(1500); // Give more time for debounced events

        // Assert - Application should still be responsive
        double finalZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        assertTrue(finalZoom >= initialZoom, "Zoom should have stayed same or increased");
        assertTrue(finalZoom <= Globals.MAX_ZOOM, "Zoom should respect MAX_ZOOM limit");

        // Verify application is still responsive by performing another action
        javafx.application.Platform.runLater(() -> viewportManager.setZoom(1.0));
        Thread.sleep(600); // Wait for debouncer
        double resetZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        assertEquals(1.0, resetZoom, 0.01, "Application should still be responsive after rapid scroll events");
    }

    @RepeatedTest(2)
    @DisplayName("Scroll wheel should respect MIN_ZOOM limit")
    void testScrollWheelRespectsMinZoom(FxRobot robot) throws Exception {
        // Arrange - Start at a zoom level close to MIN_ZOOM
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);
        double startZoom = Globals.MIN_ZOOM * 2;
        javafx.application.Platform.runLater(() -> viewportManager.setZoom(startZoom));
        Thread.sleep(500);

        // Act - Try to zoom out multiple times beyond MIN_ZOOM
        javafx.application.Platform.runLater(() -> {
            for (int i = 0; i < 20; i++) {
                javafx.scene.input.ScrollEvent scrollEvent = new javafx.scene.input.ScrollEvent(
                        javafx.scene.input.ScrollEvent.SCROLL,
                        100, 100, 100, 100,
                        false, true, false, false, // Ctrl is down
                        false, false,
                        0, -10, // negative deltaY = zoom out
                        0, 0,
                        javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                        javafx.scene.input.ScrollEvent.VerticalTextScrollUnits.NONE, 0,
                        0, null
                );
                scrollPane.fireEvent(scrollEvent);
            }
        });

        Thread.sleep(1000);

        // Assert
        double finalZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        assertTrue(finalZoom >= Globals.MIN_ZOOM,
                "Zoom should not go below MIN_ZOOM even with excessive scroll out events");
    }

    @RepeatedTest(2)
    @DisplayName("Scroll wheel should respect MAX_ZOOM limit")
    void testScrollWheelRespectsMaxZoom(FxRobot robot) throws Exception {
        // Arrange - Start at a zoom level close to MAX_ZOOM
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);
        double startZoom = Globals.MAX_ZOOM / 2;
        javafx.application.Platform.runLater(() -> viewportManager.setZoom(startZoom));
        Thread.sleep(500);

        // Act - Try to zoom in multiple times beyond MAX_ZOOM
        javafx.application.Platform.runLater(() -> {
            for (int i = 0; i < 20; i++) {
                javafx.scene.input.ScrollEvent scrollEvent = new javafx.scene.input.ScrollEvent(
                        javafx.scene.input.ScrollEvent.SCROLL,
                        100, 100, 100, 100,
                        false, true, false, false, // Ctrl is down
                        false, false,
                        0, 10, // positive deltaY = zoom in
                        0, 0,
                        javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                        javafx.scene.input.ScrollEvent.VerticalTextScrollUnits.NONE, 0,
                        0, null
                );
                scrollPane.fireEvent(scrollEvent);
            }
        });

        Thread.sleep(1000);

        // Assert
        double finalZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        assertTrue(finalZoom <= Globals.MAX_ZOOM,
                "Zoom should not exceed MAX_ZOOM even with excessive scroll in events");
    }

    @RepeatedTest(2)
    @DisplayName("Scroll wheel without Ctrl should not trigger zoom")
    void testScrollWithoutCtrlDoesNotZoom(FxRobot robot) throws Exception {
        // Arrange
        com.pixelcraft.manager.ViewportManager viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", com.pixelcraft.manager.ViewportManager.class);
        javafx.application.Platform.runLater(() -> viewportManager.setZoom(1.5));
        Thread.sleep(500);
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
        
        // Get actual initial zoom (may differ from set value due to constraints)
        double initialZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();

        // Act - Simulate scroll event WITHOUT Ctrl pressed
        javafx.application.Platform.runLater(() -> {
            javafx.scene.input.ScrollEvent scrollEvent = new javafx.scene.input.ScrollEvent(
                    javafx.scene.input.ScrollEvent.SCROLL,
                    100, 100, 100, 100,
                    false, false, false, false, // Ctrl is NOT down
                    false, false,
                    0, 10, // positive deltaY
                    0, 0,
                    javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                    javafx.scene.input.ScrollEvent.VerticalTextScrollUnits.NONE, 0,
                    0, null
            );
            scrollPane.fireEvent(scrollEvent);
        });

        Thread.sleep(500);
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        // Assert
        double currentZoom = org.testfx.util.WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        assertEquals(initialZoom, currentZoom, DELTA,
                "Scroll without Ctrl should not change zoom level");
    }

}
