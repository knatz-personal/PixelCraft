package com.pixelcraft.util.logging;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Log target that writes to the console (System.out or System.err).
 */
public class ConsoleLogTarget implements ILogTarget {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    private final eLogLevel minLevel;
    private final boolean useStdErr;
    private final boolean colorEnabled;

    public ConsoleLogTarget() {
        this(eLogLevel.INFO, true, false);
    }

    public ConsoleLogTarget(eLogLevel minLevel) {
        this(minLevel, true, false);
    }

    public ConsoleLogTarget(eLogLevel minLevel, boolean useStdErr, boolean colorEnabled) {
        this.minLevel = minLevel;
        this.useStdErr = useStdErr;
        this.colorEnabled = colorEnabled;
    }

    @Override
    public void write(eLogLevel level, String className, String message, Throwable throwable) {
        if (!isEnabled(level)) {
            return;
        }

        PrintStream stream = (useStdErr && level.getPriority() >= eLogLevel.ERROR.getPriority()) 
            ? System.err 
            : System.out;

        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        String levelStr = formatLevel(level);
        String simpleClassName = getSimpleClassName(className);

        String logMessage = String.format("[%s] %s [%s] %s", 
            timestamp, levelStr, simpleClassName, message);

        stream.println(logMessage);

        if (throwable != null) {
            throwable.printStackTrace(stream);
        }
    }

    @Override
    public boolean isEnabled(eLogLevel level) {
        return level.getPriority() >= minLevel.getPriority();
    }

    @Override
    public void flush() {
        System.out.flush();
        System.err.flush();
    }

    @Override
    public void close() {
        flush();
    }

    private String formatLevel(eLogLevel level) {
        if (!colorEnabled) {
            return String.format("%-5s", level.getLabel());
        }

        // ANSI color codes for different log levels
        String color;
        switch (level) {
            case TRACE:
                color = "\u001B[37m"; // White
                break;
            case DEBUG:
                color = "\u001B[36m"; // Cyan
                break;
            case INFO:
                color = "\u001B[32m"; // Green
                break;
            case WARN:
                color = "\u001B[33m"; // Yellow
                break;
            case ERROR:
                color = "\u001B[31m"; // Red
                break;
            case FATAL:
                color = "\u001B[35m"; // Magenta
                break;
            default:
                color = "";
        }

        return color + String.format("%-5s", level.getLabel()) + "\u001B[0m"; // Reset
    }

    private String getSimpleClassName(String className) {
        int lastDot = className.lastIndexOf('.');
        return lastDot >= 0 ? className.substring(lastDot + 1) : className;
    }
}
