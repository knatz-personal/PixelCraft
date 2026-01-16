package com.pixelcraft.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import com.pixelcraft.manager.FileManager;
import com.pixelcraft.model.RasterImage;

import javafx.scene.shape.Rectangle;

/**
 * Debug undo functionality
 */
@ExtendWith(ApplicationExtension.class)
@DisplayName("CropCommand Undo Debug")
class CropCommandUndoDebugTest {

    private FileManager fileManager;

    @BeforeEach
    void setUp() {
        fileManager = new FileManager();
        RasterImage image = new RasterImage(200, 200);
        fileManager.setCurrentImage(image);
    }

    @Test
    @DisplayName("Debug undo step by step")
    void testUndoDebug() throws Exception {
        System.out.println("Step 1: Initial image");
        assertEquals(200, fileManager.getCurrentImage().get().getWidth());

        Rectangle bounds = new Rectangle(50, 50, 100, 100);
        CropCommand cmd = new CropCommand(fileManager, bounds);

        System.out.println("Step 2: Execute crop");
        cmd.execute();
        RasterImage afterCrop = fileManager.getCurrentImage().get();
        System.out.println("After crop: " + afterCrop.getWidth() + "x" + afterCrop.getHeight());
        assertEquals(100, afterCrop.getWidth());

        // Check snapshot
        Field snapshotField = cmd.getClass().getSuperclass().getDeclaredField("snapshot");
        snapshotField.setAccessible(true);
        RasterImage snapshot = (RasterImage) snapshotField.get(cmd);
        System.out.println("Snapshot size: " + snapshot.getWidth() + "x" + snapshot.getHeight());
        assertEquals(200, snapshot.getWidth(), "Snapshot should be 200x200");

        System.out.println("Step 3: Call undo");
        cmd.undo();
        RasterImage afterUndo = fileManager.getCurrentImage().get();
        System.out.println("After undo: " + afterUndo.getWidth() + "x" + afterUndo.getHeight());
        assertEquals(200, afterUndo.getWidth(), "After undo should be 200x200");
    }
}
