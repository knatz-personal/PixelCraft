package com.pixelcraft.commands;

public interface ICommand {
    void execute();
    void undo();
    String getDescription();
}
