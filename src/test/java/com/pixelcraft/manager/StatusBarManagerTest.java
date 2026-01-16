package com.pixelcraft.manager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.Pair;

/**
 * Unit tests for StatusBarManager class
 * 
 * @author Nathan Khupe
 */
@ExtendWith(ApplicationExtension.class)
class StatusBarManagerTest {

    private StatusBarManager statusBarManager;
    private Label lblImageSize;
    private Label lblFileSize;
    private Label lblPosition;
    private Label lblMode;
    private ComboBox<Pair<String, Double>> cmbZoomPresets;

    @BeforeEach
    void setUp() {
        lblImageSize = new Label();
        lblFileSize = new Label();
        lblPosition = new Label();
        lblMode = new Label();
        cmbZoomPresets = new ComboBox<>();
        
        statusBarManager = new StatusBarManager(lblImageSize, lblFileSize, lblPosition, lblMode, cmbZoomPresets);
    }

    @Test
    @DisplayName("Update image size displays correct format")
    void testUpdateImageSize() {
        statusBarManager.updateImageSize(800, 600);
        
        assertEquals("800 x 600 px", lblImageSize.getText());
    }

    @Test
    @DisplayName("Update image size with decimal values")
    void testUpdateImageSizeWithDecimals() {
        statusBarManager.updateImageSize(1920.5, 1080.7);
        
        assertEquals("1921 x 1081 px", lblImageSize.getText());
    }

    @Test
    @DisplayName("Update position displays correct format")
    void testUpdatePosition() {
        statusBarManager.updatePosition(150, 250);
        
        assertEquals(String.format("%15s", "(150, 250)"), lblPosition.getText());
    }

    @Test
    @DisplayName("Update position with decimal values")
    void testUpdatePositionWithDecimals() {
        statusBarManager.updatePosition(123.7, 456.2);
        
        assertEquals(String.format("%15s", "(124, 456)"), lblPosition.getText());
    }

    @Test
    @DisplayName("Update position with negative values")
    void testUpdatePositionWithNegativeValues() {
        statusBarManager.updatePosition(-10, -20);
        
        assertEquals(String.format("%15s", "(-10, -20)"), lblPosition.getText());
    }

    @Test
    @DisplayName("Update mode displays correctly")
    void testUpdateMode() {
        statusBarManager.updateMode("Drawing");
        
        assertEquals("Mode: Drawing", lblMode.getText());
    }

    @Test
    @DisplayName("Update mode with different values")
    void testUpdateModeWithDifferentValues() {
        statusBarManager.updateMode("Selection");
        assertEquals("Mode: Selection", lblMode.getText());
        
        statusBarManager.updateMode("Eraser");
        assertEquals("Mode: Eraser", lblMode.getText());
    }

    @Test
    @DisplayName("Update zoom displays percentage")
    void testUpdateZoom() {
        statusBarManager.updateZoom(1.0);
        
        // The button cell should show "100%"
        assertNotNull(cmbZoomPresets.getButtonCell());
    }

    @Test
    @DisplayName("Update zoom with different values")
    void testUpdateZoomWithDifferentValues() {
        statusBarManager.updateZoom(0.5);
        statusBarManager.updateZoom(2.0);
        statusBarManager.updateZoom(1.5);
        
        // Should not throw exceptions
        assertDoesNotThrow(() -> statusBarManager.updateZoom(3.0));
    }

    @Test
    @DisplayName("Update zoom selects matching preset")
    void testUpdateZoomSelectsMatchingPreset() throws Exception {
        statusBarManager.updateZoom(1.0);
        
        // Wait for Platform.runLater to complete
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        javafx.application.Platform.runLater(latch::countDown);
        latch.await();
        
        // Should select the 100% preset
        Pair<String, Double> selected = cmbZoomPresets.getSelectionModel().getSelectedItem();
        assertNotNull(selected);
        assertEquals(1.0, selected.getValue(), 0.001);
    }

    @Test
    @DisplayName("Update zoom with non-preset value clears selection")
    void testUpdateZoomWithNonPresetValue() throws Exception {
        statusBarManager.updateZoom(1.0); // Select a preset first
        
        // Wait for Platform.runLater to complete
        java.util.concurrent.CountDownLatch latch1 = new java.util.concurrent.CountDownLatch(1);
        javafx.application.Platform.runLater(latch1::countDown);
        latch1.await();
        
        statusBarManager.updateZoom(1.37); // Non-preset value
        
        // Wait for Platform.runLater to complete
        java.util.concurrent.CountDownLatch latch2 = new java.util.concurrent.CountDownLatch(1);
        javafx.application.Platform.runLater(latch2::countDown);
        latch2.await();
        
        // Selection should be cleared
        assertNull(cmbZoomPresets.getSelectionModel().getSelectedItem());
    }

