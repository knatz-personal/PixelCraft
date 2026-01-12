package com.pixelcraft.util.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Custom logger that wraps multiple log targets.
 * Provides a unified interface for logging to various destinations.
 */
public class Logger {
    private final String className;
    private final List<ILogTarget> targets;

    private Logger(String className) {
        this.className = className;
        this.targets = new CopyOnWriteArrayList<>();
    }

    /**
     * Creates a logger for the specified class.
     * 
     * @param clazz The class to create a logger for
     * @return A new Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz.getName());
    }

    /**
     * Creates a logger with the specified name.
     * 
     * @param name The logger name
     * @return A new Logger instance
     */
    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    /**
     * Adds a log target to this logger.
     * 
     * @param target The log target to add
     * @return This logger for method chaining
     */
    public Logger addTarget(ILogTarget target) {
        if (target != null && !targets.contains(target)) {
            targets.add(target);
        }
        return this;
    }

    /**
     * Removes a log target from this logger.
     * 
     * @param target The log target to remove
     * @return This logger for method chaining
     */
    public Logger removeTarget(ILogTarget target) {
        targets.remove(target);
        return this;
    }

    /**
     * Clears all log targets from this logger.
     */
    public void clearTargets() {
        targets.clear();
    }

    /**
     * Gets all registered log targets.
     * 
     * @return List of log targets
     */
    public List<ILogTarget> getTargets() {
        return new ArrayList<>(targets);
    }

    // Trace level logging
    public void trace(String message) {
        log(eLogLevel.TRACE, message, null);
    }

    public void trace(String message, Throwable throwable) {
        log(eLogLevel.TRACE, message, throwable);
    }

    // Debug level logging
    public void debug(String message) {
        log(eLogLevel.DEBUG, message, null);
    }

    public void debug(String message, Throwable throwable) {
        log(eLogLevel.DEBUG, message, throwable);
    }

    // Info level logging
    public void info(String message) {
        log(eLogLevel.INFO, message, null);
    }

    public void info(String message, Throwable throwable) {
        log(eLogLevel.INFO, message, throwable);
    }

    // Warn level logging
    public void warn(String message) {
        log(eLogLevel.WARN, message, null);
    }

    public void warn(String message, Throwable throwable) {
        log(eLogLevel.WARN, message, throwable);
    }

    // Error level logging
    public void error(String message) {
        log(eLogLevel.ERROR, message, null);
    }

    public void error(String message, Throwable throwable) {
        log(eLogLevel.ERROR, message, throwable);
    }

    // Fatal level logging
    public void fatal(String message) {
        log(eLogLevel.FATAL, message, null);
    }

    public void fatal(String message, Throwable throwable) {
        log(eLogLevel.FATAL, message, throwable);
    }

    /**
     * Logs a message at the specified level.
     * 
     * @param level The log level
     * @param message The log message
     * @param throwable Optional throwable
     */
    public void log(eLogLevel level, String message, Throwable throwable) {
        for (ILogTarget target : targets) {
            try {
                target.write(level, className, message, throwable);
            } catch (Exception e) {
                // Prevent logging errors from breaking the application
                System.err.println("Error writing to log target: " + e.getMessage());
            }
        }
    }

    /**
     * Checks if any target is enabled for the given level.
     * 
     * @param level The log level to check
     * @return true if at least one target is enabled for this level
     */
    public boolean isEnabled(eLogLevel level) {
        for (ILogTarget target : targets) {
            if (target.isEnabled(level)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Flushes all log targets.
     */
    public void flush() {
        for (ILogTarget target : targets) {
            try {
                target.flush();
            } catch (Exception e) {
                System.err.println("Error flushing log target: " + e.getMessage());
            }
        }
    }

    /**
     * Closes all log targets and releases resources.
     */
    public void close() {
        for (ILogTarget target : targets) {
            try {
                target.close();
            } catch (Exception e) {
                System.err.println("Error closing log target: " + e.getMessage());
            }
        }
        clearTargets();
    }
}
