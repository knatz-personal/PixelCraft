package com.pixelcraft.commands;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;

public class ZoomResetCommand implements ICommand {
    private final ZoomSetCommand zoomSetCommand;

    public ZoomResetCommand(ScrollPane scrollPane, Canvas canvas,
                            Supplier<Double> zoomLevelGetter, Consumer<Double> zoomLevelSetter,
                            Runnable updateStatusBar) {
        this.zoomSetCommand = new ZoomSetCommand(scrollPane, canvas,
                zoomLevelGetter, zoomLevelSetter,
                updateStatusBar, 1.0);
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
        return "Zoom Reset";
    }
}
