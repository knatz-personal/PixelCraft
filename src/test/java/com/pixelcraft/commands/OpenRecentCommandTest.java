package com.pixelcraft.commands;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.pixelcraft.manager.FileManager;

/**
 * Unit tests for OpenRecentCommand class
 * 
 * @author Nathan Khupe
 */
class OpenRecentCommandTest {

    private FileManager fileManager;
    private OpenRecentCommand command;

    @BeforeEach
    void setUp() {
        fileManager = new FileManager();
    }

    @Test
    @DisplayName("Execute loads image from path")
    void testExecuteLoadsImage(@TempDir File tempDir) {
        File testFile = new File(tempDir, "test.png");
        command = new OpenRecentCommand(fileManager, testFile.getAbsolutePath());
        
        command.execute();
        
        Optional<File> currentFile = fileManager.getCurrentFile();
        assertTrue(currentFile.isPresent());
        assertEquals(testFile.getAbsolutePath(), currentFile.get().getAbsolutePath());
    }

    @Test
    @DisplayName("Execute saves previous state for undo")
    void testExecuteSavesPreviousState(@TempDir File tempDir) {
        File originalFile = new File(tempDir, "original.png");
        fileManager.loadImage(originalFile);
        
        File recentFile = new File(tempDir, "recent.png");
        command = new OpenRecentCommand(fileManager, recentFile.getAbsolutePath());
        
        command.execute();
        command.undo();
        
        Optional<File> currentFile = fileManager.getCurrentFile();
        assertTrue(currentFile.isPresent());
        assertEquals(originalFile, currentFile.get());
    }

    @Test
    @DisplayName("Undo with no previous image")
    void testUndoWithNoPreviousImage(@TempDir File tempDir) {
        File testFile = new File(tempDir, "test.png");
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
        
        // File should be set even if it doesn't exist
        Optional<File> currentFile = fileManager.getCurrentFile();
        assertTrue(currentFile.isPresent());
    }

    @Test
    @DisplayName("Multiple execute calls work correctly")
    void testMultipleExecuteCalls(@TempDir File tempDir) {
        File testFile = new File(tempDir, "test.png");
        command = new OpenRecentCommand(fileManager, testFile.getAbsolutePath());
        
        command.execute();
        File firstFile = fileManager.getCurrentFile().get();
        
        command.execute();
        File secondFile = fileManager.getCurrentFile().get();
        
        assertEquals(firstFile, secondFile);
    }

    @Test
    @DisplayName("Undo-redo cycle works correctly")
    void testUndoRedoCycle(@TempDir File tempDir) {
        File originalFile = new File(tempDir, "original.png");
        fileManager.loadImage(originalFile);
        
        File recentFile = new File(tempDir, "recent.png");
        command = new OpenRecentCommand(fileManager, recentFile.getAbsolutePath());
        
        command.execute();
        command.undo();
        command.execute(); // Redo
        
        Optional<File> currentFile = fileManager.getCurrentFile();
        assertTrue(currentFile.isPresent());
        assertEquals(recentFile, currentFile.get());
    }
}
