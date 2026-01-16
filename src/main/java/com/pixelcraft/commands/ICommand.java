package com.pixelcraft.commands;

public interface ICommand {
    void execute();
    void undo();
    default void redo() { execute(); } 
    String getDescription();
}