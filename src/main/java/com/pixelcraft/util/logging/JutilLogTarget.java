package com.pixelcraft.util.logging;

import java.util.logging.Level;

/**
 * Log target that bridges to java.util.logging.Logger.
 * Useful for integrating with existing JUL-based logging infrastructure.
 */
public class JutilLogTarget implements ILogTarget {
    @SuppressWarnings("NonConstantLogger")
    private static java.util.logging.Logger jutilLogger = null;
    private final eLogLevel minLevel;

    public JutilLogTarget(String loggerName) {
        this(loggerName, eLogLevel.DEBUG);
    }

    public JutilLogTarget(String loggerName, eLogLevel minLevel) {
        jutilLogger = java.util.logging.Logger.getLogger(loggerName);
        this.minLevel = minLevel;
    }

    public JutilLogTarget(Class<?> clazz) {
        this(clazz.getName(), eLogLevel.DEBUG);
    }

    public JutilLogTarget(Class<?> clazz, eLogLevel minLevel) {
        this(clazz.getName(), minLevel);
    }

    @Override
    public void write(eLogLevel level, String className, String message, Throwable throwable) {
        if (!isEnabled(level)) {
            return;
        }

        Level julLevel = convertLevel(level);
        if (throwable != null) {
            jutilLogger.log(julLevel, message, throwable);
        } else {
            jutilLogger.log(julLevel, message);
        }
    }

    @Override
    public boolean isEnabled(eLogLevel level) {
        return level.getPriority() >= minLevel.getPriority() 
            && jutilLogger.isLoggable(convertLevel(level));
    }

    @Override
    public void flush() {
        // JUL handles flushing internally
    }

    @Override
    public void close() {
        // JUL manages logger lifecycle
    }

    /**
     * Converts our LogLevel to java.util.logging.Level.
     * 
     * @param level The LogLevel to convert
     * @return The corresponding JUL Level
     */
    private Level convertLevel(eLogLevel level) {
        return switch (level) {
            case TRACE -> Level.FINEST;
            case DEBUG -> Level.FINE;
            case INFO -> Level.INFO;
            case WARN -> Level.WARNING;
            case ERROR -> Level.SEVERE;
            case FATAL -> Level.SEVERE;
            default -> Level.INFO;
        };
    }
}
