package com.pixelcraft.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixelcraft.event.IToolChangeListener;
import com.pixelcraft.tools.ITool;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class ToolManager {

    private ITool activeTool;
    private final Map<String, ITool> tools;
    private final List<IToolChangeListener> listeners;

    public ToolManager() {
        this.tools = new HashMap<>();
        this.listeners = new ArrayList<>();
    }

    //#region Getters and Setters
    public void setActiveTool(String toolName) {
        ITool newTool = tools.get(toolName);
        if (newTool == null) {
            return;
        }

        if (activeTool != null) {
            activeTool.onDeactivate();
        }

        activeTool = newTool;

        // Notify listeners (e.g., update toolbar UI)
        for (IToolChangeListener listener : listeners) {
            listener.onToolChanged(activeTool);
        }
    }

    public Cursor getCurrentCursor() {
        return activeTool != null ? activeTool.getCursor() : Cursor.DEFAULT;
    }

    public ITool getActiveTool() {
        return activeTool;
    }
    //#endregion

    public void registerTool(String name, ITool tool) {
        tools.put(name, tool);
    }

    public String getActiveToolName()
    {
        if(activeTool == null)
        {
            return "No Tool Selected";
        }
        return activeTool.getName();
    }

    //#region Event Handlers
    public void handleMousePressed(MouseEvent event) {
        if (activeTool != null) {
            activeTool.onMousePressed(event);
        }
    }

    public void handleMouseDragged(MouseEvent event) {
        if (activeTool != null) {
            activeTool.onMouseDragged(event);
        }
    }

    public void handleMouseReleased(MouseEvent event) {
        if (activeTool != null) {
            activeTool.onMouseReleased(event);
        }
    }
    //#endregion
}
