package com.pixelcraft.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import com.pixelcraft.model.RasterImage;

import javafx.scene.shape.Rectangle;

/**
 * Debug tests to diagnose crop issues
 */
@ExtendWith(ApplicationExtension.class)
@DisplayName("RasterImage Crop Debug Tests")
class RasterImageCropDebugTest {

    @Test
    @DisplayName("RasterImage.crop() works directly")
    void testRasterImageCropDirect() {
        // Test if RasterImage.crop() actually works
        RasterImage image = new RasterImage(200, 200);
        assertNotNull(image);
        assertEquals(200, image.getWidth());
        assertEquals(200, image.getHeight());

        // Crop it
        Rectangle bounds = new Rectangle(50, 50, 100, 100);
        RasterImage cropped = image.crop(bounds);
        
        assertNotNull(cropped);
        assertEquals(100, cropped.getWidth(), "Cropped width should be 100");
        assertEquals(100, cropped.getHeight(), "Cropped height should be 100");
    }

    @Test
    @DisplayName("RasterImage.crop() with direct int parameters")
    void testRasterImageCropDirectParams() {
        RasterImage image = new RasterImage(200, 200);
        RasterImage cropped = image.crop(50, 50, 100, 100);
        
        assertEquals(100, cropped.getWidth());
        assertEquals(100, cropped.getHeight());
    }
}
