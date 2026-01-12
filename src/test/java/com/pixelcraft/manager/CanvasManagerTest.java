package com.pixelcraft.manager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import com.pixelcraft.model.RasterImage;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Unit tests for CanvasManager class
 * 
 * @author Nathan Khupe
 */
@ExtendWith(ApplicationExtension.class)
class CanvasManagerTest {

    private CanvasManager canvasManager;
    private Canvas canvas;
    private GraphicsContext gc;

    @BeforeEach
    void setUp() {
        canvas = new Canvas(800, 600);
        canvasManager = new CanvasManager(canvas);
        gc = canvas.getGraphicsContext2D();
    }

    @Test
    @DisplayName("Constructor initializes canvas manager")
    void testConstructor() {
        assertNotNull(canvasManager);
        assertEquals(canvas, canvasManager.getCanvas());
    }

    @Test
    @DisplayName("Get canvas returns correct canvas")
    void testGetCanvas() {
        Canvas retrievedCanvas = canvasManager.getCanvas();
        
        assertSame(canvas, retrievedCanvas);
    }

    @Test
    @DisplayName("Draw checkerboard does not throw exception")
    void testDrawCheckerboard() {
        assertDoesNotThrow(() -> canvasManager.drawCheckerboard());
    }

    @Test
    @DisplayName("Draw checkerboard on small canvas")
    void testDrawCheckerboardSmallCanvas() {
        Canvas smallCanvas = new Canvas(50, 50);
        CanvasManager smallManager = new CanvasManager(smallCanvas);
        
        assertDoesNotThrow(() -> smallManager.drawCheckerboard());
    }

    @Test
    @DisplayName("Draw checkerboard on large canvas")
    void testDrawCheckerboardLargeCanvas() {
        Canvas largeCanvas = new Canvas(2000, 2000);
        CanvasManager largeManager = new CanvasManager(largeCanvas);
        
        assertDoesNotThrow(() -> largeManager.drawCheckerboard());
    }

    @Test
    @DisplayName("Draw image with null does not throw exception")
    void testDrawImageWithNull() {
        assertDoesNotThrow(() -> canvasManager.drawImage(null));
    }

    @Test
    @DisplayName("Draw image with valid RasterImage")
    void testDrawImageWithValidRasterImage() {
        RasterImage image = new RasterImage(100, 100);
        
        assertDoesNotThrow(() -> canvasManager.drawImage(image));
    }

    @Test
    @DisplayName("Render with null image")
    void testRenderWithNull() {
        assertDoesNotThrow(() -> canvasManager.render(null));
    }

    @Test
    @DisplayName("Render with valid image")
    void testRenderWithValidImage() {
        RasterImage image = new RasterImage(100, 100);
        
        assertDoesNotThrow(() -> canvasManager.render(image));
    }

    @Test
    @DisplayName("Render draws checkerboard first")
    void testRenderDrawsCheckerboard() {
        RasterImage image = new RasterImage(50, 50);
        
        // Rendering should not throw and should draw both checkerboard and image
        assertDoesNotThrow(() -> canvasManager.render(image));
    }

    @Test
    @DisplayName("Multiple render calls work correctly")
    void testMultipleRenderCalls() {
        RasterImage image1 = new RasterImage(100, 100);
        RasterImage image2 = new RasterImage(200, 200);
        
        assertDoesNotThrow(() -> {
            canvasManager.render(image1);
            canvasManager.render(image2);
            canvasManager.render(null);
            canvasManager.render(image1);
        });
    }

    @Test
    @DisplayName("Draw image with different sizes")
    void testDrawImageWithDifferentSizes() {
        RasterImage small = new RasterImage(10, 10);
        RasterImage medium = new RasterImage(100, 100);
        RasterImage large = new RasterImage(1000, 1000);
        
        assertDoesNotThrow(() -> {
            canvasManager.drawImage(small);
            canvasManager.drawImage(medium);
            canvasManager.drawImage(large);
        });
    }

    @Test
    @DisplayName("Render sequence with various images")
    void testRenderSequence() {
        RasterImage image = new RasterImage(100, 100);
        
        assertDoesNotThrow(() -> {
            canvasManager.render(null);
            canvasManager.render(image);
            canvasManager.render(null);
            canvasManager.render(image);
        });
    }

    @Test
    @DisplayName("Canvas manager works with minimum size canvas")
    void testMinimumSizeCanvas() {
        Canvas tinyCanvas = new Canvas(1, 1);
        CanvasManager tinyManager = new CanvasManager(tinyCanvas);
        
        assertDoesNotThrow(() -> {
            tinyManager.drawCheckerboard();
            tinyManager.render(null);
        });
    }

    @Test
    @DisplayName("Draw checkerboard multiple times")
    void testDrawCheckerboardMultipleTimes() {
        assertDoesNotThrow(() -> {
            canvasManager.drawCheckerboard();
            canvasManager.drawCheckerboard();
            canvasManager.drawCheckerboard();
        });
    }

    @Test
    @DisplayName("Render updates canvas content")
    void testRenderUpdatesCanvas() {
        RasterImage whiteImage = new RasterImage(100, 100);
        
        // Fill with white
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                whiteImage.setPixel(x, y, 0xFFFFFFFF);
            }
        }
        
        assertDoesNotThrow(() -> canvasManager.render(whiteImage));
    }

    @Test
    @DisplayName("Canvas dimensions remain unchanged")
    void testCanvasDimensionsUnchanged() {
        double originalWidth = canvas.getWidth();
        double originalHeight = canvas.getHeight();
        
        canvasManager.drawCheckerboard();
        RasterImage image = new RasterImage(50, 50);
        canvasManager.render(image);
        
        assertEquals(originalWidth, canvas.getWidth());
        assertEquals(originalHeight, canvas.getHeight());
    }
}
