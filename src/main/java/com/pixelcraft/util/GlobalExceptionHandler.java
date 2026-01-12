package com.pixelcraft.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.pixelcraft.util.logging.Logger;
import com.pixelcraft.util.logging.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Global exception handler for catching and managing uncaught exceptions
 * throughout the application lifecycle.
 */
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static GlobalExceptionHandler instance;
    private final boolean showDialogs;
    
    /**
     * Creates a new global exception handler.
     * @param showDialogs Whether to show error dialogs to the user
     */
    private GlobalExceptionHandler(boolean showDialogs)
    {
        this.showDialogs = showDialogs;
    }
    
    /**
     * Initializes the global exception handler and sets it as the default
     * uncaught exception handler for all threads.
     * @param showDialogs Whether to show error dialogs to the user
     */
    public static void initialize(boolean showDialogs)
    {
        if (instance != null)
        {
            LOG.warn("Global exception handler already initialized");
            return;
        }
        
        instance = new GlobalExceptionHandler(showDialogs);
        
        // Set handler for all threads
        Thread.setDefaultUncaughtExceptionHandler(instance);
        
        // Set handler for JavaFX Application Thread exceptions
        setupJavaFXExceptionHandler();
        
        LOG.info(String.format("Global exception handler initialized (showDialogs: %b)", showDialogs));
    }
    
    /**
     * Sets up exception handling for the JavaFX Application Thread.
     */
    private static void setupJavaFXExceptionHandler()
    {
        // Catch exceptions in Platform.runLater calls
        if (Platform.isFxApplicationThread())
        {
            Thread.currentThread().setUncaughtExceptionHandler(instance);
        }
    }
    
    @Override
    public void uncaughtException(Thread thread, Throwable throwable)
    {
        LOG.error(String.format("Uncaught exception in thread '%s': %s", 
                  thread.getName(), throwable.getMessage()), throwable);
        
        // Handle on JavaFX thread if needed for UI dialogs
        if (showDialogs)
        {
            if (Platform.isFxApplicationThread())
            {
                showErrorDialog(throwable);
            }
            else
            {
                Platform.runLater(() -> showErrorDialog(throwable));
            }
        }
    }
    
    /**
     * Displays an error dialog with exception details.
     * @param throwable The exception to display
     */
    private void showErrorDialog(Throwable throwable)
    {
        try
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Unexpected Error");
            alert.setHeaderText("An unexpected error has occurred");
            alert.setContentText(throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
            
            // Create expandable Exception details
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String exceptionText = sw.toString();
            
            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            
            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 0);
            
            alert.getDialogPane().setExpandableContent(expContent);
            alert.showAndWait();
        }
        catch (Exception e)
        {
            // If we can't show the dialog, at least log it
            LOG.error("Failed to show error dialog", e);
        }
    }
    
    /**
     * Handles an exception manually, useful for catching exceptions
     * in try-catch blocks that should be handled globally.
     * @param throwable The exception to handle
     */
    public static void handleException(Throwable throwable)
    {
        if (instance != null)
        {
            instance.uncaughtException(Thread.currentThread(), throwable);
        }
        else
        {
            LOG.error(String.format("Exception occurred but global handler not initialized: %s", 
                     throwable.getMessage()), throwable);
        }
    }
    
    /**
     * Handles an exception with a custom context message.
     * @param context Description of where/why the exception occurred
     * @param throwable The exception to handle
     */
    public static void handleException(String context, Throwable throwable)
    {
        LOG.error(String.format("Exception in context '%s': %s", context, throwable.getMessage()), throwable);
        handleException(throwable);
    }
}
