# PixelCraft - AI Coding Agent Instructions

## Project Overview
PixelCraft is a JavaFX 21 image editor using the modular Java Platform Module System (JPMS). The UI is defined in FXML with controller binding.

## Architecture

### Core Components
- **Entry Point**: `PixelCraft.java` → loads `main.fxml` → binds to `MainController`
- **Manager Pattern**: Domain responsibilities split into single-purpose managers in `com.pixelcraft.manager/`:
  - `FileManager` - Image I/O, modification tracking, file state
  - `CanvasManager` - Rendering, checkerboard background, image drawing
  - `ViewportManager` - Zoom/pan state, coordinate transforms
  - `RecentFilesManager` - Recent files persistence via `java.util.prefs`
  - `StatusBarManager` - Status bar UI updates
  - `HistoryDisplayManager` - Undo/redo list visualization

### Command Pattern (Undo/Redo)
All undoable operations implement `ICommand` interface in `com.pixelcraft.commands/`:
```java
public interface ICommand {
    void execute();
    void undo();
    String getDescription();
}
```
- Commands capture state in constructor, restore in `undo()`
- Execute via `CommandHistory.execute(cmd)` - never call `cmd.execute()` directly
- See `NewImageCommand`, `ZoomInCommand` for reference implementations

### Event System
Listener interfaces in `com.pixelcraft.event/` for decoupled communication:
- `IImageChangeListener` - Image/file state changes
- `IViewportChangeListener` - Zoom/pan updates

Managers notify listeners; `MainController` subscribes and coordinates UI updates.

### Image Model
`RasterImage` wraps `BufferedImage` with:
- Dual-buffer design: `originalImage` (immutable source) + `image` (working copy)
- Non-destructive view state: `renderScale`, `positionX`, `positionY`
- `toFXImage()` converts to JavaFX `Image` with caching

## Build & Run
```bash
cd pixelcraft
mvn clean install          # Build and run tests
mvn javafx:run             # Launch application
mvn test                   # Run unit tests (headless via Monocle)
```

## Testing Conventions
- **Framework**: JUnit 5 + TestFX + Mockito
- **Headless**: Tests run via Monocle (configured in `pom.xml` surefire plugin)
- **Structure**: Mirror source in `src/test/java/com/pixelcraft/{package}/`
- **Naming**: `{ClassName}Test.java` for unit tests, `*IntegrationTest.java` or `*E2ETest.java` for integration

Example test pattern (mocking commands):
```java
@Test
void testExecuteCommand() {
    ICommand mockCommand = mock(ICommand.class);
    history.execute(mockCommand);
    verify(mockCommand).execute();
}
```

## Key Conventions

### Adding New Features
1. **Undoable actions**: Create `ICommand` implementation, execute via `CommandHistory`
2. **UI bindings**: Add `@FXML` fields in controller, wire in `main.fxml`
3. **Manager logic**: Keep controllers thin; delegate to appropriate manager
4. **Events**: Use listener interfaces, not direct method calls between components

### Module System (JPMS)
`module-info.java` controls visibility:
- Export packages that other modules need
- Open packages to `javafx.fxml` for reflection
- Add `requires` for external dependencies

### Constants
Use `Globals` class for shared constants (zoom bounds, defaults).

## File Locations
| Purpose | Path |
|---------|------|
| Main FXML layout | `src/main/resources/com/pixelcraft/main.fxml` |
| Application config | `src/main/resources/config/application.properties` |
| Test images | `src/test/resources/sample_images/` |
| Development guide | `docs/Requirements.md` |

## Common Patterns

### Creating a New Command
```java
public class MyCommand implements ICommand {
    private final SomeManager manager;
    private PreviousState savedState;  // For undo
    
    @Override
    public void execute() {
        savedState = manager.getCurrentState();
        manager.doOperation();
    }
    
    @Override
    public void undo() {
        manager.restoreState(savedState);
    }
}
```

### Adding Menu Actions
1. Add `MenuItem` in `main.fxml`
2. Add `@FXML` handler method in `MainController`
3. Create/use appropriate `ICommand`, execute via `commandHistory.execute(cmd)`
