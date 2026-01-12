package com.pixelcraft.manager;

import com.pixelcraft.event.IViewportChangeListener;
import com.pixelcraft.util.Globals;
import com.pixelcraft.util.logging.LoggerFactory;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;

public final class ViewportManager {
    private static final com.pixelcraft.util.logging.Logger LOG = LoggerFactory.getLogger(ViewportManager.class);
    private static final double ZOOM_FIT_PADDING = 0.98;

    private final ScrollPane scrollPane;
    private final Canvas canvas;
    private double zoomLevel = 1.0;
    private IViewportChangeListener listener;

    // Panning state
    private boolean isPanning = false;
    private double panStartX;
    private double panStartY;
    private double scrollStartH;
    private double scrollStartV;

    public ViewportManager(ScrollPane scrollPane, Canvas canvas) {
        this.scrollPane = scrollPane;
        this.canvas = canvas;
    }

    public void setListener(IViewportChangeListener listener) {
        this.listener = listener;
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoom(double zoom) {
        LOG.info("setZoom(" + zoom + ") - current=" + zoomLevel);
        double clamped = clamp(zoom, Globals.MIN_ZOOM, Globals.MAX_ZOOM);
        if (clamped == zoomLevel) {
            LOG.info("setZoom() - no change needed");
            return;
        }
        this.zoomLevel = clamped;
        LOG.info("setZoom() - new level=" + zoomLevel + ", notifying listener");
        notifyZoomChanged(); // listener should resize canvas & redraw
    }

    public void zoomIn() {
        setZoom(zoomLevel * Globals.ZOOM_STEP);
    }

    public void zoomOut() {
        setZoom(zoomLevel / Globals.ZOOM_STEP);
    }

    public void zoomToFit(double imageWidth, double imageHeight) {
        LOG.info("zoomToFit(" + imageWidth + ", " + imageHeight + ")");
        if (imageWidth <= 0 || imageHeight <= 0) {
            LOG.warn("zoomToFit() - invalid image dimensions");
            return;
        }

        Bounds viewportBounds = scrollPane.getViewportBounds();
        double viewportWidth = viewportBounds.getWidth();
        double viewportHeight = viewportBounds.getHeight();
        LOG.info("zoomToFit() - viewport: " + viewportWidth + "x" + viewportHeight);
        if (viewportWidth <= 0 || viewportHeight <= 0) {
            LOG.warn("zoomToFit() - invalid viewport, defaulting to 1.0");
            setZoom(1.0);
            return;
        }

        double scaleX = viewportWidth / imageWidth;
        double scaleY = viewportHeight / imageHeight;
        double fitZoom = Math.min(scaleX, scaleY) * ZOOM_FIT_PADDING;
        LOG.info("zoomToFit() - scaleX=" + scaleX + ", scaleY=" + scaleY + ", fitZoom=" + fitZoom);
        setZoom(fitZoom);
    }

    public void zoomAt(double newZoom, double pivotX, double pivotY) {
        double oldZoom = zoomLevel;

        // Adjust scroll using old content size BEFORE listener resizes the canvas
        double zoomRatio = newZoom / oldZoom;
        adjustScrollForZoom(pivotX, pivotY, zoomRatio);

        // Now set zoom (listener will resize canvas & redraw)
        setZoom(newZoom);
    }

    public void startPan(double viewportX, double viewportY) {
        isPanning = true;
        panStartX = viewportX;
        panStartY = viewportY;
        scrollStartH = scrollPane.getHvalue();
        scrollStartV = scrollPane.getVvalue();
    }

    public void updatePan(double viewportX, double viewportY) {
        if (!isPanning) return;

        double deltaX = viewportX - panStartX;
        double deltaY = viewportY - panStartY;

        // Get viewport bounds for calculations
        Bounds vp = scrollPane.getViewportBounds();
        double viewportW = vp.getWidth();
        double viewportH = vp.getHeight();

        // Calculate content size (canvas size)
        double contentW = canvas.getWidth();
        double contentH = canvas.getHeight();

        // Calculate scrollable range (content that extends beyond viewport)
        double hRange = Math.max(0.0, contentW - viewportW);
        double vRange = Math.max(0.0, contentH - viewportH);

        // Convert pixel delta to normalized scroll value (0.0 to 1.0)
        // Negative delta because dragging right (positive delta) should scroll content left (decrease scroll value)
        // But JavaFX scroll values work opposite: higher value = scrolled more to the right/down
        // So: drag right (positive deltaX) -> want to see content on the left -> decrease scroll value
        double hDelta = (hRange > 0) ? (-deltaX / hRange) : 0.0;
        double vDelta = (vRange > 0) ? (-deltaY / vRange) : 0.0;

        // Apply new scroll values, clamped to valid range
        scrollPane.setHvalue(clamp(scrollStartH + hDelta, 0.0, 1.0));
        scrollPane.setVvalue(clamp(scrollStartV + vDelta, 0.0, 1.0));

        notifyPanChanged();
    }

    public void endPan() {
        isPanning = false;
    }

    public boolean isPanning() {
        return isPanning;
    }

    private void adjustScrollForZoom(double pivotX, double pivotY, double zoomRatio) {
        Bounds vp = scrollPane.getViewportBounds();
        double viewportW = vp.getWidth();
        double viewportH = vp.getHeight();

        double contentWOld = canvas.getWidth();
        double contentHOld = canvas.getHeight();
        if (viewportW <= 0 || viewportH <= 0 || contentWOld <= 0 || contentHOld <= 0) return;

        double contentWNew = contentWOld * zoomRatio;
        double contentHNew = contentHOld * zoomRatio;

        double viewOriginXOld = scrollPane.getHvalue() * Math.max(1.0, contentWOld - viewportW);
        double viewOriginYOld = scrollPane.getVvalue() * Math.max(1.0, contentHOld - viewportH);

        double pivotContentXOld = viewOriginXOld + pivotX;
        double pivotContentYOld = viewOriginYOld + pivotY;

        double pivotContentXNew = pivotContentXOld * zoomRatio;
        double pivotContentYNew = pivotContentYOld * zoomRatio;

        double viewOriginXNew = pivotContentXNew - pivotX;
        double viewOriginYNew = pivotContentYNew - pivotY;

        double hRangeNew = Math.max(1.0, contentWNew - viewportW);
        double vRangeNew = Math.max(1.0, contentHNew - viewportH);

        double hNew = clamp(viewOriginXNew / hRangeNew, 0.0, 1.0);
        double vNew = clamp(viewOriginYNew / vRangeNew, 0.0, 1.0);

        scrollPane.setHvalue(hNew);
        scrollPane.setVvalue(vNew);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void notifyZoomChanged() {
        if (listener != null) {
            listener.onZoomChanged(zoomLevel);
        }
    }

    private void notifyPanChanged() {
        if (listener != null) {
            listener.onPanChanged();
        }
    }
}
