package com.pixelcraft.commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationExtension;

import com.pixelcraft.manager.FileManager;
import com.pixelcraft.model.RasterImage;

/**
 * Unit tests for NewImageCommand class
 * 
 * @author Nathan Khupe
 */
@ExtendWith(ApplicationExtension.class)
class NewImageCommandTest {

    private FileManager fileManager;
    private NewImageCommand command;

    @BeforeEach
    void setUp() {
        fileManager = new FileManager();
    }

    @Test
    @DisplayName("Execute creates new image with specified dimensions")
    void testExecuteCreatesNewImage() {
        command = new NewImageCommand(fileManager, 100, 200);
        
        command.execute();
        
        Optional<RasterImage> image = fileManager.getCurrentImage();
        assertTrue(image.isPresent());
        assertEquals(100, image.get().getWidth());
        assertEquals(200, image.get().getHeight());
    }

    @Test
    @DisplayName("Execute clears current file")
    void testExecuteClearsCurrentFile() {
        fileManager.loadImage(new File("test.png"));
        command = new NewImageCommand(fileManager, 50, 50);
        
        command.execute();
        
        assertFalse(fileManager.getCurrentFile().isPresent());
    }

    @Test
    @DisplayName("Undo restores previous image dimensions")
    void testUndoRestoresPreviousFile(@TempDir File tempDir) throws IOException {
        File originalFile = new File(tempDir, "original.png");
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(img, "png", originalFile);
        
        fileManager.loadImage(originalFile);
        Optional<RasterImage> originalImage = fileManager.getCurrentImage();
        assertTrue(originalImage.isPresent());
        int originalWidth = originalImage.get().getWidth();
        int originalHeight = originalImage.get().getHeight();
        
        command = new NewImageCommand(fileManager, 100, 100);
        command.execute();
        waitForFxEvents();
        
        // Verify new image has different dimensions
        Optional<RasterImage> newImage = fileManager.getCurrentImage();
        assertTrue(newImage.isPresent());
        assertEquals(100, newImage.get().getWidth());
        
        command.undo();
        waitForFxEvents();
        
        // Undo restores the original image content (dimensions)
        Optional<RasterImage> restoredImage = fileManager.getCurrentImage();
        assertTrue(restoredImage.isPresent());
        assertEquals(originalWidth, restoredImage.get().getWidth());
        assertEquals(originalHeight, restoredImage.get().getHeight());
    }
    
    /** Helper to wait for JavaFX Platform.runLater events to complete */
    private void waitForFxEvents() {
        try {
            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            javafx.application.Platform.runLater(latch::countDown);
            latch.await(1, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("Undo with no previous image")
    void testUndoWithNoPreviousImage() {
        command = new NewImageCommand(fileManager, 100, 100);
        
        command.execute();
        command.undo();
        
        // Should handle gracefully
        assertDoesNotThrow(() -> command.undo());
    }

    @Test
    @DisplayName("Get description returns meaningful text")
    void testGetDescription() {
        command = new NewImageCommand(fileManager, 640, 480);
        
        String description = command.getDescription();
        
        assertNotNull(description);
        assertTrue(description.contains("640"));
        assertTrue(description.contains("480"));
    }

    @Test
    @DisplayName("Multiple execute calls work correctly")
    void testMultipleExecuteCalls() {
        command = new NewImageCommand(fileManager, 100, 100);
        
        command.execute();
        RasterImage first = fileManager.getCurrentImage().get();
        
        command.execute();
        RasterImage second = fileManager.getCurrentImage().get();
        
        assertNotSame(first, second, "Should create new image each time");
    }

    @Test
    @DisplayName("Create very small image")
    void testCreateVerySmallImage() {
        command = new NewImageCommand(fileManager, 1, 1);
        
        command.execute();
        
        Optional<RasterImage> image = fileManager.getCurrentImage();
        assertTrue(image.isPresent());
        assertEquals(1, image.get().getWidth());
        assertEquals(1, image.get().getHeight());
    }

    @Test
    @DisplayName("Create large image")
    void testCreateLargeImage() {
        command = new NewImageCommand(fileManager, 2000, 2000);
        
        assertDoesNotThrow(() -> command.execute());
        
        Optional<RasterImage> image = fileManager.getCurrentImage();
        assertTrue(image.isPresent());
        assertEquals(2000, image.get().getWidth());
    }
}