    @Test
    @DisplayName("Zoom presets are initialized")
    void testZoomPresetsInitialized() {
        assertTrue(cmbZoomPresets.getItems().size() > 0, "Zoom presets should be initialized");
    }

    @Test
    @DisplayName("Zoom presets contain standard values")
    void testZoomPresetsContainStandardValues() {
        boolean has100Percent = cmbZoomPresets.getItems().stream()
            .anyMatch(pair -> pair.getValue() == 1.0);
        boolean has50Percent = cmbZoomPresets.getItems().stream()
            .anyMatch(pair -> pair.getValue() == 0.5);
        boolean has200Percent = cmbZoomPresets.getItems().stream()
            .anyMatch(pair -> pair.getValue() == 2.0);
        
        assertTrue(has100Percent, "Should have 100% preset");
        assertTrue(has50Percent, "Should have 50% preset");
        assertTrue(has200Percent, "Should have 200% preset");
    }

    @Test
    @DisplayName("Zoom presets are properly formatted")
    void testZoomPresetsFormatted() {
        for (Pair<String, Double> preset : cmbZoomPresets.getItems()) {
            assertNotNull(preset.getKey(), "Preset label should not be null");
            assertNotNull(preset.getValue(), "Preset value should not be null");
            assertTrue(preset.getKey().endsWith("%"), "Preset label should end with %");
        }
    }

    @Test
    @DisplayName("Multiple updates work correctly")
    void testMultipleUpdates() {
        statusBarManager.updateImageSize(100, 100);
        statusBarManager.updatePosition(50, 50);
        statusBarManager.updateMode("Test");
        statusBarManager.updateZoom(1.0);
        
        statusBarManager.updateImageSize(200, 200);
        statusBarManager.updatePosition(75, 75);
        statusBarManager.updateMode("Another");
        statusBarManager.updateZoom(2.0);
        
        assertEquals("200 x 200 px", lblImageSize.getText());
        assertEquals(String.format("%15s", "(75, 75)"), lblPosition.getText());
        assertEquals("Mode: Another", lblMode.getText());
    }

    @Test
    @DisplayName("Update with zero values")
    void testUpdateWithZeroValues() {
        statusBarManager.updateImageSize(0, 0);
        statusBarManager.updatePosition(0, 0);
        statusBarManager.updateZoom(0.1);
        
        assertEquals("0 x 0 px", lblImageSize.getText());
        assertEquals(String.format("%15s", "(0, 0)"), lblPosition.getText());
    }

    @Test
    @DisplayName("Update with large values")
    void testUpdateWithLargeValues() {
        statusBarManager.updateImageSize(99999, 99999);
        statusBarManager.updatePosition(99999, 99999);
        
        assertTrue(lblImageSize.getText().contains("99999"));
        assertTrue(lblPosition.getText().contains("99999"));
    }

    @Test
    @DisplayName("updateFileSize displays bytes for values < 1024")
    void testUpdateFileSizeBytes() {
        statusBarManager.updateFileSize(512);
        assertEquals(String.format("Size: %8s", "512.00 bytes"), lblFileSize.getText());
    }

    @Test
    @DisplayName("updateFileSize displays KB for values >= 1024 and < 1MB")
    void testUpdateFileSizeKilobytes() {
        statusBarManager.updateFileSize(1536); // 1.5 KB
        assertEquals(String.format("Size: %8s", "1.50 KB"), lblFileSize.getText());
    }

    @Test
    @DisplayName("updateFileSize displays MB for values >= 1MB and < 1GB")
    void testUpdateFileSizeMegabytes() {
        statusBarManager.updateFileSize(1024L * 1024L); // 1 MB
        assertEquals(String.format("Size: %8s", "1.00 MB"), lblFileSize.getText());
    }

    @Test
    @DisplayName("updateFileSize displays GB for values >= 1GB")
    void testUpdateFileSizeGigabytes() {
        statusBarManager.updateFileSize(1024L * 1024L * 1024L); // 1 GB
        assertEquals(String.format("Size: %8s", "1.00 GB"), lblFileSize.getText());
    }

    @Test
    @DisplayName("updateFileSize handles zero correctly")
    void testUpdateFileSizeZero() {
        statusBarManager.updateFileSize(0);
        assertEquals(String.format("Size: %8s", "0.00 bytes"), lblFileSize.getText());
    }
}
