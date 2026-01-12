package com.pixelcraft.util.logging;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ObservableList;

/**
 * Factory and configuration manager for creating loggers with common configurations.
 */
public class LoggerFactory {
    private static final Map<String, Logger> loggerCache = new HashMap<>();
    private static LoggerConfig defaultConfig = new LoggerConfig()
        .enableConsoleLog(eLogLevel.INFO);

    /**
     * Configuration class for setting up loggers.
     */
    public static class LoggerConfig {
        private boolean consoleEnabled = false;
        private eLogLevel consoleLevel = eLogLevel.INFO;
        private boolean consoleColorEnabled = false;

        private boolean fileEnabled = false;
        private String filePattern = "logs/app.log";
        private eLogLevel fileLevel = eLogLevel.DEBUG;

        private boolean jutilEnabled = false;
        private eLogLevel jutilLevel = eLogLevel.DEBUG;

        private boolean observableEnabled = false;
        private eLogLevel observableLevel = eLogLevel.INFO;
        private int observableMaxEntries = 5000;

        public LoggerConfig enableConsoleLog(eLogLevel level) {
            this.consoleEnabled = true;
            this.consoleLevel = level;
            return this;
        }

        public LoggerConfig enableConsoleLogWithColors(eLogLevel level) {
            this.consoleEnabled = true;
            this.consoleLevel = level;
            this.consoleColorEnabled = true;
            return this;
        }

        public LoggerConfig enableFileLog(String filePath, eLogLevel level) {
            this.fileEnabled = true;
            this.filePattern = filePath;
            this.fileLevel = level;
            return this;
        }

        public LoggerConfig enableActivityLog(eLogLevel level, int maxEntries) {
            this.observableEnabled = true;
            this.observableLevel = level;
            this.observableMaxEntries = maxEntries;
            return this;
        }

        public LoggerConfig enableJutilLog(eLogLevel level) {
            this.jutilEnabled = true;
            this.jutilLevel = level;
            return this;
        }

        Logger createLogger(String name) {
            return createLogger(name, null);
        }

        Logger createLogger(String name, ObservableList<String> list) {
            Logger logger = Logger.getLogger(name);

            if (consoleEnabled) {
                logger.addTarget(new ConsoleLogTarget(consoleLevel, true, consoleColorEnabled));
            }

            if (fileEnabled) {
                logger.addTarget(new FileLogTarget(filePattern, fileLevel));
            }

            if (observableEnabled && list != null) {
                logger.addTarget(new ObservableListLogTarget(list, observableLevel, observableMaxEntries, true));
            }

            if (jutilEnabled) {
                logger.addTarget(new JutilLogTarget(name, jutilLevel));
            }

            return logger;
        }
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
