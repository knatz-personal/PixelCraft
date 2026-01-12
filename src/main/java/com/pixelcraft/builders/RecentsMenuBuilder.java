package com.pixelcraft.builders;

import java.nio.file.Paths;
import java.util.function.Consumer;

import com.pixelcraft.manager.RecentFilesManager;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public final class RecentsMenuBuilder {
    private RecentsMenuBuilder()
    {
        // Do not instantiate
    }

    public static void build(Menu mnuRecents, RecentFilesManager manager, Consumer<String> onOpen) {
        mnuRecents.getItems().clear();
        
        if (manager.isEmpty()) {
            MenuItem none = new MenuItem("(No recent files)");
            none.setDisable(true);
            mnuRecents.getItems().add(none);
        } else {
            for (String path : manager.getRecents()) {
                MenuItem mi = new MenuItem(Paths.get(path).getFileName().toString());
                mi.setOnAction(e -> onOpen.accept(path));
                mi.setText(path);
                mnuRecents.getItems().add(mi);
            }
            
            mnuRecents.getItems().add(new SeparatorMenuItem());
            MenuItem clear = new MenuItem("Clear Recent");
            clear.setOnAction(e -> {
                manager.clear();
                build(mnuRecents, manager, onOpen);
            });
            mnuRecents.getItems().add(clear);
        }
    }
}