package com.pixelcraft.util.logging;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.ObservableList;

/**
 * Factory and configuration manager for creating loggers with common configurations.
 */
public class LoggerFactory {
    private static final Map<String, Logger> loggerCache = new HashMap<>();
    private static final String APP_NAME = "PixelCraft";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private static LoggerConfig defaultConfig = new LoggerConfig()
        .enableConsoleLog(eLogLevel.INFO)
        .enableFileLog(getDailyLogFilePath(), eLogLevel.DEBUG);
    
    /**
     * Gets the path for daily log file in the system temp directory.
     * All logs for the same day will be written to the same file.
     * 
     * @return The path to the daily log file
     */
    public static String getDailyLogFilePath() {
        String tempDir = System.getenv("TEMP");
        if (tempDir == null || tempDir.isEmpty()) {
            tempDir = System.getProperty("java.io.tmpdir");
        }
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        Path logPath = Paths.get(tempDir, APP_NAME, String.format("%s.log", dateStr));
        return logPath.toString();
    }
    
    /**
     * Gets the log directory path in the system temp directory.
     * 
     * @return The path to the log directory
     */
    public static Path getLogDirectory() {
        String tempDir = System.getenv("TEMP");
        if (tempDir == null || tempDir.isEmpty()) {
            tempDir = System.getProperty("java.io.tmpdir");
        }
        return Paths.get(tempDir, APP_NAME);
    }

    /**
     * Sets the default configuration for all new loggers.
     * 
     * @param config The default configuration
     */
    public static void setDefaultConfig(LoggerConfig config) {
        defaultConfig = config;
    }

    /**
     * Gets a logger with the default configuration.
     * Loggers are cached by name.
     * 
     * @param clazz The class to create a logger for
     * @return A configured Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * Gets a logger with the default configuration.
     * Loggers are cached by name.
     * 
     * @param name The logger name
     * @return A configured Logger instance
     */
    public static synchronized Logger getLogger(String name) {
        return loggerCache.computeIfAbsent(name, n -> defaultConfig.createLogger(n));
    }

    /**
     * Gets a logger with a custom configuration.
     * This logger is not cached.
     * 
     * @param clazz The class to create a logger for
     * @param config The configuration to use
     * @return A configured Logger instance
     */
    public static Logger getLogger(Class<?> clazz, LoggerConfig config) {
        return config.createLogger(clazz.getName());
    }

    /**
     * Gets a logger with a custom configuration.
     * This logger is not cached.
     * 
     * @param name The logger name
     * @param config The configuration to use
     * @return A configured Logger instance
     */
    public static Logger getLogger(String name, LoggerConfig config) {
        return config.createLogger(name);
    }

    /**
     * Clears the logger cache.
     */
    public static synchronized void clearCache() {
        // Close all cached loggers
        for (Logger logger : loggerCache.values()) {
            logger.close();
        }
        loggerCache.clear();
    }

    /**
     * Creates a simple console logger.
     * 
     * @param clazz The class to create a logger for
     * @return A console-only logger
     */
    public static Logger createConsoleLogger(Class<?> clazz) {
        return Logger.getLogger(clazz)
            .addTarget(new ConsoleLogTarget(eLogLevel.INFO));
    }

    /**
     * Creates a logger that writes to both console and file.
     * 
     * @param clazz The class to create a logger for
     * @param logFilePath The path to the log file
     * @return A multi-target logger
     */
    public static Logger createConsoleAndFileLogger(Class<?> clazz, String logFilePath) {
        return Logger.getLogger(clazz)
            .addTarget(new ConsoleLogTarget(eLogLevel.INFO))
            .addTarget(new FileLogTarget(logFilePath, eLogLevel.DEBUG));
    }

    /**
     * Creates a comprehensive logger with console, file, and memory targets.
     * 
     * @param clazz The class to create a logger for
     * @param logFilePath The path to the log file
     * @return A fully configured logger
     */
    public static Logger createFullLogger(Class<?> clazz, String logFilePath, ObservableList<String> list) {
        return Logger.getLogger(clazz)
            .addTarget(new ConsoleLogTarget(eLogLevel.INFO))
            .addTarget(new FileLogTarget(logFilePath, eLogLevel.DEBUG))
            .addTarget(new ObservableListLogTarget(list, eLogLevel.DEBUG, 5000, true));
    }
}
