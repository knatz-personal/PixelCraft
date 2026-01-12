package com.pixelcraft;

import java.io.IOException;
import java.net.URL;

import com.pixelcraft.util.GlobalExceptionHandler;
import com.pixelcraft.util.Globals;
import com.pixelcraft.util.logging.LoggerFactory;
import com.pixelcraft.util.logging.eLogLevel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PixelCraft extends Application
{
    private static final com.pixelcraft.util.logging.Logger LOG;
    
    static {
        // Configure default logging for the entire application
        LoggerFactory.setDefaultConfig(
            new LoggerFactory.LoggerConfig()
                .enableConsoleLogWithColors(eLogLevel.INFO)
                .enableFileLog("logs/pixelcraft.log", eLogLevel.DEBUG)
        );
        
        LOG = LoggerFactory.getLogger(PixelCraft.class);
    }

    @Override
    public void start(Stage primaryStage) 
    {
        loadLayout(primaryStage);
    }

    private void loadLayout(Stage primaryStage) 
    {
        try 
        {
            Parent root = loadFXML("main");
            Scene scene = new Scene(root, Globals.DEFAULT_WIDTH, Globals.DEFAULT_HEIGHT);
            primaryStage.setTitle("PixelCraft - Image Editor");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setFullScreen(false);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (IOException e) {
            LOG.error("Failed to load layout", e);
            GlobalExceptionHandler.handleException("Layout loading", e);
        }
    }

    private static Parent loadFXML(String fxmlName) throws IOException 
    {
        URL fxml = PixelCraft.class.getResource(fxmlName + ".fxml");
        if(fxml == null)
        {
            throw new IOException("FXML resource not found: " + fxmlName);
        }
        return new FXMLLoader(fxml).load();
    }

    public static void main( String[] args )
    {
        // Initialize global exception handler
        GlobalExceptionHandler.initialize(true);
        
        try
        {
            LOG.info("Launching PixelCraft!");
            launch(args);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            GlobalExceptionHandler.handleException("Application launch", ex);
        }
    }
}