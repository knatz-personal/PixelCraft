package com.pixelcraft.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for CommandHistory class
 * 
 * @author Nathan Khupe
 */
class CommandHistoryTest {

    private CommandHistory history;
    private ICommand mockCommand;

    @BeforeEach
    void setUp() {
        history = new CommandHistory();
        mockCommand = mock(ICommand.class);
    }

    @Test
    @DisplayName("Execute command calls execute method")
    void testExecuteCommand() {
        history.execute(mockCommand);
        
        verify(mockCommand).execute();
    }

    @Test
    @DisplayName("Execute command adds to undo stack")
    void testExecuteAddsToUndoStack() {
        history.execute(mockCommand);
        
        assertEquals(1, history.getUndoStack().size());
        assertEquals(mockCommand, history.getUndoStack().peek());
    }

    @Test
    @DisplayName("Execute command clears redo stack")
    void testExecuteClearsRedoStack() {
        ICommand command1 = mock(ICommand.class);
        ICommand command2 = mock(ICommand.class);
        
        history.execute(command1);
        history.undo();
        assertEquals(1, history.getRedoStack().size());
        
        history.execute(command2);
        
        assertEquals(0, history.getRedoStack().size());
    }

    @Test
    @DisplayName("Undo with empty stack does nothing")
    void testUndoEmptyStack() {
        assertDoesNotThrow(() -> history.undo());
        assertEquals(0, history.getUndoStack().size());
    }

    @Test
    @DisplayName("Undo calls undo method on command")
    void testUndoCallsCommandUndo() {
        history.execute(mockCommand);
        
        history.undo();
        
        verify(mockCommand).undo();
    }

    @Test
    @DisplayName("Undo moves command to redo stack")
    void testUndoMovesToRedoStack() {
        history.execute(mockCommand);
        
        history.undo();
        
        assertEquals(0, history.getUndoStack().size());
        assertEquals(1, history.getRedoStack().size());
        assertEquals(mockCommand, history.getRedoStack().peek());
    }

    @Test
    @DisplayName("Redo with empty stack does nothing")
    void testRedoEmptyStack() {
        assertDoesNotThrow(() -> history.redo());
        assertEquals(0, history.getRedoStack().size());
    }

    @Test
    @DisplayName("Redo calls redo method on command")
    void testRedoCallsCommandExecute() {
        history.execute(mockCommand);
        history.undo();
        reset(mockCommand); // Clear previous execute call
        
        history.redo();
        
        verify(mockCommand).redo();
    }

    @Test
    @DisplayName("Redo moves command back to undo stack")
    void testRedoMovesBackToUndoStack() {
        history.execute(mockCommand);
        history.undo();
        
        history.redo();
        
        assertEquals(1, history.getUndoStack().size());
        assertEquals(0, history.getRedoStack().size());
        assertEquals(mockCommand, history.getUndoStack().peek());
    }

    @Test
    @DisplayName("Multiple commands in sequence")
    void testMultipleCommands() {
        ICommand cmd1 = mock(ICommand.class);
        ICommand cmd2 = mock(ICommand.class);
        ICommand cmd3 = mock(ICommand.class);
        
        history.execute(cmd1);
        history.execute(cmd2);
        history.execute(cmd3);
        
        assertEquals(3, history.getUndoStack().size());
    }

    @Test
    @DisplayName("Undo multiple commands")
    void testUndoMultipleCommands() {
        ICommand cmd1 = mock(ICommand.class);
        ICommand cmd2 = mock(ICommand.class);
        ICommand cmd3 = mock(ICommand.class);
        
        history.execute(cmd1);
        history.execute(cmd2);
        history.execute(cmd3);
        
        history.undo();
        verify(cmd3).undo();
        
        history.undo();
        verify(cmd2).undo();
        
        history.undo();
        verify(cmd1).undo();
        
        assertEquals(0, history.getUndoStack().size());
        assertEquals(3, history.getRedoStack().size());
    }

    @Test
    @DisplayName("Redo multiple commands")
    void testRedoMultipleCommands() {
        ICommand cmd1 = mock(ICommand.class);
        ICommand cmd2 = mock(ICommand.class);
        
        history.execute(cmd1);
        history.execute(cmd2);
        history.undo();
        history.undo();
        
        reset(cmd1, cmd2);
        
        history.redo();
        verify(cmd1).redo();
        
        history.redo();
        verify(cmd2).redo();
        
        assertEquals(2, history.getUndoStack().size());
        assertEquals(0, history.getRedoStack().size());
    }

    @Test
    @DisplayName("Execute after undo clears redo stack")
    void testExecuteAfterUndoClearsRedo() {
        ICommand cmd1 = mock(ICommand.class);
        ICommand cmd2 = mock(ICommand.class);
        ICommand cmd3 = mock(ICommand.class);
        
        history.execute(cmd1);
        history.execute(cmd2);
        history.undo();
        
        assertEquals(1, history.getRedoStack().size());
        
        history.execute(cmd3);
        
        assertEquals(0, history.getRedoStack().size());
        assertEquals(2, history.getUndoStack().size());
    }

    @Test
    @DisplayName("Get undo stack returns copy")
    void testGetUndoStackReturnsCopy() {
        history.execute(mockCommand);
        
        var stack1 = history.getUndoStack();
        var stack2 = history.getUndoStack();
        
        assertNotSame(stack1, stack2, "Should return different instances");
        assertEquals(stack1.size(), stack2.size());
    }

    @Test
    @DisplayName("Get redo stack returns copy")
    void testGetRedoStackReturnsCopy() {
        history.execute(mockCommand);
        history.undo();
        
        var stack1 = history.getRedoStack();
        var stack2 = history.getRedoStack();
        
        assertNotSame(stack1, stack2, "Should return different instances");
        assertEquals(stack1.size(), stack2.size());
    }

    @Test
    @DisplayName("Complex undo-redo sequence")
    void testComplexUndoRedoSequence() {
        ICommand cmd1 = mock(ICommand.class);
        ICommand cmd2 = mock(ICommand.class);
        ICommand cmd3 = mock(ICommand.class);
        
        // Execute three commands
        history.execute(cmd1);
        history.execute(cmd2);
        history.execute(cmd3);
        
        // Undo two
        history.undo();
        history.undo();
        
        // Redo one
        history.redo();
        
        // Execute new command (should clear redo stack)
        ICommand cmd4 = mock(ICommand.class);
        history.execute(cmd4);
        
        assertEquals(3, history.getUndoStack().size()); // cmd1, cmd2, cmd4
        assertEquals(0, history.getRedoStack().size());
    }
}
