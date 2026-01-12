package com.pixelcraft;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/**
 * Unit tests for PixelCraft application
 * 
 * @author Nathan Khupe
 */
@ExtendWith(ApplicationExtension.class)
public class PixelCraftTest {

    @Test
    @DisplayName("PixelCraft instance can be created")
    void testPixelCraftInstanceCreation() {
        PixelCraft app = new PixelCraft();
        assertNotNull(app, "PixelCraft instance should not be null");
    }

    @Test
    @DisplayName("Multiple PixelCraft instances can be created")
    void testMultipleInstancesCreation() {
        PixelCraft app1 = new PixelCraft();
        PixelCraft app2 = new PixelCraft();
        
        assertNotNull(app1, "First instance should not be null");
        assertNotNull(app2, "Second instance should not be null");
        assertNotSame(app1, app2, "Instances should be different objects");
    }

    @Test
    @DisplayName("PixelCraft extends JavaFX Application")
    void testPixelCraftExtendsApplication() {
        PixelCraft app = new PixelCraft();
        assertTrue(app instanceof javafx.application.Application, 
                   "PixelCraft should extend JavaFX Application");
    }

    @Test
    @DisplayName("Application instance is valid")
    void testApplicationInstanceValid()
    {
        PixelCraft app = new PixelCraft();
        assertNotNull(app, "Application instance should not be null");
        assertTrue(app instanceof javafx.application.Application, 
                   "Should be instance of JavaFX Application");
    }
}
