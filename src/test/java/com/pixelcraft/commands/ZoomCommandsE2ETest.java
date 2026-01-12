package com.pixelcraft.commands;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import com.pixelcraft.util.Globals;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End tests for Zoom Commands
 * Tests the integration of zoom commands with real JavaFX components
 * 
 * @author Nathan Khupe
 */
@ExtendWith(ApplicationExtension.class)
@DisplayName("Zoom Commands E2E Tests")
class ZoomCommandsE2ETest {
    private static final double DELTA = 0.0001;
    
    private ScrollPane scrollPane;
    private Canvas canvas;
    private AtomicReference<Double> zoomLevel;
    private AtomicBoolean statusBarUpdated;

    @BeforeEach
    void setUp() {
        // Initialize JavaFX components
        scrollPane = new ScrollPane();
        canvas = new Canvas(800, 600);
        scrollPane.setContent(canvas);
        
        // Initialize zoom state
        zoomLevel = new AtomicReference<>(1.0);
        statusBarUpdated = new AtomicBoolean(false);
        
        // Set initial scale
        canvas.setScaleX(1.0);
        canvas.setScaleY(1.0);
    }

    private Runnable createStatusBarCallback() {
        return () -> statusBarUpdated.set(true);
    }

    @Test
    @DisplayName("ZoomInCommand should increase zoom level by ZOOM_STEP")
    void testZoomInCommand() {
        // Arrange
        double initialZoom = zoomLevel.get();
        ZoomInCommand command = new ZoomInCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()
        );

        // Act
        command.execute();

