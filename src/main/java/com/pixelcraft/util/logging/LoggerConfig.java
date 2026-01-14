package com.pixelcraft.util.logging;

import javafx.collections.ObservableList;

/**
 * Configuration class for setting up loggers.
 */
public class LoggerConfig {
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