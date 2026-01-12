package com.pixelcraft.manager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.testfx.framework.junit5.ApplicationExtension;

import com.pixelcraft.event.IViewportChangeListener;
import com.pixelcraft.util.Globals;

import javafx.geometry.BoundingBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;

/**
 * Unit tests for ViewportManager class
 * 
 * @author Nathan Khupe
 */
@ExtendWith(ApplicationExtension.class)
class ViewportManagerTest {

    private ViewportManager viewportManager;
    private ScrollPane scrollPane;
    private Canvas canvas;
    private IViewportChangeListener mockListener;

    @BeforeEach
    void setUp() {
        scrollPane = new ScrollPane();
        canvas = new Canvas(800, 600);
        scrollPane.setContent(canvas);
        viewportManager = new ViewportManager(scrollPane, canvas);
        mockListener = mock(IViewportChangeListener.class);
        viewportManager.setListener(mockListener);
    }

    @Test
    @DisplayName("Initial zoom level is 1.0")
    void testInitialZoomLevel() {
        assertEquals(1.0, viewportManager.getZoomLevel(), 0.001);
    }

    @Test
    @DisplayName("Set zoom level")
    void testSetZoom() {
        viewportManager.setZoom(2.0);
        
        assertEquals(2.0, viewportManager.getZoomLevel(), 0.001);
    }

    @Test
    @DisplayName("Set zoom notifies listener")
    void testSetZoomNotifiesListener() {
        viewportManager.setZoom(1.5);
        
        verify(mockListener).onZoomChanged(1.5);
    }

    @Test
    @DisplayName("Zoom level is clamped to minimum")
    void testZoomClampedToMinimum() {
        viewportManager.setZoom(0.001);
        
        assertEquals(Globals.MIN_ZOOM, viewportManager.getZoomLevel(), 0.001);
    }

    @Test
    @DisplayName("Zoom level is clamped to maximum")
    void testZoomClampedToMaximum() {
        viewportManager.setZoom(100.0);
        
        assertEquals(Globals.MAX_ZOOM, viewportManager.getZoomLevel(), 0.001);
    }

    @Test
    @DisplayName("Zoom in increases zoom level")
    void testZoomIn() {
        double initialZoom = viewportManager.getZoomLevel();
        
        viewportManager.zoomIn();
        
        assertTrue(viewportManager.getZoomLevel() > initialZoom);
        assertEquals(initialZoom * Globals.ZOOM_STEP, viewportManager.getZoomLevel(), 0.001);
    }

    @Test
    @DisplayName("Zoom out decreases zoom level")
    void testZoomOut() {
        viewportManager.setZoom(2.0);
        double initialZoom = viewportManager.getZoomLevel();
        
        viewportManager.zoomOut();
        
        assertTrue(viewportManager.getZoomLevel() < initialZoom);
        assertEquals(initialZoom / Globals.ZOOM_STEP, viewportManager.getZoomLevel(), 0.001);
    }

    @Test
    @DisplayName("Zoom to fit calculates correct scale")
    void testZoomToFit() {
        // Create mock viewport bounds
        scrollPane = spy(scrollPane);
        when(scrollPane.getViewportBounds()).thenReturn(new BoundingBox(0, 0, 1000, 800));
        viewportManager = new ViewportManager(scrollPane, canvas);
        
        viewportManager.zoomToFit(2000, 1000);
        
        // Should fit to the smaller dimension (height: 800/1000 = 0.8 * 0.95)
        assertTrue(viewportManager.getZoomLevel() < 1.0);
        assertTrue(viewportManager.getZoomLevel() > 0);
    }

    @Test
    @DisplayName("Start panning sets panning state")
    void testStartPan() {
        assertFalse(viewportManager.isPanning());
        
        viewportManager.startPan(100, 100);
        
        assertTrue(viewportManager.isPanning());
    }

    @Test
    @DisplayName("End panning clears panning state")
    void testEndPan() {
        viewportManager.startPan(100, 100);
        assertTrue(viewportManager.isPanning());
        
        viewportManager.endPan();
        
        assertFalse(viewportManager.isPanning());
    }

    @Test
    @DisplayName("Update pan without starting does nothing")
    void testUpdatePanWithoutStart() {
        assertDoesNotThrow(() -> {
            viewportManager.updatePan(150, 150);
        });
    }

    @Test
    @DisplayName("Update pan notifies listener")
    void testUpdatePanNotifiesListener() {
        scrollPane = spy(scrollPane);
        when(scrollPane.getViewportBounds()).thenReturn(new BoundingBox(0, 0, 400, 300));
        viewportManager = new ViewportManager(scrollPane, canvas);
        viewportManager.setListener(mockListener);
        
        viewportManager.startPan(100, 100);
        viewportManager.updatePan(150, 150);
        
        verify(mockListener, atLeastOnce()).onPanChanged();
    }

    @Test
    @DisplayName("Zoom at specific point")
    void testZoomAt() {
        viewportManager.zoomAt(2.0, 400, 300);
        
        assertEquals(2.0, viewportManager.getZoomLevel(), 0.001);
    }

    @Test
    @DisplayName("Set listener without crashing")
    void testSetListener() {
        IViewportChangeListener newListener = mock(IViewportChangeListener.class);
        
        assertDoesNotThrow(() -> viewportManager.setListener(newListener));
    }

    @Test
    @DisplayName("Operations work without listener")
    void testOperationsWithoutListener() {
        ViewportManager vm = new ViewportManager(scrollPane, canvas);
        
        assertDoesNotThrow(() -> {
            vm.setZoom(1.5);
            vm.zoomIn();
            vm.zoomOut();
            vm.startPan(0, 0);
            vm.endPan();
        });
    }

    @Test
    @DisplayName("Multiple zoom operations")
    void testMultipleZoomOperations() {
        viewportManager.zoomIn();
        viewportManager.zoomIn();
        double afterTwoZoomIns = viewportManager.getZoomLevel();
        
        viewportManager.zoomOut();
        double afterOneZoomOut = viewportManager.getZoomLevel();
        
        assertTrue(afterOneZoomOut < afterTwoZoomIns);
        assertTrue(afterOneZoomOut > 1.0);
    }

    @Test
    @DisplayName("Zoom level remains in bounds after multiple operations")
    void testZoomBoundsAfterMultipleOperations() {
        // Try to zoom in many times
        for (int i = 0; i < 100; i++) {
            viewportManager.zoomIn();
        }
        
        assertTrue(viewportManager.getZoomLevel() <= Globals.MAX_ZOOM);
        
        // Try to zoom out many times
        for (int i = 0; i < 200; i++) {
            viewportManager.zoomOut();
        }
        
        assertTrue(viewportManager.getZoomLevel() >= Globals.MIN_ZOOM);
    }
}
