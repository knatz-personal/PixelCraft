package com.pixelcraft.commands;

import java.util.Optional;

import com.pixelcraft.manager.FileManager;
import com.pixelcraft.model.RasterImage;

public abstract class CommandBase implements ICommand {

    protected RasterImage snapshot;
    protected double previousPositionX;
    protected double previousPositionY;
    protected final FileManager fileManager;

   protected CommandBase() {
        fileManager = null;
   }

    protected CommandBase(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void undo() {
        restoreSnapshot();
    }

    //#region Helpers
    protected void restoreSnapshot() {
        reloadSnapshot(snapshot);
    }

    private void reloadSnapshot(RasterImage snapshot) {
        if (snapshot == null) {
            return;
        }
        RasterImage restored = snapshot.deepClone();
        restored.setPosition(previousPositionX, previousPositionY);
        fileManager.setCurrentImage(restored);
    }

    protected void takeSnapshot() {
        Optional<RasterImage> currentOpt = fileManager.getCurrentImage();
        if (!currentOpt.isPresent()) {
            return;
        }

        RasterImage current = currentOpt.get();
        previousPositionX = current.getPositionX();
        previousPositionY = current.getPositionY();
        snapshot = current.deepClone();
    }

    //#endregion
}
