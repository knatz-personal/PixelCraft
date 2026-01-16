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
 * Debug tests to verify snapshot mechanism
 */
@ExtendWith(ApplicationExtension.class)
@DisplayName("CropCommand Debug Tests")
class CropCommandDebugTest {

    private FileManager fileManager;

    @BeforeEach
    void setUp() {
        fileManager = new FileManager();
        RasterImage image = new RasterImage(200, 200);
        fileManager.setCurrentImage(image);
    }

    @Test
    @DisplayName("Snapshot is taken before execute")
    void testSnapshotTaken() throws Exception {
        RasterImage image = new RasterImage(200, 200);
        fileManager.setCurrentImage(image);

        Rectangle bounds = new Rectangle(50, 50, 100, 100);
        CropCommand cmd = new CropCommand(fileManager, bounds);

        // Use reflection to check snapshot
        Field snapshotField = cmd.getClass().getSuperclass().getDeclaredField("snapshot");
        snapshotField.setAccessible(true);

        // Before execute
        RasterImage snapshotBefore = (RasterImage) snapshotField.get(cmd);
        System.out.println("Snapshot before execute: " + snapshotBefore);

        // After takeSnapshot
        cmd.execute();
        RasterImage snapshotAfter = (RasterImage) snapshotField.get(cmd);
        System.out.println("Snapshot after execute: " + snapshotAfter);

        // Check if getCurrentImage was set properly before crop
        RasterImage beforeCrop = new RasterImage(200, 200);
        fileManager.setCurrentImage(beforeCrop);
        
        // Take snapshot manually to debug
        Field snapshotField2 = cmd.getClass().getSuperclass().getDeclaredField("snapshot");
        snapshotField2.setAccessible(true);
        
        cmd.takeSnapshot();
        RasterImage snapshotManual = (RasterImage) snapshotField2.get(cmd);
        System.out.println("Manual snapshot: " + snapshotManual);
        assertNotNull(snapshotManual, "Snapshot should not be null after takeSnapshot()");
    }

    @Test
    @DisplayName("Crop command updates FileManager")
    void testCropUpdatesFileManager() {
        RasterImage original = new RasterImage(200, 200);
        fileManager.setCurrentImage(original);
        
        assertEquals(200, fileManager.getCurrentImage().get().getWidth());

        Rectangle bounds = new Rectangle(50, 50, 100, 100);
        CropCommand cmd = new CropCommand(fileManager, bounds);
        cmd.execute();

        RasterImage after = fileManager.getCurrentImage().get();
        System.out.println("After crop: " + after.getWidth() + "x" + after.getHeight());
        assertEquals(100, after.getWidth(), "Width should be cropped to 100");
        assertEquals(100, after.getHeight(), "Height should be cropped to 100");
    }
}
