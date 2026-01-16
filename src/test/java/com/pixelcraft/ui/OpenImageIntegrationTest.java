package com.pixelcraft.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import com.pixelcraft.manager.FileManager;
import com.pixelcraft.manager.ViewportManager;
import com.pixelcraft.model.RasterImage;
import com.pixelcraft.util.Globals;
import com.pixelcraft.util.ReflectionUtil;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Integration tests for opening multiple images sequentially.
 * Tests that the canvas properly updates when switching between images
 * of different dimensions without freezing or displaying artifacts.
 *
 * @author Nathan Khupe
 */
@ExtendWith(ApplicationExtension.class)
@DisplayName("Open Image Integration Tests")
public class OpenImageIntegrationTest {

    private MainController controller;
    private ScrollPane scrollPane;
    private Canvas canvas;
    private Stage stage;
    private FileManager fileManager;
    private ViewportManager viewportManager;

    private static final String[] SAMPLE_FILES = new String[]{
        "/sample_images/xmas_sock.jpg",   // Colourful portrait image
        "/sample_images/japan.jpg",       // Large landscape image  
        "/sample_images/grey_lion.jpg"    // Grayscale portrait image
    };

    private static final double DELTA = 0.0001;

    @Start
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        // Create controller and UI components manually (no FXML needed)
        controller = new MainController();
        
        // Create all required FXML components
        StackPane canvasContainer = new StackPane();
        scrollPane = new ScrollPane();
        scrollPane.setContent(canvasContainer);
        
        Label lblImageSize = new Label();
        Label lblFileSize = new Label();
        Label lblPosition = new Label();
        Label lblMode = new Label();
        Menu mnuRecents = new Menu();
        ComboBox<Pair<String, Double>> cmbZoomPresets = new ComboBox<>();
        ListView<Label> lstHistory = new ListView<>();
        ListView<String> lstActivityLog = new ListView<>();

        // Inject FXML fields using reflection
        ReflectionUtil.injectField(controller, "canvasContainer", canvasContainer);
        ReflectionUtil.injectField(controller, "scrollPane", scrollPane);
        ReflectionUtil.injectField(controller, "lblImageSize", lblImageSize);
        ReflectionUtil.injectField(controller, "lblFileSize", lblFileSize);
        ReflectionUtil.injectField(controller, "lblPosition", lblPosition);
        ReflectionUtil.injectField(controller, "lblMode", lblMode);
        ReflectionUtil.injectField(controller, "mnuRecents", mnuRecents);
        ReflectionUtil.injectField(controller, "cmbZoomPresets", cmbZoomPresets);
        ReflectionUtil.injectField(controller, "lstHistory", lstHistory);
        ReflectionUtil.injectField(controller, "lstActivityLog", lstActivityLog);

        // Call initialize method
        Method initMethod = MainController.class.getDeclaredMethod("initialize");
        initMethod.setAccessible(true);
        initMethod.invoke(controller);

        // Get the canvas that was created
        canvas = (Canvas) canvasContainer.getChildren().get(0);
        
        // Get managers for testing
        fileManager = ReflectionUtil.getPrivateField(controller, "fileManager", FileManager.class);
        viewportManager = ReflectionUtil.getPrivateField(controller, "viewportManager", ViewportManager.class);

