package com.pixelcraft.util.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Log target that writes to a file.
 */
public class FileLogTarget implements ILogTarget {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    private final eLogLevel minLevel;
    private final Path logFile;
    private BufferedWriter writer;
    private final boolean autoFlush;

    public FileLogTarget(String logFilePath) {
        this(logFilePath, eLogLevel.DEBUG, true);
    }

    public FileLogTarget(String logFilePath, eLogLevel minLevel) {
        this(logFilePath, minLevel, true);
    }

    public FileLogTarget(String logFilePath, eLogLevel minLevel, boolean autoFlush) {
        this.minLevel = minLevel;
        this.logFile = Paths.get(logFilePath);
        this.autoFlush = autoFlush;
        initializeWriter();
    }

    private void initializeWriter() {
        try {
            // Create parent directories if they don't exist
            if (logFile.getParent() != null) {
                Files.createDirectories(logFile.getParent());
            }

            // Open writer in append mode
            this.writer = new BufferedWriter(new FileWriter(logFile.toFile(), true));
        } catch (IOException e) {
            System.err.println("Failed to initialize file log target: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void write(eLogLevel level, String className, String message, Throwable throwable) {
        if (!isEnabled(level) || writer == null) {
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
            String logMessage = String.format("[%s] %-5s [%s] %s", 
                timestamp, level.getLabel(), className, message);

            writer.write(logMessage);
            writer.newLine();

            if (throwable != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                writer.write(sw.toString());
            }

            if (autoFlush) {
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    @Override
    public boolean isEnabled(eLogLevel level) {
        return level.getPriority() >= minLevel.getPriority();
    }

    @Override
    public void flush() {
        if (writer != null) {
            try {
                writer.flush();
            } catch (IOException e) {
                System.err.println("Failed to flush log file: " + e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("Failed to close log file: " + e.getMessage());
            }
        }
    }

    public Path getLogFile() {
        return logFile;
    }
}
