package com.pixelcraft.commands;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.pixelcraft.util.Globals;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;


public class ZoomSetCommand implements ICommand {
    // Maximum texture size for GPU rendering (safe limit for most GPUs)
    private static final double MAX_TEXTURE_SIZE = 8192.0;

    private final ScrollPane scrollPane;
    private final Canvas canvas;
    private final Supplier<Double> zoomLevelGetter;
    private final Consumer<Double> zoomLevelSetter;
    private final Runnable updateStatusBar;
    private final double targetZoom;
    private double previousZoom;

    public ZoomSetCommand(ScrollPane scrollPane, Canvas canvas,                          
                          Supplier<Double> zoomLevelGetter, Consumer<Double> zoomLevelSetter,
                          Runnable updateStatusBar, double targetZoom) {
        this.scrollPane = scrollPane;
        this.canvas = canvas;
        this.zoomLevelGetter = zoomLevelGetter;
        this.zoomLevelSetter = zoomLevelSetter;
        this.updateStatusBar = updateStatusBar;
        this.targetZoom = targetZoom;
    }

    @Override
    public void execute() {
        previousZoom = zoomLevelGetter.get();
        double newZoom = Math.clamp(targetZoom, Globals.MIN_ZOOM, Globals.MAX_ZOOM);

        // Clamp zoom to prevent GPU texture overflow (RTTexture NPE).
        // The effective scaled canvas size must not exceed MAX_TEXTURE_SIZE.
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double maxDimension = Math.max(canvasWidth, canvasHeight);
        if (maxDimension > 0) {
            double maxAllowedZoom = MAX_TEXTURE_SIZE / maxDimension;
            newZoom = Math.min(newZoom, maxAllowedZoom);
        }

        if (previousZoom == newZoom) {
            return;
        }

        // Don't manipulate canvas scale here - let MainController.applyZoom() handle canvas sizing
        // This ensures consistent behavior with the ViewportManager pattern

        zoomLevelSetter.accept(newZoom);

        if (updateStatusBar != null) {
            updateStatusBar.run();
        }
    }

    @Override
    public void undo() {
        new ZoomSetCommand(scrollPane, canvas, zoomLevelGetter, zoomLevelSetter, updateStatusBar, previousZoom).execute();
    }

    @Override
    public String getDescription() {
        String perc = ""+Math.round(targetZoom * 100);
        return String.format("Set Zoom to %s %%", perc);
    }
}
