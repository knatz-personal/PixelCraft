package com.pixelcraft.commands;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandHistory {

    private final Deque<ICommand> undoStack = new ArrayDeque<>();
    private final Deque<ICommand> redoStack = new ArrayDeque<>(); 

    /**
     * Executes the given command, adds it to the undo stack, and clears the redo stack.
     * <p>
     * This method is typically used to perform an action that can be undone and redone.
     * After executing the command, it is pushed onto the undo stack to allow for undo operations.
     * The redo stack is cleared to maintain the correct state of the command history.
     *
     * @param command the {@link ICommand} to execute
     */
    public void execute(ICommand command)
    {
        command.execute();
        // add the command to the top of the stack
        undoStack.push(command);
        // clear the redo stack
        redoStack.clear();
    }
    
    /**
     * Undoes the last executed command by popping it from the undo stack,
     * invoking its undo operation, and pushing it onto the redo stack.
     * If there are no commands to undo, this method does nothing.
     */
    public void undo()
    {
        if(undoStack.isEmpty()) {
            return;
        }
        // Pop the last command from the undo stack
        ICommand command = undoStack.pop();
        // Reverse the command
        command.undo();
        // Add the command to the top of the redo stack
        redoStack.push(command);
    }
    
    /**
     * Re-executes the most recently undone command.
     * <p>
     * If the redo stack is not empty, this method pops the last command from the redo stack,
     * executes it, and then pushes it onto the undo stack. If the redo stack is empty,
     * the method returns without performing any action.
     */
    public void redo()
    {        
        if(redoStack.isEmpty()) {
            return;
        }
        // Pop the last command from the top of the redo stack
        ICommand command = redoStack.pop();
        // Execute the command
        command.execute();
        // Add the command to the top of the undo stack
        undoStack.push(command);
    }

    /**
     * Returns a read-only view of the undo stack.
     * 
     * @return the undo stack
     */
    public Deque<ICommand> getUndoStack() {
        return new ArrayDeque<>(undoStack);
    }

    /**
     * Returns a read-only view of the redo stack.
     * 
     * @return the redo stack
     */
    public Deque<ICommand> getRedoStack() {
        return new ArrayDeque<>(redoStack);
    }

}
