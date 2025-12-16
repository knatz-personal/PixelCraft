package com.pixelcraft;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application
{
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage primaryStage) {
        loadLayout(primaryStage);
    }

    private void loadLayout(Stage primaryStage) {
        try 
        {
            Parent root = loadFXML("main");
            Scene scene = new Scene(root, 800, 640);
            primaryStage.setTitle("PixelCraft - Image Editor");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) 
        {
            LOGGER.severe("Failed to load layout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Parent loadFXML(String fxmlName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlName + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main( String[] args )
    {
        LOGGER.info("Launching PixelCraft!");
        launch(args);
    }
}