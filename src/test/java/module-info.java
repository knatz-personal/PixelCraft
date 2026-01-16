open module com.pixelcraft {
    requires transitive javafx.controls;
    requires transitive javafx.graphics; 
    requires transitive java.desktop; 

    requires javafx.fxml;
    requires javafx.swing;
    requires java.logging;
    requires java.prefs;
    requires javafx.base;
    
    // Test dependencies
    requires org.junit.jupiter.api;
    requires org.testfx;
    requires org.testfx.junit5;
    requires org.mockito;
    
    // 'open module' automatically opens all packages to all modules
    // No need for explicit 'opens' statements
    
    exports com.pixelcraft;
    exports com.pixelcraft.commands;
    exports com.pixelcraft.manager;
    exports com.pixelcraft.model;
    exports com.pixelcraft.event;
    exports com.pixelcraft.util;
    exports com.pixelcraft.util.logging;
    exports com.pixelcraft.ui;
}
