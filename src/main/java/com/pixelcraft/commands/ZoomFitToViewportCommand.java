package com.pixelcraft.commands;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;

public class ZoomFitToViewportCommand implements ICommand {

    private final ZoomSetCommand zoomSetCommand;
    private final double targetZoom;

    public ZoomFitToViewportCommand(ScrollPane scrollPane, Canvas canvas,
            Supplier<Double> zoomLevelGetter, Consumer<Double> zoomLevelSetter,
            Runnable updateStatusBar) {

        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        
        // Get current zoom to calculate actual image dimensions
        double currentZoom = zoomLevelGetter.get();
        double imageWidth = canvas.getWidth() / currentZoom;
        double imageHeight = canvas.getHeight() / currentZoom;
        
        // Calculate zoom to fit image (not current scaled canvas) to viewport
        double xZoom = viewportWidth / imageWidth;
        double yZoom = viewportHeight / imageHeight;

        targetZoom = Math.min(xZoom, yZoom) * 0.98; // Add 2% padding

        this.zoomSetCommand = new ZoomSetCommand(scrollPane, canvas, zoomLevelGetter, zoomLevelSetter, updateStatusBar, targetZoom);
    }

    @Override
    public void execute() {
        zoomSetCommand.execute();
    }

    @Override
    public void undo() {
        zoomSetCommand.undo();
    }

    @Override
    public String getDescription() {
        String perc = "" + Math.round(targetZoom * 100);
        return String.format("Fit To Viewport %s", perc);
    }
}
