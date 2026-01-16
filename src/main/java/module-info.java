module com.pixelcraft {
    requires transitive javafx.controls;
    requires transitive javafx.graphics; 
    requires transitive java.desktop; 
    requires transitive java.prefs;

    requires javafx.fxml;
    requires javafx.swing;
    requires java.logging;
    requires javafx.base;

    opens com.pixelcraft.ui to javafx.fxml;
    
    exports com.pixelcraft;
    exports com.pixelcraft.commands;
    exports com.pixelcraft.manager;
    exports com.pixelcraft.model;
    exports com.pixelcraft.event;
    exports com.pixelcraft.tools;
    exports com.pixelcraft.util;
    exports com.pixelcraft.util.logging;
    exports com.pixelcraft.ui;
}