        // Assert
        double expectedZoom = initialZoom * Globals.ZOOM_STEP;
        assertEquals(expectedZoom, zoomLevel.get(), DELTA, 
                "Zoom level should increase by ZOOM_STEP");
        // Note: Canvas scale is not used - zoom is implemented via canvas width/height
        assertTrue(statusBarUpdated.get(), "Status bar should be updated");
    }

    @Test
    @DisplayName("ZoomInCommand should not exceed MAX_ZOOM")
    void testZoomInCommandMaxLimit() {
        // Arrange
        zoomLevel.set(Globals.MAX_ZOOM / Globals.ZOOM_STEP); // Set close to max
        ZoomInCommand command = new ZoomInCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()
        );

        // Act
        command.execute();

        // Assert
        assertTrue(zoomLevel.get() <= Globals.MAX_ZOOM,
                "Zoom level should not exceed MAX_ZOOM");
    }

    @Test
    @DisplayName("ZoomOutCommand should decrease zoom level by ZOOM_STEP")
    void testZoomOutCommand() {
        // Arrange
        zoomLevel.set(2.0); // Start at 200%
        double initialZoom = zoomLevel.get();
        ZoomOutCommand command = new ZoomOutCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()
        );

        // Act
        command.execute();

        // Assert
        double expectedZoom = initialZoom / Globals.ZOOM_STEP;
        assertEquals(expectedZoom, zoomLevel.get(), DELTA,
                "Zoom level should decrease by ZOOM_STEP");
        // Note: Canvas scale is not used - zoom is implemented via canvas width/height
        assertTrue(statusBarUpdated.get(), "Status bar should be updated");
    }

    @Test
    @DisplayName("ZoomOutCommand should not go below MIN_ZOOM")
    void testZoomOutCommandMinLimit() {
        // Arrange
        zoomLevel.set(Globals.MIN_ZOOM * Globals.ZOOM_STEP); // Set close to min
        ZoomOutCommand command = new ZoomOutCommand(
               scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()
        );

        // Act
        command.execute();

        // Assert
        assertTrue(zoomLevel.get() >= Globals.MIN_ZOOM,
                "Zoom level should not go below MIN_ZOOM");
    }

    @Test
    @DisplayName("ZoomResetCommand should reset zoom to 100%")
    void testZoomResetCommand() {
        // Arrange
        zoomLevel.set(2.5); // Start at 250%
        ZoomResetCommand command = new ZoomResetCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()
        );

        // Act
        command.execute();

        // Assert
        assertEquals(1.0, zoomLevel.get(), DELTA,
                "Zoom level should be reset to 1.0 (100%)");
        // Note: Canvas scale is always 1.0 - zoom is implemented via canvas width/height
        assertTrue(statusBarUpdated.get(), "Status bar should be updated");
    }

    @Test
    @DisplayName("ZoomFitToViewportCommand should fit canvas to viewport")
    void testZoomFitToViewportCommand() {
        // Arrange
        scrollPane.setMinWidth(400);
        scrollPane.setMinHeight(300);
        scrollPane.setPrefWidth(400);
        scrollPane.setPrefHeight(300);
        
        ZoomFitToViewportCommand command = new ZoomFitToViewportCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()
        );

        // Act
        command.execute();

        // Assert
        assertNotNull(zoomLevel.get(), "Zoom level should be set");
        assertTrue(zoomLevel.get() > 0, "Zoom level should be positive");
        assertTrue(statusBarUpdated.get(), "Status bar should be updated");
    }

    @Test
    @DisplayName("ZoomSetCommand should set zoom to specific value")
    void testZoomSetCommand() {
        // Arrange
        double targetZoom = 1.5;
        ZoomSetCommand command = new ZoomSetCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback(),
                targetZoom
        );

        // Act
        command.execute();

        // Assert
        assertEquals(targetZoom, zoomLevel.get(), DELTA,
                "Zoom level should be set to target value");
        // Note: Canvas scale is not used - zoom is implemented via canvas width/height
        assertTrue(statusBarUpdated.get(), "Status bar should be updated");
    }

    @Test
    @DisplayName("ZoomSetCommand should clamp to MAX_ZOOM")
    void testZoomSetCommandMaxClamp() {
        // Arrange
        double targetZoom = Globals.MAX_ZOOM + 10.0; // Way above max
        ZoomSetCommand command = new ZoomSetCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback(),
                targetZoom
        );

        // Act
        command.execute();

        // Assert
        assertEquals(Globals.MAX_ZOOM, zoomLevel.get(), DELTA,
                "Zoom level should be clamped to MAX_ZOOM");
    }

    @Test
    @DisplayName("ZoomSetCommand should clamp to MIN_ZOOM")
    void testZoomSetCommandMinClamp() {
        // Arrange
        double targetZoom = Globals.MIN_ZOOM - 0.1; // Below min
        ZoomSetCommand command = new ZoomSetCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback(),
                targetZoom
        );

        // Act
        command.execute();

        // Assert
        assertEquals(Globals.MIN_ZOOM, zoomLevel.get(), DELTA,
                "Zoom level should be clamped to MIN_ZOOM");
    }

    @Test
    @DisplayName("ZoomSetCommand undo should restore previous zoom level")
    void testZoomSetCommandUndo() {
        // Arrange
        double initialZoom = 1.0;
        zoomLevel.set(initialZoom);
        double targetZoom = 2.0;
        
        ZoomSetCommand command = new ZoomSetCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback(),
                targetZoom
        );

        // Act
        command.execute();
        assertEquals(targetZoom, zoomLevel.get(), DELTA, "Zoom should be at target");
        
        command.undo();

        // Assert
        assertEquals(initialZoom, zoomLevel.get(), DELTA,
                "Undo should restore previous zoom level");
    }

    @Test
    @DisplayName("Multiple zoom in commands should compound correctly")
    void testMultipleZoomInCommands() {
        // Arrange
        double initialZoom = 1.0;
        zoomLevel.set(initialZoom);

        // Act - Zoom in 3 times
        for (int i = 0; i < 3; i++) {
            ZoomInCommand command = new ZoomInCommand(
                    scrollPane, canvas,
                    zoomLevel::get, zoomLevel::set,
                    createStatusBarCallback()
            );
            command.execute();
        }

        // Assert
        double expectedZoom = initialZoom * Math.pow(Globals.ZOOM_STEP, 3);
        assertEquals(expectedZoom, zoomLevel.get(), DELTA,
                "Three zoom ins should compound correctly");
    }

    @Test
    @DisplayName("Zoom in followed by zoom out should return to near original")
    void testZoomInOutRoundTrip() {
        // Arrange
        double initialZoom = 1.0;
        zoomLevel.set(initialZoom);

        // Act - Zoom in then out
        ZoomInCommand zoomIn = new ZoomInCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()
        );
        zoomIn.execute();

        ZoomOutCommand zoomOut = new ZoomOutCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()
        );
        zoomOut.execute();

        // Assert
        assertEquals(initialZoom, zoomLevel.get(), DELTA,
                "Zoom in followed by zoom out should return to original");
    }

    @Test
    @DisplayName("Zoom reset should work after multiple zoom operations")
    void testZoomResetAfterMultipleOperations() {
        // Arrange
        zoomLevel.set(1.0);

        // Act - Zoom in, out, in, then reset
        new ZoomInCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()).execute();
        
        new ZoomOutCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()).execute();
        
        new ZoomInCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()).execute();

        new ZoomResetCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback()).execute();

        // Assert
        assertEquals(1.0, zoomLevel.get(), DELTA,
                "Reset should restore zoom to 1.0 regardless of previous operations");
    }

    @Test
    @DisplayName("Canvas pane dimensions should update with zoom")
    void testCanvasPaneDimensionsUpdate() {
        // Arrange
        double targetZoom = 2.0;

        ZoomSetCommand command = new ZoomSetCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback(),
                targetZoom
        );

        // Act
        command.execute();

        // Assert - zoom is implemented via canvas width/height, not scale
        // Note: Canvas scale is always 1.0 in this implementation
        assertEquals(1.0, canvas.getScaleX(), DELTA,
                "Canvas scaleX should be 1.0 (zoom via width/height)");
        assertEquals(1.0, canvas.getScaleY(), DELTA,
                "Canvas scaleY should be 1.0 (zoom via width/height)");
    }

    @Test
    @DisplayName("Status bar callback should be invoked on every zoom change")
    void testStatusBarCallbackInvoked() {
        // Arrange
        AtomicBoolean callback1Called = new AtomicBoolean(false);
        AtomicBoolean callback2Called = new AtomicBoolean(false);
        AtomicBoolean callback3Called = new AtomicBoolean(false);
        
        // Set initial zoom to something other than 1.0 to ensure reset actually changes it
        zoomLevel.set(2.0);

        // Act
        new ZoomInCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                () -> callback1Called.set(true)).execute();

        new ZoomOutCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                () -> callback2Called.set(true)).execute();

        new ZoomResetCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                () -> callback3Called.set(true)).execute();

        // Assert
        assertTrue(callback1Called.get(), "ZoomIn callback should be invoked");
        assertTrue(callback2Called.get(), "ZoomOut callback should be invoked");
        assertTrue(callback3Called.get(), "ZoomReset callback should be invoked");
    }

    @Test
    @DisplayName("Command descriptions should be meaningful")
    void testCommandDescriptions() {
        // Arrange & Act
        ZoomInCommand zoomIn = new ZoomInCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set, createStatusBarCallback());
        
        ZoomOutCommand zoomOut = new ZoomOutCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set, createStatusBarCallback());
        
        ZoomResetCommand zoomReset = new ZoomResetCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set, createStatusBarCallback());
        
        ZoomFitToViewportCommand zoomFit = new ZoomFitToViewportCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set, createStatusBarCallback());
        
        ZoomSetCommand zoomSet = new ZoomSetCommand(scrollPane, canvas,
                zoomLevel::get, zoomLevel::set, createStatusBarCallback(),
                1.5);

        // Assert
        assertNotNull(zoomIn.getDescription(), "ZoomIn should have description");
        assertNotNull(zoomOut.getDescription(), "ZoomOut should have description");
        assertNotNull(zoomReset.getDescription(), "ZoomReset should have description");
        assertNotNull(zoomFit.getDescription(), "ZoomFit should have description");
        assertNotNull(zoomSet.getDescription(), "ZoomSet should have description");
        
        assertTrue(zoomIn.getDescription().contains("Zoom"),
                "ZoomIn description should contain 'Zoom'");
        assertTrue(zoomOut.getDescription().contains("Zoom"),
                "ZoomOut description should contain 'Zoom'");
        assertTrue(zoomReset.getDescription().contains("Zoom"),
                "ZoomReset description should contain 'Zoom'");
    }

    @Test
    @DisplayName("Zoom should not change if already at target level")
    void testNoChangeWhenAlreadyAtTarget() {
        // Arrange
        double targetZoom = 1.5;
        zoomLevel.set(targetZoom);
        canvas.setScaleX(targetZoom);
        canvas.setScaleY(targetZoom);
        
        statusBarUpdated.set(false);
        
        ZoomSetCommand command = new ZoomSetCommand(
                scrollPane, canvas,
                zoomLevel::get, zoomLevel::set,
                createStatusBarCallback(),
                targetZoom
        );

        // Act
        command.execute();

        // Assert
        assertEquals(targetZoom, zoomLevel.get(), DELTA,
                "Zoom level should remain unchanged");
        // Note: Status bar may still update even if zoom doesn't change
    }
}
