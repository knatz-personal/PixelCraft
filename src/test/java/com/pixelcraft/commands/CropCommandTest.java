package com.pixelcraft.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import com.pixelcraft.manager.FileManager;
import com.pixelcraft.model.RasterImage;

import javafx.scene.shape.Rectangle;

/**
 * Unit tests for CropCommand class
 * 
 * Tests the crop functionality:
 * - Crop execution with valid bounds
 * - Validation of invalid bounds
 * - Undo/restore functionality
 * 
 * @author Nathan Khupe
 */
@ExtendWith(ApplicationExtension.class)
@DisplayName("CropCommand Tests")
class CropCommandTest {

    private FileManager fileManager;
    private RasterImage originalImage;

    @BeforeEach
    void setUp() {
        fileManager = new FileManager();
        originalImage = new RasterImage(200, 200);
        fileManager.setCurrentImage(originalImage);
    }

    @Test
    @DisplayName("Crop execution reduces image dimensions")
    void testCropExecutes() {
        Rectangle bounds = new Rectangle(50, 50, 100, 100);
        CropCommand cmd = new CropCommand(fileManager, bounds);
        cmd.execute();

        assertEquals(100, fileManager.getCurrentImage().get().getWidth());
        assertEquals(100, fileManager.getCurrentImage().get().getHeight());
    }

    @Test
    @DisplayName("Undo restore original dimensions after crop")
    void testUndoRestoresOriginal() {
        Rectangle bounds = new Rectangle(50, 50, 100, 100);
        CropCommand cmd = new CropCommand(fileManager, bounds);
        cmd.execute();
        cmd.undo();

        assertEquals(200, fileManager.getCurrentImage().get().getWidth());
        assertEquals(200, fileManager.getCurrentImage().get().getHeight());
    }

    @Test
    @DisplayName("Crop throws on negative X")
    void testNegativeX() {
        Rectangle bounds = new Rectangle(-10, 50, 100, 100);
        CropCommand cmd = new CropCommand(fileManager, bounds);
        assertThrows(IllegalArgumentException.class, cmd::execute);
    }

    @Test
    @DisplayName("Crop throws on negative Y")
    void testNegativeY() {
        Rectangle bounds = new Rectangle(50, -10, 100, 100);
        CropCommand cmd = new CropCommand(fileManager, bounds);
        assertThrows(IllegalArgumentException.class, cmd::execute);
    }

    @Test
    @DisplayName("Crop throws when exceeding width")
    void testExceedsWidth() {
        Rectangle bounds = new Rectangle(150, 50, 100, 100);
        CropCommand cmd = new CropCommand(fileManager, bounds);
        assertThrows(IllegalArgumentException.class, cmd::execute);
    }

    @Test
    @DisplayName("Crop throws when exceeding height")
    void testExceedsHeight() {
        Rectangle bounds = new Rectangle(50, 150, 100, 100);
        CropCommand cmd = new CropCommand(fileManager, bounds);
        assertThrows(IllegalArgumentException.class, cmd::execute);
    }

    @Test
    @DisplayName("Crop at origin works")
    void testCropAtOrigin() {
        Rectangle bounds = new Rectangle(0, 0, 100, 100);
        CropCommand cmd = new CropCommand(fileManager, bounds);
        cmd.execute();
        assertEquals(100, fileManager.getCurrentImage().get().getWidth());
    }

    @Test
    @DisplayName("Small 2x2 crop works")
    void testSmallCrop() {
        Rectangle bounds = new Rectangle(50, 50, 2, 2);
        CropCommand cmd = new CropCommand(fileManager, bounds);
        cmd.execute();
        assertEquals(2, fileManager.getCurrentImage().get().getWidth());
    }

    @Test
    @DisplayName("Crop full image works")
    void testCropFullImage() {
        Rectangle bounds = new Rectangle(0, 0, 200, 200);
        CropCommand cmd = new CropCommand(fileManager, bounds);
        cmd.execute();
        assertEquals(200, fileManager.getCurrentImage().get().getWidth());
    }

    @Test
    @DisplayName("Description includes crop bounds")
    void testDescription() {
        Rectangle bounds = new Rectangle(10, 20, 50, 60);
        CropCommand cmd = new CropCommand(fileManager, bounds);
        String desc = cmd.getDescription();
        
        assertNotNull(desc);
        assertTrue(desc.contains("Crop"));
    }
}
