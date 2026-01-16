package com.pixelcraft.tools;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public interface ITool {
    void onMousePressed(MouseEvent event);
    void onMouseDragged(MouseEvent event);
    void onMouseReleased(MouseEvent event);
    
    String getName();
    Cursor getCursor();
    void onDeactivate();
}
