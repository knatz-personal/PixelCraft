package com.pixelcraft.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pixelcraft.commands.CommandHistory;
import com.pixelcraft.commands.ICommand;
import com.pixelcraft.util.IconUtil;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;

/**
 * Manages presentation of a CommandHistory inside a JavaFX ListView&lt;Label&gt;.
 *
 * <p>The manager reads the undo and redo stacks from a provided CommandHistory and
 * populates the supplied ListView with Labels representing each ICommand. Redo
 * (future) entries are shown first, displayed in DARKGRAY and decorated with a
 * redo icon; undo (past) entries follow, displayed in BLACK and decorated with an
 * undo icon.</p>
 *
 * <p>Behavior and constraints:
 * <ul>
 *   <li>If the configured ListView is null, update() is a no-op.</li>
 *   <li>update() clears the ListView items and repopulates them from the current
 *       command history.</li>
 *   <li>Expects CommandHistory#getRedoStack() and getUndoStack() to return
 *       collections of ICommand and uses ICommand#getDescription() for label text.</li>
 *   <li>Relies on IconUtil to create graphical icons for redo/undo entries.</li>
 *   <li>Not thread-safe; update() must be invoked on the JavaFX Application Thread.</li>
 * </ul>
 * </p>
 *
 * <p>Typical usage:
 * <pre>
 * HistoryDisplayManager mgr = new HistoryDisplayManager(listView, commandHistory);
 * mgr.update();
 * </pre>
 * </p>
 */
public class HistoryDisplayManager {
    private final ListView<Label> lstHistory;
    private final CommandHistory commandHistory;
    
    /**
     * Creates a new HistoryDisplayManager.
     * 
     * @param lstHistory the ListView to display history in
     * @param commandHistory the command history to display
     */
    public HistoryDisplayManager(ListView<Label> lstHistory, CommandHistory commandHistory) {
        this.lstHistory = lstHistory;
        this.commandHistory = commandHistory;
    }
    
    /**
     * Updates the history display with current undo/redo stacks.
     * Redo items (future actions) are shown in gray with redo icons.
     * Undo items (past actions) are shown in black with undo icons.
     */
    public void update() {
        if (lstHistory == null) return;
        
        lstHistory.getItems().clear();
        
        // Add redo stack (future actions, grayed out)
        List<ICommand> redoList = new ArrayList<>(commandHistory.getRedoStack());
        Collections.reverse(redoList);
        for (ICommand cmd : redoList) {
            Label lbl = new Label(cmd.getDescription());
            lbl.setTextFill(Color.DARKGRAY);
            // Use redo icon for future actions
            lbl.setGraphic(IconUtil.createIconByName("redo"));
            lstHistory.getItems().add(lbl);
        }
        
        // Add undo stack (past actions)
        for (ICommand cmd : commandHistory.getUndoStack()) {
            Label lbl = new Label(cmd.getDescription());
            lbl.setTextFill(Color.BLACK);
            // Use undo icon for past actions
            lbl.setGraphic(IconUtil.createIconByName("undo"));
            lstHistory.getItems().add(lbl);
        }
    }
}