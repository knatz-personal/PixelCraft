module name {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;

    requires transitive javafx.graphics;

    opens com.pixelcraft to javafx.fxml;
    
    // Ikonli
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.prefs;


    exports com.pixelcraft;
}
