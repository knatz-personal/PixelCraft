package com.pixelcraft.commands;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.pixelcraft.util.Globals;

import javafx.scene.canvas.Canvas;

public class ZoomOutCommand extends CommandBase {
    private final ZoomSetCommand zoomSetCommand;
    private final double targetZoom;

    public ZoomOutCommand(Canvas canvas, Supplier<Double> zoomLevelGetter, Consumer<Double> zoomLevelSetter, Runnable updateStatusBar) {
        targetZoom = zoomLevelGetter.get() / Globals.ZOOM_STEP;
        this.zoomSetCommand = new ZoomSetCommand(canvas, zoomLevelGetter, zoomLevelSetter, updateStatusBar, targetZoom);
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
        String perc = ""+Math.round(targetZoom * 100);
        return String.format("Zoom Out %s", perc);
    }
}
