package com.pixelcraft.util.logging;

/**
 * Interface for different log targets (console, file, UI, etc.).
 */
public interface ILogTarget {
    /**
     * Writes a log message to the target.
     * 
     * @param level The log level
     * @param className The name of the class logging the message
     * @param message The log message
     * @param throwable Optional throwable associated with the log entry
     */
    void write(eLogLevel level, String className, String message, Throwable throwable);

    /**
     * Checks if this target is enabled for the given log level.
     * 
     * @param level The log level to check
     * @return true if this target should log messages at this level
     */
    boolean isEnabled(eLogLevel level);

    /**
     * Flushes any buffered log data.
     */
    void flush();

    /**
     * Closes the log target and releases any resources.
     */
    void close();
}
