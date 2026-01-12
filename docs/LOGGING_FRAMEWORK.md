t# Custom Log Wrapper Framework

A flexible and extensible logging framework for Java applications that encapsulates different kinds of log targets.

## Overview

This custom logging framework provides a unified interface for logging to multiple destinations simultaneously. It supports various log targets including console, files, memory, JavaFX UI components, and java.util.logging integration.

## Features

- **Multiple Log Targets**: Console, File, JavaFX ObservableList, and JUL integration
- **Flexible Log Levels**: TRACE, DEBUG, INFO, WARN, ERROR, FATAL
- **Thread-Safe**: All components are designed for concurrent use
- **Extensible**: Easy to create custom log targets by implementing the `LogTarget` interface
- **Factory Pattern**: Simplified logger creation and configuration
- **Exception Handling**: Built-in support for logging exceptions with stack traces

## Components

### Core Classes

1. **Logger** - Main logging interface that manages multiple targets
2. **LogLevel** - Enum defining log severity levels
3. **LogTarget** - Interface for implementing custom log destinations

### Built-in Log Targets

1. **ConsoleLogTarget** - Logs to System.out/System.err with optional color support
2. **FileLogTarget** - Logs to files with automatic directory creation
3. **ObservableListLogTarget** - Logs to JavaFX ObservableList for UI display
4. **JulLogTarget** - Bridges to java.util.logging.Logger

### Factory

**LoggerFactory** - Provides convenient methods for creating and configuring loggers

## Usage Examples

### Basic Logging

```java
// Create a simple console logger
Logger logger = Logger.getLogger(MyClass.class)
    .addTarget(new ConsoleLogTarget(LogLevel.INFO));

logger.info("Application started");
logger.warn("This is a warning");
logger.error("An error occurred", exception);

logger.close();
```

### Multiple Targets

```java
// Log to console and file simultaneously
Logger logger = Logger.getLogger("MyApp")
    .addTarget(new ConsoleLogTarget(LogLevel.INFO))
    .addTarget(new FileLogTarget("logs/app.log", LogLevel.DEBUG));

logger.debug("Debug info (file only)");
logger.info("Important message (console and file)");

logger.close();
```

### Using LoggerFactory

```java
// Set default configuration for all loggers
LoggerFactory.setDefaultConfig(
    new LoggerFactory.LoggerConfig()
        .enableConsole(LogLevel.INFO)
        .enableFile("logs/app.log", LogLevel.DEBUG)
);

// Get a configured logger
Logger logger = LoggerFactory.getLogger(MyClass.class);
logger.info("This uses the default configuration");
```

### Custom Configuration

```java
// Create logger with custom configuration
Logger logger = LoggerFactory.getLogger(
    MyClass.class,
    new LoggerFactory.LoggerConfig()
        .enableConsoleWithColors(LogLevel.DEBUG)
        .enableFile("logs/custom.log", LogLevel.TRACE)
);

logger.debug("Colored console output");
```

### JavaFX Integration

```java
// Log to a ListView in JavaFX
ObservableList<String> logList = FXCollections.observableArrayList();
ListView<String> listView = new ListView<>(logList);

Logger logger = Logger.getLogger("UILogger")
    .addTarget(new ObservableListLogTarget(logList, LogLevel.INFO));

logger.info("This appears in the ListView");
```

### JUL Integration

```java
// Bridge to java.util.logging
Logger logger = Logger.getLogger(MyClass.class)
    .addTarget(new JulLogTarget(MyClass.class, LogLevel.DEBUG));

logger.info("Routed to java.util.logging");
```

## Log Levels

The framework supports six log levels in ascending order of severity:

1. **TRACE** - Detailed diagnostic information
2. **DEBUG** - Debug information for development
3. **INFO** - General informational messages
4. **WARN** - Warning messages for potentially harmful situations
5. **ERROR** - Error messages for serious problems
6. **FATAL** - Critical errors that may cause termination

## Creating Custom Log Targets

Implement the `LogTarget` interface to create custom log destinations:

```java
public class CustomLogTarget implements LogTarget {
    private LogLevel minLevel;
    
    public CustomLogTarget(LogLevel minLevel) {
        this.minLevel = minLevel;
    }
    
    @Override
    public void write(LogLevel level, String className, String message, Throwable throwable) {
        if (!isEnabled(level)) return;
        
        // Your custom logging logic here
        // e.g., send to database, external service, etc.
    }
    
    @Override
    public boolean isEnabled(LogLevel level) {
        return level.getPriority() >= minLevel.getPriority();
    }
    
    @Override
    public void flush() {
        // Flush any buffered data
    }
    
    @Override
    public void close() {
        // Clean up resources
    }
}
```

## Best Practices

1. **Always close loggers** when done to release resources
2. **Use appropriate log levels** - don't log everything at ERROR
3. **Configure minimum levels** per target to control verbosity
4. **Use LoggerFactory** for consistent configuration across the application
5. **Limit memory target sizes** to prevent memory issues
6. **Flush file targets** regularly for important logs
7. **Thread safety** - all targets are thread-safe, but consider performance

## Thread Safety

All components of this logging framework are designed to be thread-safe:

- `Logger` uses `CopyOnWriteArrayList` for managing targets
- `FileLogTarget` uses synchronized file operations
- `ObservableListLogTarget` properly uses JavaFX Platform.runLater()

## Performance Considerations

- **Console logging** is fast but can impact performance in tight loops
- **File logging** is buffered with optional auto-flush
- **Check log levels** before expensive string formatting:

```java
if (logger.isEnabled(LogLevel.DEBUG)) {
    logger.debug("Expensive operation: " + expensiveComputation());
}
```

## Integration with Existing Code

This framework can coexist with existing logging solutions:

- Use `JulLogTarget` to bridge to java.util.logging
- Use `ConsoleLogTarget` alongside System.out.println
- Gradually migrate existing logging to the new framework

## License

This custom logging framework is part of the PixelCraft project.
