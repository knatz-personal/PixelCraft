package com.pixelcraft.util.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Platform;
import javafx.collections.ObservableList;

/**
 * Log target that writes to a JavaFX ObservableList.
 * Useful for displaying real-time logs in UI components like ListView.
 */
public class ObservableListLogTarget implements ILogTarget {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private final eLogLevel minLevel;
    private final ObservableList<String> list;
    private final int maxEntries;
    private final boolean includeTimestamp;

    public ObservableListLogTarget(ObservableList<String> list) {
        this(list, eLogLevel.INFO, 5000, true);
    }

    public ObservableListLogTarget(ObservableList<String> list, eLogLevel minLevel) {
        this(list, minLevel, 500, true);
    }

    public ObservableListLogTarget(ObservableList<String> list, eLogLevel minLevel, 
                                   int maxEntries, boolean includeTimestamp) {
        this.list = list;
        this.minLevel = minLevel;
        this.maxEntries = maxEntries;
        this.includeTimestamp = includeTimestamp;
    }

    @Override
    public void write(eLogLevel level, String className, String message, Throwable throwable) {
        if (!isEnabled(level)) {
            return;
        }

        String formattedMessage = formatMessage(level, className, message);

        // Always use Platform.runLater to avoid JavaFX IndexOutOfBoundsException bug
        // when list modifications happen during selection events (JDK-8197846)
        Platform.runLater(() -> addToList(formattedMessage));

        // If there's a throwable, add its message as well
        if (throwable != null) {
            String errorMsg = "  → " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            Platform.runLater(() -> addToList(errorMsg));
        }
    }

    @Override
    public boolean isEnabled(eLogLevel level) {
        return level.getPriority() >= minLevel.getPriority();
    }

    @Override
    public void flush() {
        // No-op for ObservableList
    }

    @Override
    public void close() {
        // No-op for ObservableList
    }

    private void addToList(String message) {
        list.add(message);

        // Remove oldest entries if we exceed the maximum
        while (list.size() > maxEntries) {
            list.remove(0);
        }
    }

    private String formatMessage(eLogLevel level, String className, String message) {
        StringBuilder sb = new StringBuilder();

        if (includeTimestamp) {
            sb.append("[").append(LocalDateTime.now().format(TIME_FORMATTER)).append("] ");
        }

        String simpleClassName = getSimpleClassName(className);
        sb.append(String.format("%-5s", level.getLabel()))
          .append(" [").append(simpleClassName).append("] ")
          .append(message);

        return sb.toString();
    }

    private String getSimpleClassName(String className) {
        int lastDot = className.lastIndexOf('.');
        return lastDot >= 0 ? className.substring(lastDot + 1) : className;
    }
}