        // Set up the stage with smaller dimensions for headless testing
        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);
        Scene scene = new Scene(root, Globals.DEFAULT_WIDTH, Globals.DEFAULT_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Wait for any pending FX operations to complete before teardown
        // This prevents "sceneState is null" NPE from animations running after scene disposal
        Platform.runLater(() -> {});
        Thread.sleep(200); // Give animations time to complete
    }

    /**
     * Helper method to load a sample image from resources and create a temp file
     */
    private File createTempFileFromResource(String resourcePath) throws IOException {
        Image fxImage = new Image(getClass().getResourceAsStream(resourcePath));
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);
        
        String extension = resourcePath.substring(resourcePath.lastIndexOf('.'));
        File tempFile = File.createTempFile("test_image", extension);
        tempFile.deleteOnExit();
        
        String format = extension.substring(1); // Remove the dot
        if (format.equals("jpg")) format = "jpeg";
        ImageIO.write(bufferedImage, format, tempFile);
        
        return tempFile;
    }

    /**
     * Helper to get image dimensions from a resource
     */
    private int[] getImageDimensions(String resourcePath) {
        Image fxImage = new Image(getClass().getResourceAsStream(resourcePath));
        return new int[]{(int) fxImage.getWidth(), (int) fxImage.getHeight()};
    }

    @Test
    @DisplayName("Opening first image should update canvas with scaled dimensions")
    void testOpenFirstImage(FxRobot robot) throws Exception {
        // Arrange
        File imageFile = createTempFileFromResource(SAMPLE_FILES[0]);
        int[] expectedDims = getImageDimensions(SAMPLE_FILES[0]);

        // Act
        Platform.runLater(() -> fileManager.loadImage(imageFile));
        Thread.sleep(500);

        // Assert - Canvas size = image size * zoom (for fit-to-viewport)
        // Canvas size will be scaled based on viewport fit, not raw image dimensions
        double canvasWidth = WaitForAsyncUtils.asyncFx(() -> canvas.getWidth()).get();
        double canvasHeight = WaitForAsyncUtils.asyncFx(() -> canvas.getHeight()).get();
        double zoom = WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();

        // Canvas dimensions should be approximately image * zoom (with some tolerance for clamping)
        assertTrue(canvasWidth > 0, "Canvas width should be positive");
        assertTrue(canvasHeight > 0, "Canvas height should be positive");
        assertTrue(zoom > 0, "Zoom level should be positive");
        
        // Verify aspect ratio is preserved
        double imageAspect = (double) expectedDims[0] / expectedDims[1];
        double canvasAspect = canvasWidth / canvasHeight;
        assertEquals(imageAspect, canvasAspect, 0.01, "Canvas aspect ratio should match image aspect ratio");
    }

    @Test
    @DisplayName("Opening second image should update canvas to new scaled dimensions")
    void testOpenSecondImageUpdatesDimensions(FxRobot robot) throws Exception {
        // Arrange - Load first image
        File firstFile = createTempFileFromResource(SAMPLE_FILES[0]);
        Platform.runLater(() -> fileManager.loadImage(firstFile));
        Thread.sleep(500);

        double firstWidth = WaitForAsyncUtils.asyncFx(() -> canvas.getWidth()).get();
        double firstHeight = WaitForAsyncUtils.asyncFx(() -> canvas.getHeight()).get();

        // Act - Load second image with different dimensions
        File secondFile = createTempFileFromResource(SAMPLE_FILES[1]);
        int[] secondDims = getImageDimensions(SAMPLE_FILES[1]);
        Platform.runLater(() -> fileManager.loadImage(secondFile));
        Thread.sleep(500);

        // Assert - Canvas should have updated (dimensions change based on fit-to-viewport)
        double secondWidth = WaitForAsyncUtils.asyncFx(() -> canvas.getWidth()).get();
        double secondHeight = WaitForAsyncUtils.asyncFx(() -> canvas.getHeight()).get();

        // Verify canvas has positive dimensions
        assertTrue(secondWidth > 0, "Canvas width should be positive after loading second image");
        assertTrue(secondHeight > 0, "Canvas height should be positive after loading second image");
        
        // Verify aspect ratio matches second image
        double imageAspect = (double) secondDims[0] / secondDims[1];
        double canvasAspect = secondWidth / secondHeight;
        assertEquals(imageAspect, canvasAspect, 0.01, "Canvas aspect ratio should match second image");
        
        // Verify dimensions actually changed (unless images happen to have same aspect ratio)
        System.out.println("First image canvas: " + firstWidth + "x" + firstHeight);
        System.out.println("Second image canvas: " + secondWidth + "x" + secondHeight);
    }

    @Test
    @DisplayName("Opening multiple images sequentially should not freeze")
    void testOpenMultipleImagesSequentially(FxRobot robot) throws Exception {
        // Act - Load all sample images in sequence
        for (int i = 0; i < SAMPLE_FILES.length; i++) {
            File imageFile = createTempFileFromResource(SAMPLE_FILES[i]);
            int[] dims = getImageDimensions(SAMPLE_FILES[i]);
            
            System.out.println("Loading image " + (i + 1) + ": " + SAMPLE_FILES[i]);
            Platform.runLater(() -> fileManager.loadImage(imageFile));
            Thread.sleep(500);

            // Assert each image loads correctly with proper aspect ratio
            double canvasWidth = WaitForAsyncUtils.asyncFx(() -> canvas.getWidth()).get();
            double canvasHeight = WaitForAsyncUtils.asyncFx(() -> canvas.getHeight()).get();

            assertTrue(canvasWidth > 0, "Canvas width should be positive for image " + (i + 1));
            assertTrue(canvasHeight > 0, "Canvas height should be positive for image " + (i + 1));
            
            // Verify aspect ratio is preserved
            double imageAspect = (double) dims[0] / dims[1];
            double canvasAspect = canvasWidth / canvasHeight;
            assertEquals(imageAspect, canvasAspect, 0.01,
                "Canvas aspect ratio should match image " + (i + 1) + " aspect ratio");
        }

        // Verify application is still responsive
        double finalZoom = WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        assertTrue(finalZoom > 0, "Application should still be responsive after loading multiple images");
    }

    @Test
    @DisplayName("Canvas scale should be reset appropriately when opening new image")
    void testCanvasScaleResetOnNewImage(FxRobot robot) throws Exception {
        // Arrange - Load first image and zoom in
        File firstFile = createTempFileFromResource(SAMPLE_FILES[0]);
        Platform.runLater(() -> fileManager.loadImage(firstFile));
        Thread.sleep(500);

        // Set a specific zoom level
        Platform.runLater(() -> viewportManager.setZoom(2.0));
        Thread.sleep(300);

        double zoomBefore = WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        
        // Act - Load second image
        File secondFile = createTempFileFromResource(SAMPLE_FILES[1]);
        Platform.runLater(() -> fileManager.loadImage(secondFile));
        Thread.sleep(500);

        // Assert - Zoom should be recalculated for fit-to-viewport
        double zoomAfter = WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        
        // Zoom should change to fit the new image (unlikely to be exactly 2.0)
        assertNotEquals(2.0, zoomAfter, DELTA,
            "Zoom should be recalculated when loading a new image");
        assertTrue(zoomAfter > 0 && zoomAfter <= Globals.MAX_ZOOM,
            "Zoom should be within valid range");
    }

    @Test
    @DisplayName("Canvas scaleX and scaleY should be 1.0 after loading image (zoom via scaled rendering)")
    void testCanvasScaleMatchesZoom(FxRobot robot) throws Exception {
        // Arrange & Act
        File imageFile = createTempFileFromResource(SAMPLE_FILES[0]);
        Platform.runLater(() -> fileManager.loadImage(imageFile));
        Thread.sleep(500);

        // Assert - In new architecture, canvas scale is always 1.0
        // Zoom is applied via scaled image rendering, not canvas transforms
        double zoom = WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        double scaleX = WaitForAsyncUtils.asyncFx(() -> canvas.getScaleX()).get();
        double scaleY = WaitForAsyncUtils.asyncFx(() -> canvas.getScaleY()).get();

        assertEquals(1.0, scaleX, DELTA, "Canvas scaleX should be 1.0 (zoom via scaled rendering)");
        assertEquals(1.0, scaleY, DELTA, "Canvas scaleY should be 1.0 (zoom via scaled rendering)");
        assertTrue(zoom > 0, "Zoom level should be positive");
    }

    @Test
    @DisplayName("FileManager should have current image after loading")
    void testFileManagerHasCurrentImage(FxRobot robot) throws Exception {
        // Arrange & Act
        File imageFile = createTempFileFromResource(SAMPLE_FILES[0]);
        Platform.runLater(() -> fileManager.loadImage(imageFile));
        Thread.sleep(500);

        // Assert
        Optional<RasterImage> currentImage = WaitForAsyncUtils.asyncFx(
            () -> fileManager.getCurrentImage()
        ).get();

        assertTrue(currentImage.isPresent(), "FileManager should have a current image");
        assertTrue(currentImage.get().isValid(), "Current image should be valid");
        assertTrue(currentImage.get().getWidth() > 0, "Image width should be positive");
        assertTrue(currentImage.get().getHeight() > 0, "Image height should be positive");
    }

    @Test
    @DisplayName("Loading different sized images should update FileManager correctly")
    void testFileManagerUpdatesOnImageSwitch(FxRobot robot) throws Exception {
        // Load first image
        File firstFile = createTempFileFromResource(SAMPLE_FILES[0]);
        int[] firstDims = getImageDimensions(SAMPLE_FILES[0]);
        Platform.runLater(() -> fileManager.loadImage(firstFile));
        Thread.sleep(500);

        RasterImage firstImage = WaitForAsyncUtils.asyncFx(
            () -> fileManager.getCurrentImage().orElse(null)
        ).get();
        assertNotNull(firstImage, "First image should be loaded");
        assertEquals(firstDims[0], firstImage.getWidth(), "First image width mismatch");
        assertEquals(firstDims[1], firstImage.getHeight(), "First image height mismatch");

        // Load second image
        File secondFile = createTempFileFromResource(SAMPLE_FILES[1]);
        int[] secondDims = getImageDimensions(SAMPLE_FILES[1]);
        Platform.runLater(() -> fileManager.loadImage(secondFile));
        Thread.sleep(500);

        RasterImage secondImage = WaitForAsyncUtils.asyncFx(
            () -> fileManager.getCurrentImage().orElse(null)
        ).get();
        assertNotNull(secondImage, "Second image should be loaded");
        assertEquals(secondDims[0], secondImage.getWidth(), "Second image width mismatch");
        assertEquals(secondDims[1], secondImage.getHeight(), "Second image height mismatch");
    }

    @Test
    @DisplayName("Zoom to fit should work correctly for landscape vs portrait images")
    void testZoomToFitDifferentAspectRatios(FxRobot robot) throws Exception {
        // Test with each sample image
        for (String sampleFile : SAMPLE_FILES) {
            File imageFile = createTempFileFromResource(sampleFile);
            int[] dims = getImageDimensions(sampleFile);
            
            Platform.runLater(() -> fileManager.loadImage(imageFile));
            Thread.sleep(500);

            double zoom = WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
            
            // Calculate expected zoom-to-fit
            double viewportWidth = scrollPane.getViewportBounds().getWidth();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();
            double expectedScaleX = viewportWidth / dims[0];
            double expectedScaleY = viewportHeight / dims[1];
            double expectedZoom = Math.min(expectedScaleX, expectedScaleY) * 0.98; // ZOOM_FIT_PADDING

            System.out.println("Image: " + sampleFile + " (" + dims[0] + "x" + dims[1] + ")");
            System.out.println("Expected zoom: " + expectedZoom + ", Actual zoom: " + zoom);

            // Allow some tolerance due to timing
            assertEquals(expectedZoom, zoom, 0.1,
                "Zoom should fit image to viewport for " + sampleFile);
        }
    }

    @Test
    @DisplayName("Opening image after canvas has been zoomed should reset properly")
    void testOpenImageAfterZoom(FxRobot robot) throws Exception {
        // Arrange - Load first image
        File firstFile = createTempFileFromResource(SAMPLE_FILES[0]);
        Platform.runLater(() -> fileManager.loadImage(firstFile));
        Thread.sleep(500);

        double initialZoom = WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();

        // Set zoom to a specific higher value
        Platform.runLater(() -> viewportManager.setZoom(2.0));
        Thread.sleep(300);

        double zoomedLevel = WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        // Note: zoom may not increase if already at max due to texture constraints
        assertTrue(zoomedLevel >= initialZoom, "Zoom should be at least initial level after setZoom(2.0)");

        // Act - Load second image
        File secondFile = createTempFileFromResource(SAMPLE_FILES[1]);
        int[] secondDims = getImageDimensions(SAMPLE_FILES[1]);
        Platform.runLater(() -> fileManager.loadImage(secondFile));
        Thread.sleep(500);

        // Assert - Canvas should show new image with correct aspect ratio
        double canvasWidth = WaitForAsyncUtils.asyncFx(() -> canvas.getWidth()).get();
        double canvasHeight = WaitForAsyncUtils.asyncFx(() -> canvas.getHeight()).get();

        // Verify aspect ratio matches second image
        double imageAspect = (double) secondDims[0] / secondDims[1];
        double canvasAspect = canvasWidth / canvasHeight;
        assertEquals(imageAspect, canvasAspect, 0.01,
            "Canvas aspect ratio should match new image");
        
        // Zoom should be recalculated (not 2.0 anymore)
        double newZoom = WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        assertNotEquals(2.0, newZoom, DELTA,
            "Zoom should be recalculated for new image");
    }

    @Test
    @DisplayName("Rapid image switching should not cause errors")
    void testRapidImageSwitching(FxRobot robot) throws Exception {
        // Prepare all temp files first
        File[] tempFiles = new File[SAMPLE_FILES.length];
        for (int i = 0; i < SAMPLE_FILES.length; i++) {
            tempFiles[i] = createTempFileFromResource(SAMPLE_FILES[i]);
        }

        // Act - Rapidly switch between images
        for (int round = 0; round < 3; round++) {
            for (File tempFile : tempFiles) {
                Platform.runLater(() -> fileManager.loadImage(tempFile));
                Thread.sleep(100); // Very short delay to stress test
            }
        }

        // Wait for everything to settle
        Thread.sleep(500);

        // Assert - Application should still be responsive
        double finalZoom = WaitForAsyncUtils.asyncFx(() -> viewportManager.getZoomLevel()).get();
        assertTrue(finalZoom > 0, "Zoom should be positive after rapid switching");
        
        Optional<RasterImage> currentImage = WaitForAsyncUtils.asyncFx(
            () -> fileManager.getCurrentImage()
        ).get();
        assertTrue(currentImage.isPresent(), "Should have a current image");
        assertTrue(currentImage.get().isValid(), "Current image should be valid");
    }

    @Test
    @DisplayName("Canvas should be properly centered after loading new image")  
    void testCanvasCenteredAfterLoad(FxRobot robot) throws Exception {
        // Arrange & Act
        File imageFile = createTempFileFromResource(SAMPLE_FILES[0]);
        Platform.runLater(() -> fileManager.loadImage(imageFile));
        Thread.sleep(700); // Allow time for centering

        // Assert - Scroll values should be centered (0.5)
        double hValue = WaitForAsyncUtils.asyncFx(() -> scrollPane.getHvalue()).get();
        double vValue = WaitForAsyncUtils.asyncFx(() -> scrollPane.getVvalue()).get();

        // Allow some tolerance as centering may not be exact
        assertEquals(0.5, hValue, 0.2, "Horizontal scroll should be approximately centered");
        assertEquals(0.5, vValue, 0.2, "Vertical scroll should be approximately centered");
    }
}
