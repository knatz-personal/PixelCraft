package com.pixelcraft.tools;

import java.util.function.Supplier;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SelectionTool extends ToolBase {

    private double startX, startY;
    private double currentX, currentY;

    private boolean isDragging;
    private boolean hasSelection;
    private boolean constrainToSquare = false; 
    private Rectangle selectionBounds;

    private final Canvas overlayCanvas;
    private final Supplier<Double> zoomGetter;
    private final Supplier<Integer> imageWidthGetter;
    private final Supplier<Integer> imageHeightGetter;

    public SelectionTool(Canvas overlayCanvas, Supplier<Integer> imageWidthGetter, Supplier<Integer> imageHeightGetter, Supplier<Double> zoomGetter) {
        super();
        this.overlayCanvas = overlayCanvas;
        this.imageWidthGetter = imageWidthGetter;
        this.imageHeightGetter = imageHeightGetter;
        this.zoomGetter = zoomGetter;
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        Point2D imageCoords = canvasToImage(event.getX(), event.getY());
        startX = imageCoords.getX();
        startY = imageCoords.getY();
        isDragging = true;
        clearSelection();
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        Point2D imageCoords = canvasToImage(event.getX(), event.getY());
        currentX = imageCoords.getX();
        currentY = imageCoords.getY();

        drawSelectionOverlay();
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        isDragging = false;
        Rectangle bounds = calculateBounds();
        if (bounds.getWidth() > 0 && bounds.getHeight() > 0) {
            selectionBounds = bounds;
            hasSelection = true;
        }
        drawSelectionOverlay();
    }

    @Override
    public String getName() {
        return "Selection";
    }

    @Override
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    //#region Public API
    // Get the selection for CropCommand
    public Rectangle getSelectionBounds() {
        return hasSelection ? selectionBounds : null;
    }

    // Check if there's an active selection
    public boolean hasActiveSelection() {
        return hasSelection;
    }

    // Clear the selection (e.g., after crop, or on Escape key)
    public void clearSelection() {
        hasSelection = false;
        selectionBounds = null;

        // Clear overlay
        GraphicsContext gc = overlayCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());
    }

    // For constraint keys (Shift = square selection)
    public void setConstrainToSquare(boolean constrain) {
        this.constrainToSquare = constrain;
    }
    //#endregion

    //#region Helpers
    private Point2D canvasToImage(double canvasX, double canvasY) {
        double zoom = zoomGetter.get();
        double imageX = canvasX / zoom;
        double imageY = canvasY / zoom;

        int imageWidth = imageWidthGetter.get();
        int imageHeight = imageHeightGetter.get();

        imageX = Math.max(0, Math.min(imageX, imageWidth - 1));
        imageY = Math.max(0, Math.min(imageY, imageHeight - 1));

        return new Point2D(imageX, imageY);
    }

    private Rectangle calculateBounds() {
        int x = (int) Math.min(startX, currentX);
        int y = (int) Math.min(startY, currentY);
        int width = (int) Math.abs(currentX - startX);
        int height = (int) Math.abs(currentY - startY);

        if (constrainToSquare) {
            int size = Math.min(width, height);
            width = size;
            height = size;
        }

        return new Rectangle(x, y, width, height);
    }

    private void drawSelectionOverlay() {
        GraphicsContext gc = overlayCanvas.getGraphicsContext2D();
        double zoom = zoomGetter.get();

        // Clear previous drawing
        gc.clearRect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());

        if (!isDragging && !hasSelection) {
            return;
        }

        // Calculate bounds and convert to screen coords for drawing
        Rectangle bounds = calculateBounds();
        double screenX = bounds.getX() * zoom;
        double screenY = bounds.getY() * zoom;
        double screenW = bounds.getWidth() * zoom;
        double screenH = bounds.getHeight() * zoom;

        // Draw dashed rectangle (marching ants style)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.setLineDashes(4, 4);
        gc.strokeRect(screenX, screenY, screenW, screenH);

        // Optional: second white dashed line offset for visibility
        gc.setStroke(Color.WHITE);
        gc.setLineDashOffset(4);
        gc.strokeRect(screenX, screenY, screenW, screenH);
    }

    //#endregion

    @Override
    public void onDeactivate() {

    }
}
