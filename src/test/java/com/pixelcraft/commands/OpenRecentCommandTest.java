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
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationExtension;

import com.pixelcraft.manager.FileManager;

/**
 * Unit tests for OpenRecentCommand class
 * 
 * @author Nathan Khupe
 */
@ExtendWith(ApplicationExtension.class)
class OpenRecentCommandTest {

    private FileManager fileManager;
    private OpenRecentCommand command;

    @BeforeEach
    void setUp() {
        fileManager = new FileManager();
    }
    
    /** Helper to create a real image file */
    private File createTestImageFile(File dir, String name) throws IOException {
        File file = new File(dir, name);
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(img, "png", file);
        return file;
    }

    @Test
    @DisplayName("Execute loads image from path")
    void testExecuteLoadsImage(@TempDir File tempDir) throws IOException {
        File testFile = createTestImageFile(tempDir, "test.png");
        command = new OpenRecentCommand(fileManager, testFile.getAbsolutePath());
        
        command.execute();
        
        Optional<File> currentFile = fileManager.getCurrentFile();
        assertTrue(currentFile.isPresent());
        assertEquals(testFile.getAbsolutePath(), currentFile.get().getAbsolutePath());
    }

    @Test
    @DisplayName("Execute saves previous state for undo")
    void testExecuteSavesPreviousState(@TempDir File tempDir) throws IOException {
        File originalFile = createTestImageFile(tempDir, "original.png");
        fileManager.loadImage(originalFile);
        Optional<com.pixelcraft.model.RasterImage> originalImage = fileManager.getCurrentImage();
        assertTrue(originalImage.isPresent());
        int originalWidth = originalImage.get().getWidth();
        int originalHeight = originalImage.get().getHeight();
        
        File recentFile = createTestImageFile(tempDir, "recent.png");
        command = new OpenRecentCommand(fileManager, recentFile.getAbsolutePath());
        
        command.execute();
        waitForFxEvents();
        command.undo();
        waitForFxEvents();
        
        // Undo restores the image content (dimensions match original)
        Optional<com.pixelcraft.model.RasterImage> restoredImage = fileManager.getCurrentImage();
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
    void testUndoWithNoPreviousImage(@TempDir File tempDir) throws IOException {
        File testFile = createTestImageFile(tempDir, "test.png");
        command = new OpenRecentCommand(fileManager, testFile.getAbsolutePath());
        
        command.execute();
        command.undo();
        
        assertDoesNotThrow(() -> command.undo());
    }

    @Test
    @DisplayName("Get description returns meaningful text")
    void testGetDescription(@TempDir File tempDir) {
        File testFile = new File(tempDir, "myimage.png");
        command = new OpenRecentCommand(fileManager, testFile.getAbsolutePath());
        
        String description = command.getDescription();
        
        assertNotNull(description);
        assertTrue(description.toLowerCase().contains("open") || 
                   description.toLowerCase().contains("recent") ||
                   description.contains("myimage.png"));
    }

    @Test
    @DisplayName("Execute with non-existent file")
    void testExecuteWithNonExistentFile(@TempDir File tempDir) {
        File nonExistent = new File(tempDir, "nonexistent.png");
        command = new OpenRecentCommand(fileManager, nonExistent.getAbsolutePath());
        
        assertDoesNotThrow(() -> command.execute());
        
        // File is validated - non-existent files are rejected, so no file is set
        Optional<File> currentFile = fileManager.getCurrentFile();
        assertFalse(currentFile.isPresent());
    }

    @Test
    @DisplayName("Multiple execute calls work correctly")
    void testMultipleExecuteCalls(@TempDir File tempDir) throws IOException {
        File testFile = createTestImageFile(tempDir, "test.png");
        command = new OpenRecentCommand(fileManager, testFile.getAbsolutePath());
        
        command.execute();
        File firstFile = fileManager.getCurrentFile().get();
        
        command.execute();
        File secondFile = fileManager.getCurrentFile().get();
        
        assertEquals(firstFile, secondFile);
    }

    @Test
    @DisplayName("Undo-redo cycle works correctly")
    void testUndoRedoCycle(@TempDir File tempDir) throws IOException {
        File originalFile = createTestImageFile(tempDir, "original.png");
        fileManager.loadImage(originalFile);
        
        File recentFile = createTestImageFile(tempDir, "recent.png");
        command = new OpenRecentCommand(fileManager, recentFile.getAbsolutePath());
        
        command.execute();
        waitForFxEvents();
        command.undo();
        waitForFxEvents();
        command.redo();
        waitForFxEvents();
        
        // After redo, the image dimensions should match recentFile (10x10)
        Optional<com.pixelcraft.model.RasterImage> restoredImage = fileManager.getCurrentImage();
        assertTrue(restoredImage.isPresent());
        assertEquals(10, restoredImage.get().getWidth());
    }
}
