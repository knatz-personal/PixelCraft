# PixelCraft - Step-by-Step Development Guide

Welcome to PixelCraft! This guide provides a structured path to building a professional image editor while mastering Java, JavaFX, and software design patterns. Follow each milestone sequentially to build your skills progressively.

## 📖 Table of Contents

### Getting Started
- [Project Overview](#-project-overview)
- [Development Roadmap](#-development-roadmap)
- [Table of Contents](#-table-of-contents)

### Step-by-Step Milestones
1.  [Milestone 1: Project Setup & Foundation](#milestone-1-project-setup--foundation)
2.  [Milestone 2: Canvas and Display System](#milestone-2-canvas-and-display-system)
3.  [Milestone 3: Image Loading and Saving](#milestone-3-image-loading-and-saving)
4.  [Milestone 4: Drawing Tools - Pencil and Eraser](#milestone-4-drawing-tools---pencil-and-eraser)
5.  [Milestone 5: Zoom and Pan Navigation](#milestone-5-zoom-and-pan-navigation)
6.  [Milestone 6: Basic Image Adjustments](#milestone-6-basic-image-adjustments)
7.  [Milestone 7: Additional Tools - Fill, Line, and Shapes](#milestone-7-additional-tools---fill-line-and-shapes)
8.  [Milestone 8: Undo/Redo System](#milestone-8-undoredo-system)
9.  [Milestone 9: Advanced Filters](#milestone-9-advanced-filters)
10. [Milestone 10: Layer System](#milestone-10-layer-system)
11. [Milestone 11: Polish and Performance Optimization](#milestone-11-polish-and-performance-optimization)

---

## 📋 Project Overview

### 🎯 What You'll Build
A fully functional image editor with drawing tools, filters, layers, and professional features including undo/redo, keyboard shortcuts, and an intuitive user interface.

### 🎓 Learning Objectives
By completing this guide, you will:
- Build responsive GUI applications using JavaFX
- Implement image-processing algorithms (blur, edge detection, convolution)
- Design extensible architectures using design patterns (Command, Observer, Factory)
- Optimize performance and memory usage for large images
- **Practice Test-Driven Development (TDD) with unit and E2E tests**
- Write comprehensive automated test suites
- Apply professional software development practices

### ⚙️ Prerequisites
Before starting, ensure you have:
- **Java 11+** installed and configured
- **Maven** for dependency management
- **Git** for version control
- Basic Java knowledge (syntax, collections, OOP)
- **Visual Studio Code** with Java Extension Pack installed

## 📅 Development Roadmap

### **Phase 1: Foundation**
Build the project structure and basic UI framework.

### **Phase 2: Core Features**
Implement essential drawing tools and image I/O.

### **Phase 3: Advanced Editing**
Add layers, filters, and undo/redo functionality.

### **Phase 4: Polish & Optimization**
Enhance performance, add advanced features, and finalize documentation.


## 🧪 Test-Driven Development Approach

This guide emphasizes **Test-Driven Development (TDD)** throughout. For each milestone:

### TDD Workflow
1. **Write Test First** - Define expected behavior with a failing test
2. **Implement Feature** - Write minimal code to pass the test
3. **Refactor** - Improve code quality while maintaining passing tests
4. **Repeat** - Continue cycle for each feature

### When to Write Tests
- **Always:** Business logic, algorithms, data transformations
- **Frequently:** UI components with TestFX, integration workflows
- **Selectively:** Simple getters/setters, obvious delegations

### Testing Tools Setup
```xml
<!-- Add to pom.xml -->
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.1</version>
        <scope>test</scope>
    </dependency>
    
    <!-- TestFX for JavaFX UI testing -->
    <dependency>
        <groupId>org.testfx</groupId>
        <artifactId>testfx-junit5</artifactId>
        <version>4.0.18</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito for mocking -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.8.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Test Structure
```
src/test/java/
├── com/pixelcraft/
│   ├── processor/
│   │   ├── ImageProcessorTest.java
│   │   ├── FilterTest.java
│   │   └── ConvolutionTest.java
│   ├── algorithms/
│   │   ├── FloodFillTest.java
│   │   └── BresenhamTest.java
│   ├── commands/
│   │   └── CommandHistoryTest.java
│   ├── model/
│   │   └── LayerManagerTest.java
│   └── integration/
│       ├── DrawingWorkflowTest.java
│       ├── FileOperationsTest.java
│       └── LayerUITest.java
```

---

# 🚀 Step-by-Step Milestones

## Milestone 1: Project Setup & Foundation

**🎯 Objective:** Create the project structure and configure the development environment.

**📚 What You'll Learn:**
- Maven project configuration
- JavaFX application structure
- Git workflow basics
- Project organization best practices
- Setting up testing frameworks

**✅ Tasks:**

1. **Create Maven Project Structure**
   ```bash
   mkdir -p src/main/java/com/pixelcraft
   mkdir -p src/main/resources
   mkdir -p src/test/java/com/pixelcraft
   ```

2. **Configure pom.xml**
   - Set Java 11+ as target version
   - Add JavaFX dependencies (org.openjfx)
   - Add JUnit 5, TestFX, and Mockito for testing
   - Configure Maven compiler and JAR plugins

3. **Initialize Git Repository**
   ```bash
   git init
   git add .
   git commit -m "Initial project setup"
   ```

4. **Create Main Application Class**
   - Extend `javafx.application.Application`
   - Implement `start()` method
   - Create initial window with title
   - Set minimum window size (800x600)

5. **Set Up Basic UI Layout**
   - Use `BorderPane` as root layout
   - Add placeholder for menu bar at top
   - Create placeholder for canvas area in center
   - Add placeholder for status bar at bottom

6. **Write First Test**
   ```pseudocode
   TEST testApplicationLaunches:
       app = CREATE new PixelCraft application
       ASSERT app is not null
   END TEST
   ```

**📋 Checkpoint:**
- [ ] Maven project builds without errors (`mvn clean compile`)
- [ ] Application window opens successfully
- [ ] Window has proper title and minimum size
- [ ] Git repository initialized with first commit
- [ ] Basic layout structure in place
- [ ] Test dependencies configured in pom.xml
- [ ] First test passes (`mvn test`)

**🧪 Testing Tasks:**
1. Write test to verify Application class exists
2. Write test to verify main method exists
3. Set up test folder structure matching main structure

**🎉 Milestone Achievement:**
You have a working JavaFX application with Maven and testing configured!

---

## Milestone 2: Canvas and Display System

**🎯 Objective:** Create the image display canvas and implement basic rendering.

**📚 What You'll Learn:**
- JavaFX Canvas and GraphicsContext
- Image rendering techniques
- Event handling basics
- Coordinate systems
- Testing UI components

**✅ Tasks:**

1. **Create Canvas Component (TDD)**
   - **Write test first:** Test canvas initialization
   - Add `Canvas` to center of layout
   - Initialize `GraphicsContext` for drawing
   - Set background color
   - Make canvas resizable

2. **Implement Image Model**
   - **Write test first:** Test Image class pixel access
   - Create `Image` class wrapping `BufferedImage`
   - Add methods for pixel access (`getPixel`, `setPixel`)
   - Store image dimensions and metadata

3. **Create Rendering Engine**
   - **Write test first:** Test image-to-canvas rendering
   - Implement `renderImage()` method
   - Convert `BufferedImage` to `WritableImage`
   - Draw image on canvas
   - Handle different image sizes

4. **Add Status Bar Information**
   - Display canvas dimensions
   - Show mouse coordinates
   - Add zoom level indicator
   - Show current tool name

5. **Implement Mouse Tracking**
   - Track mouse position on canvas
   - Update status bar with coordinates
   - Convert screen coordinates to image coordinates

**📋 Checkpoint:**
- [ ] Canvas displays properly in window
- [ ] Status bar shows canvas information
- [ ] Mouse coordinates update in real-time
- [ ] Canvas resizes with window
- [ ] Image model class created and tested
- [ ] **Unit tests pass for Image class**
- [ ] **Unit tests pass for coordinate transformations**

**🧪 Testing Tasks:**
1. Test Image class constructor and getters
2. Test pixel get/set operations
3. Test coordinate transformation functions
4. Test canvas initialization
5. Mock test rendering pipeline

**🎉 Milestone Achievement:**
You have a functional drawing canvas with coordinate tracking!

---

## Milestone 3: Image Loading and Saving

**🎯 Objective:** Implement file I/O for images in multiple formats.

**📚 What You'll Learn:**
- Java ImageIO library
- File chooser dialogs
- Error handling for file operations
- Image format conversions
- Testing file operations with temporary files

**✅ Tasks:**

1. **Create File Menu (TDD)**
   - **Write test first:** Test menu structure
   - Add New, Open, Save, Save As menu items
   - Add keyboard shortcuts (Ctrl+N, Ctrl+O, Ctrl+S)
   - Implement menu action handlers

2. **Implement Image Loading**
   - **Write test first:** Test loading PNG/JPG files
   - Create `FileChooser` with file filters
   - Use `ImageIO.read()` to load images
   - Support PNG, JPG, BMP formats
   - Display loaded image on canvas
   - Update window title with filename

3. **Implement Image Saving**
   - **Write test first:** Test save/load cycle
   - Create save dialog with format selection
   - Use `ImageIO.write()` for saving
   - Handle file overwrite confirmation
   - Support quality settings for JPG
   - Add file extension automatically

4. **Create New Image Dialog**
   - **Write test first:** Test dialog creation
   - Input fields for width and height
   - Default values (800x600)
   - Background color selection
   - Validation for reasonable sizes

5. **Add Error Handling**
   - Try-catch for file operations
   - User-friendly error messages
   - Handle corrupted files gracefully
   - Check disk space before saving

**📋 Checkpoint:**
- [ ] Can create new blank images
- [ ] Can open PNG, JPG, and BMP files
- [ ] Can save images in multiple formats
- [ ] File chooser shows appropriate filters
- [ ] Error messages display clearly
- [ ] **Unit tests pass for file I/O operations**
- [ ] **Integration tests verify save/load cycle**

**🧪 Testing Tasks:**
1. Test image loading with temporary test files
2. Test image saving and verify file contents
3. Test save/load round-trip preserves data
4. Test error handling with invalid files
5. Test format conversion (PNG → JPG)
6. Integration test: new → draw → save → load

**🎉 Milestone Achievement:**
Your editor can now load and save images!

---

## Milestone 4: Drawing Tools - Pencil and Eraser

**🎯 Objective:** Implement fundamental drawing tools with mouse interaction.

**📚 What You'll Learn:**
- Abstract class design patterns
- Mouse event handling
- Line drawing algorithms
- Graphics context operations
- Testing drawing operations

**✅ Tasks:**

1. **Create Abstract Tool Class (TDD)**
   - **Write test first:** Test Tool interface
   ```pseudocode
   ABSTRACT CLASS Tool:
       PROPERTIES:
           size: integer
           color: Color
       
       ABSTRACT METHODS:
           onMousePressed(event)
           onMouseDragged(event)
           onMouseReleased(event)
   END CLASS
   ```

2. **Implement Tool Manager**
   - **Write test first:** Test tool switching
   - Maintain reference to active tool
   - Switch between tools
   - Notify UI of tool changes
   - Store tool-specific settings

3. **Create Pencil Tool (TDD)**
   - **Write test first:** Test drawing on canvas
   - Implement `PencilTool` extending `Tool`
   - Draw on mouse press and drag
   - Use Bresenham's algorithm for smooth lines
   - Support variable brush sizes
   - Add anti-aliasing option

4. **Create Eraser Tool (TDD)**
   - **Write test first:** Test erasing pixels
   - Implement `EraserTool` extending `Tool`
   - Erase to transparency (alpha = 0)
   - Support variable eraser sizes
   - Show eraser cursor preview

5. **Add Toolbox Panel**
   - Create left sidebar with tool buttons
   - Show active tool with visual indicator
   - Add tool icons
   - Include tooltips with shortcuts

6. **Implement Color Picker Widget**
   - Use JavaFX `ColorPicker`
   - Display current color swatch
   - Add recent colors list
   - Support foreground/background colors

7. **Add Brush Size Slider**
   - Range: 1-50 pixels
   - Real-time preview
   - Display numeric value
   - Update cursor size

**📋 Checkpoint:**
- [ ] Can draw with pencil tool
- [ ] Can erase with eraser tool
- [ ] Brush size adjustable via slider
- [ ] Color picker works correctly
- [ ] Tools switch properly
- [ ] Drawing is smooth without gaps
- [ ] Cursor shows brush size
- [ ] **Unit tests pass for Tool classes**
- [ ] **Integration tests verify drawing workflows**

**🧪 Testing Tasks:**
1. Test Tool abstract class methods
2. Test ToolManager switching logic
3. Test PencilTool draws correct pixels
4. Test EraserTool clears pixels
5. Test Bresenham's line algorithm
6. Mock test: verify mouse events trigger drawing
7. Integration test: draw → verify pixels changed

**🎉 Milestone Achievement:**
You have functional drawing and erasing capabilities!

---

## Milestone 5: Zoom and Pan Navigation

**🎯 Objective:** Implement zoom and pan functionality for large images.

**📚 What You'll Learn:**
- Coordinate transformations
- JavaFX transforms and scaling
- Mouse wheel events
- Viewport management
- Testing transformation logic

**✅ Tasks:**

1. **Implement Zoom Levels (TDD)**
   - **Write test first:** Test zoom calculations
   - Define zoom range: 10% to 800%
   - Create zoom in/out methods
   - Add predefined zoom levels (25%, 50%, 100%, 200%, 400%)
   - Implement "Fit to Window" option

2. **Add Mouse Wheel Zoom**
   - **Write test first:** Test zoom center calculations
   - Handle scroll events
   - Zoom centered on mouse position
   - Update canvas transform
   - Clamp zoom to valid range

3. **Implement Pan Functionality**
   - Add scrollbars for large images
   - Pan with spacebar + drag
   - Update scrollbar positions
   - Keep image centered when possible

4. **Update Status Bar**
   - Display current zoom percentage
   - Show visible region coordinates
   - Update on zoom/pan changes

5. **Add View Menu Options**
   - Zoom In (Ctrl+=)
   - Zoom Out (Ctrl+-)
   - Actual Size (Ctrl+0)
   - Fit Window (Ctrl+Shift+0)

6. **Fix Coordinate Transformations (TDD)**
   - **Write test first:** Test screen-to-image conversion
   - Convert screen coordinates to image coordinates
   - Update tool coordinate calculations
   - Test drawing at various zoom levels

**📋 Checkpoint:**
- [ ] Mouse wheel zooms in/out
- [ ] Zoom menu items work correctly
- [ ] Can pan large images with scrollbars
- [ ] Drawing works correctly at all zoom levels
- [ ] Status bar shows correct zoom percentage
- [ ] Fit to Window scales appropriately
- [ ] **Unit tests pass for coordinate transformations**
- [ ] **Unit tests pass for zoom calculations**

**🧪 Testing Tasks:**
1. Test zoom level clamping (10%-800%)
2. Test screen-to-image coordinate conversion
3. Test zoom center calculation
4. Test fit-to-window calculation
5. Test drawing at 50% zoom produces correct pixels
6. Test drawing at 200% zoom produces correct pixels

**🎉 Milestone Achievement:**
You can now navigate images of any size!

---

## Milestone 6: Basic Image Adjustments

**🎯 Objective:** Implement brightness, contrast, and other basic adjustments.

**📚 What You'll Learn:**
- Pixel-level image processing
- Color manipulation algorithms
- Dialog creation
- Preview functionality
- Testing image processing with TDD

**✅ Tasks:**

1. **Create ImageProcessor Utility Class (TDD)**
   - **Write test first:** Test brightness adjustment
   - Static methods for image operations
   - Efficient pixel iteration
   - Color component extraction
   - Result image creation

2. **Implement Brightness/Contrast Dialog**
   - Two sliders: brightness (-100 to +100), contrast (-100 to +100)
   - Live preview checkbox
   - Apply/Cancel buttons
   - Reset to defaults option

3. **Implement Brightness Algorithm (TDD)**
   - **Write test first:** Verify brightness +50 on known pixel
   ```pseudocode
   FOR each pixel in image:
       newRGB = CLAMP(originalRGB + brightness, 0, 255)
   END FOR
   ```

4. **Implement Contrast Algorithm (TDD)**
   - **Write test first:** Verify contrast formula
   ```pseudocode
   factor = (259 * (contrast + 255)) / (255 * (259 - contrast))
   FOR each pixel in image:
       FOR each color channel (R, G, B):
           newValue = CLAMP(factor * (oldValue - 128) + 128, 0, 255)
       END FOR
   END FOR
   ```

5. **Add Grayscale Conversion (TDD)**
   - **Write test first:** Test luminosity formula
   - Use luminosity formula: 0.299R + 0.587G + 0.114B
   - Convert entire image
   - Add to Image menu

6. **Implement Rotate Operations (TDD)**
   - **Write test first:** Test 90° rotation
   - Rotate 90° clockwise
   - Rotate 90° counter-clockwise
   - Rotate 180°
   - Update canvas dimensions

7. **Implement Flip Operations (TDD)**
   - **Write test first:** Test horizontal flip
   - Flip horizontal
   - Flip vertical
   - Preserve image dimensions

8. **Add Image Menu**
   - Organize all adjustments
   - Add keyboard shortcuts
   - Group related operations

**📋 Checkpoint:**
- [ ] Brightness/contrast dialog works
- [ ] Preview updates in real-time
- [ ] Grayscale conversion produces correct results
- [ ] Rotate operations work correctly
- [ ] Flip operations work correctly
- [ ] All operations preserve image quality
- [ ] **Unit tests pass for all algorithms**
- [ ] **Tests verify edge cases (clamping)**

**🧪 Testing Tasks:**
1. Test brightness with RGB(100,100,100) + 50 = RGB(150,150,150)
2. Test brightness clamping at 255
3. Test brightness clamping at 0
4. Test contrast formula accuracy
5. Test grayscale: red(255,0,0) → gray(76,76,76)
6. Test 90° rotation: verify corner pixels
7. Test horizontal flip: verify pixel positions
8. Test operations chain: brightness → grayscale

**🎉 Milestone Achievement:**
Your editor can now perform basic image adjustments!

---

## Milestone 7: Additional Tools - Fill, Line, and Shapes

**🎯 Objective:** Expand the toolset with fill, line, and shape drawing tools.

**📚 What You'll Learn:**
- Flood fill algorithm (BFS)
- Bresenham's line algorithm
- Shape drawing with Graphics2D
- Queue data structures
- Testing algorithms with known inputs

**✅ Tasks:**

1. **Implement Fill Tool (TDD)**
   - **Write test first:** Test flood fill on simple pattern
   - Create `FillTool` class
   - Implement BFS flood fill algorithm
   - Add tolerance threshold slider
   - Handle alpha channel correctly
   - Optimize for performance

2. **Implement Line Tool (TDD)**
   - **Write test first:** Test Bresenham's algorithm
   - Create `LineTool` class
   - Show preview line while dragging
   - Use Bresenham's algorithm
   - Support thickness adjustment

3. **Implement Rectangle Tool**
   - Create `RectangleTool` class
   - Draw filled or outline mode
   - Show preview while dragging
   - Hold Shift for perfect squares

4. **Implement Ellipse Tool**
   - Create `EllipseTool` class
   - Draw filled or outline mode
   - Show preview while dragging
   - Hold Shift for perfect circles

5. **Implement Color Picker Tool**
   - Create `ColorPickerTool` class
   - Sample color on click
   - Update active color
   - Show magnified preview
   - Display RGB/Hex values

6. **Update Toolbox UI**
   - Add buttons for new tools
   - Create tool option panel
   - Show relevant options per tool
   - Add fill/outline toggle for shapes

**📋 Checkpoint:**
- [ ] Fill tool floods connected regions
- [ ] Line tool draws straight lines
- [ ] Rectangle tool draws rectangles and squares
- [ ] Ellipse tool draws ellipses and circles
- [ ] Color picker samples colors
- [ ] All tools show appropriate previews
- [ ] Tool options update per selected tool
- [ ] **Unit tests pass for flood fill**
- [ ] **Unit tests pass for Bresenham's algorithm**

**🧪 Testing Tasks:**
1. Test flood fill on 3x3 grid with barrier
2. Test flood fill with tolerance
3. Test Bresenham's line: (0,0) to (5,5)
4. Test Bresenham's line: (0,0) to (5,0) horizontal
5. Test Bresenham's line: (0,0) to (0,5) vertical
6. Test rectangle corner calculations
7. Test color picker samples correct pixel

**🎉 Milestone Achievement:**
You have a complete set of basic drawing tools!

---

## Milestone 8: Undo/Redo System

**🎯 Objective:** Implement comprehensive undo/redo using the Command pattern.

**📚 What You'll Learn:**
- Command design pattern
- Stack data structures
- State management
- Memory optimization techniques
- Testing design patterns

**✅ Tasks:**

1. **Create Command Interface (TDD)**
   - **Write test first:** Test Command execute/undo
   ```pseudocode
   INTERFACE Command:
       METHODS:
           execute() - Apply the command
           undo() - Reverse the command
           getDescription() - Return string description
   END INTERFACE
   ```

2. **Implement Command History Manager (TDD)**
   - **Write test first:** Test stack operations
   - Two stacks: undo and redo
   - `executeCommand()` method
   - `undo()` and `redo()` methods
   - History limit (default: 50)
   - Memory usage tracking

3. **Create Concrete Commands (TDD)**
   - **Write test first:** Test each command type
   - `DrawCommand` for drawing operations
   - `AdjustmentCommand` for brightness/contrast
   - `TransformCommand` for rotate/flip
   - `FilterCommand` for filters
   - Each stores before/after state

4. **Integrate Commands with Tools**
   - Wrap each drawing operation in a command
   - Capture image state before and after
   - Execute command through CommandHistory

5. **Add Keyboard Shortcuts**
   - Ctrl+Z for undo
   - Ctrl+Y for redo
   - Update Edit menu items
   - Enable/disable based on stack state

6. **Create History Panel (Optional)**
   - Show list of operations
   - Click to jump to any state
   - Show memory usage per command
   - Visual timeline

**📋 Checkpoint:**
- [ ] Ctrl+Z undoes last operation
- [ ] Ctrl+Y redoes undone operation
- [ ] Can undo multiple times in sequence
- [ ] Redo stack clears when new action performed
- [ ] Edit menu shows enabled/disabled Undo/Redo
- [ ] History limit works (doesn't grow forever)
- [ ] Works with all tools and transformations
- [ ] **Unit tests pass for Command pattern**
- [ ] **Integration tests verify undo/redo with multiple operations**

**🧪 Testing Tasks:**
1. Write unit tests for each Command class (execute/undo)
2. Test CommandHistory stack operations
3. Test undo limit enforcement (history cap)
4. Test redo stack clears on new command
5. Mock test: verify Command.execute() called
6. Integration test: draw → undo → redo workflow
7. Integration test: multiple operations undo chain

**🎉 Milestone Achievement:**
You now have a functional MVP image editor with undo/redo!

---

## Milestone 9: Advanced Filters

**🎯 Objective:** Implement image filters using convolution and other algorithms.

**📚 What You'll Learn:**
- Kernel-based image filtering (convolution)
- Matrix operations on images
- Gaussian blur algorithm
- Edge detection (Sobel operator)
- Multi-threaded processing for performance

**Tasks:**

1. **Create Filter Base Class**
   ```pseudocode
   ABSTRACT CLASS Filter:
       ABSTRACT METHODS:
           apply(inputImage) -> outputImage
           getName() -> string
       
       METHOD createResult(inputImage):
           RETURN new Image with same dimensions as input
       END METHOD
   END CLASS
   ```

2. **Implement Convolution Utility**
   ```pseudocode
   CLASS ConvolutionFilter EXTENDS Filter:
       PROPERTIES:
           kernel: 2D array of floats
           name: string
       
       METHOD apply(inputImage):
           resultImage = CREATE new image with same size
           offset = kernel.size / 2
           
           FOR each pixel (x, y) in image (excluding border):
               red, green, blue = 0
               
               FOR each kernel position (kx, ky):
                   neighborPixel = GET pixel at (x+kx-offset, y+ky-offset)
                   kernelValue = kernel[ky][kx]
                   
                   red += neighborPixel.red * kernelValue
                   green += neighborPixel.green * kernelValue
                   blue += neighborPixel.blue * kernelValue
               END FOR
               
               resultPixel = CLAMP(red, green, blue to 0-255)
               SET resultImage[x, y] = resultPixel
           END FOR
           
           RETURN resultImage
       END METHOD
   END CLASS
   ```

3. **Implement Blur Filter**
   ```pseudocode
   blurKernel = 3x3 matrix:
       [1/9, 1/9, 1/9]
       [1/9, 1/9, 1/9]
       [1/9, 1/9, 1/9]
   
   blurFilter = CREATE ConvolutionFilter("Blur", blurKernel)
   ```

4. **Implement Sharpen Filter**
   ```pseudocode
   sharpenKernel = 3x3 matrix:
       [ 0, -1,  0]
       [-1,  5, -1]
       [ 0, -1,  0]
   
   sharpenFilter = CREATE ConvolutionFilter("Sharpen", sharpenKernel)
   ```

5. **Implement Edge Detection**
   ```pseudocode
   sobelX = 3x3 matrix (detects vertical edges):
       [-1,  0,  1]
       [-2,  0,  2]
       [-1,  0,  1]
   
   sobelY = 3x3 matrix (detects horizontal edges):
       [-1, -2, -1]
       [ 0,  0,  0]
       [ 1,  2,  1]
   
   FOR each pixel:
       gradientX = APPLY sobelX kernel
       gradientY = APPLY sobelY kernel
       edgeMagnitude = SQRT(gradientX² + gradientY²)
   END FOR
   ```

6. **Implement Gaussian Blur**
   - Generate Gaussian kernel based on radius and sigma
   - More sophisticated than box blur

7. **Add Filter Progress Dialog**
   - Show progress bar for long operations
   - Allow cancellation
   - Use Task/Service for background processing

**📋 Checkpoint:**
- [ ] Blur filter creates blurred image
- [ ] Sharpen filter enhances edges
- [ ] Edge detection shows edges clearly
- [ ] Gaussian blur works smoothly
- [ ] Filters work on large images without freezing UI
- [ ] Progress shown for slow filters
- [ ] Filters integrated with undo/redo
- [ ] Multiple filters can be applied in sequence
- [ ] **Unit tests verify filter outputs with known inputs**
- [ ] **Performance tests measure filter execution time**

**🧪 Testing Tasks:**
1. **Unit Test:** Create 3x3 red image, apply grayscale, verify output
2. **Unit Test:** Test convolution with identity kernel (no change)
3. **Unit Test:** Test blur kernel produces expected pixel averaging
4. **Unit Test:** Test edge detection on simple patterns
5. **Performance Test:** Measure filter time on 4096x4096 image
6. **Integration Test:** Apply filter through UI and verify result

**🎉 Milestone Achievement:**
Your editor now has professional-grade image filters!

## Milestone 10: Layer System

**🎯 Objective:** Implement complete layer management with composition and blending.

**📚 What You'll Learn:**
- Layer composition techniques
- Alpha blending algorithms
- Dynamic list management
- Complex UI components (ListView)
- Composite design pattern
- Graphics compositing

**✅ Tasks:**

1. **Create Layer Class**
   ```pseudocode
   CLASS Layer:
       PROPERTIES:
           name: string
           content: Image
           visible: boolean = true
           opacity: float = 1.0 (range: 0.0 to 1.0)
           blendMode: BlendMode = NORMAL
       
       CONSTRUCTOR(name, width, height):
           this.name = name
           this.content = CREATE new Image(width, height)
       END CONSTRUCTOR
       
       METHODS:
           getContent() -> Image
           isVisible() -> boolean
           getOpacity() -> float
           setVisible(visible)
           setOpacity(opacity)
           setBlendMode(mode)
   END CLASS
   ```

2. **Create LayerManager**
   ```pseudocode
   CLASS LayerManager:
       PROPERTIES:
           layers: List of Layer objects
           activeLayerIndex: integer = 0
       
       METHOD addLayer(layer):
           ADD layer to layers list
       END METHOD
       
       METHOD getActiveLayer():
           RETURN layers[activeLayerIndex]
       END METHOD
       
       METHOD compositeImage():
           IF layers is empty THEN RETURN null
           
           Layer bottom = layers.get(0);
           BufferedImage result = new BufferedImage(
               bottom.getContent().getWidth(),
               bottom.getContent().getHeight(),
               BufferedImage.TYPE_INT_ARGB
           );
           
           Graphics2D g = result.createGraphics();
           for (Layer layer : layers) {
               if (layer.isVisible()) {
                   g.setComposite(AlphaComposite.getInstance(
                       AlphaComposite.SRC_OVER,
                       layer.getOpacity()
                   ));
                   g.drawImage(layer.getContent(), 0, 0, null);
               }
           }
           g.dispose();
           return result;
       }
       
       public void moveLayerUp(int index) { /* swap with index-1 */ }
       public void moveLayerDown(int index) { /* swap with index+1 */ }
       public void deleteLayer(int index) { layers.remove(index); }
   }
   ```

3. **Create Layers Panel UI**
   - ListView showing all layers
   - Buttons: Add, Delete, Duplicate, Merge
   - Eye icon for visibility toggle
   - Opacity slider for selected layer
   - Drag-and-drop to reorder (advanced)

4. **Integrate with Drawing**
   - All tools draw on active layer
   - Composite layers for display
   - Update canvas when layer changes

5. **Implement Layer Operations**
   - New layer
   - Duplicate layer
   - Delete layer
   - Merge layers
   - Flatten image

**📋 Checkpoint:**
- [ ] Can create multiple layers
- [ ] Can select active layer
- [ ] Drawing happens on active layer only
- [ ] Can toggle layer visibility
- [ ] Can adjust layer opacity
- [ ] Can reorder layers (drag-and-drop optional)
- [ ] Can merge/flatten layers
- [ ] Composite image displays correctly
- [ ] Layer thumbnails update properly
- [ ] Undo/redo works with layer operations
- [ ] **Unit tests verify layer composition**
- [ ] **E2E tests verify layer UI operations**

**🧪 Testing Tasks:**
1. **Unit Test:** Test alpha blending with 50% opacity
2. **Unit Test:** Test layer composition order (bottom to top)
3. **Unit Test:** Test merge operation combines layers correctly
4. **Unit Test:** Verify drawing on inactive layer doesn't affect it
5. **Integration Test:** Create layer → draw → toggle visibility → verify canvas
6. **E2E Test:** Add 3 layers through UI, verify order and composition

**🎉 Milestone Achievement:**
You now have a professional multi-layer editing system!

## Milestone 11: Polish and Performance Optimization

**🎯 Objective:** Add professional polish, optimize performance, and enhance UX.

**📚 What You'll Learn:**
- Performance profiling and optimization
- Memory management techniques
- Comprehensive keyboard shortcut systems
- Preferences/settings persistence
- Application packaging and deployment
- JAR creation and distribution

**✅ Tasks:**

1. **Add Comprehensive Keyboard Shortcuts**
   ```pseudocode
   METHOD setupKeyboardShortcuts():
       shortcuts = CREATE empty map of (KeyCombination -> Action)
       
       ADD to shortcuts:
           Ctrl+N -> onNew()
           Ctrl+O -> onOpen()
           Ctrl+S -> onSave()
           Ctrl+Z -> undo()
           Ctrl+Y -> redo()
           P -> selectTool("Pencil")
           E -> selectTool("Eraser")
           B -> selectTool("Brush")
           F -> selectTool("Fill")
       
       ON key pressed event:
           FOR each shortcut in shortcuts:
               IF shortcut key matches pressed key THEN
                   EXECUTE shortcut action
                   CONSUME event (prevent further handling)
                   BREAK
               END IF
           END FOR
       END ON
   END METHOD
   ```

2. **Optimize Canvas Rendering**
   - Only redraw when necessary
   - Use dirty regions
   - Cache composited image
   - Render at appropriate quality settings

3. **Implement Zoom Functionality**
   - Zoom in/out with mouse wheel
   - Zoom to fit
   - Zoom to actual size (100%)
   - Zoom presets (25%, 50%, 100%, 200%, 400%)
   - Pan with scrollbars or hand tool

4. **Add Preferences Dialog**
   - Default image size
   - Undo history limit
   - Auto-save interval
   - UI theme options
   - Performance settings

5. **Improve Error Handling**
   - Graceful handling of all errors
   - User-friendly error messages
   - Log errors for debugging
   - Recovery options

6. **Add Status Bar**
   - Image dimensions
   - Current zoom level
   - Mouse coordinates
   - Current tool
   - Memory usage (optional)

7. **Create About Dialog**
   - Application info
   - Version number
   - Credits
   - License information

8. **Package Application**
   - Create executable JAR
   - Add application icon
   - Create launcher scripts
   - Bundle JRE (optional, using jlink)

**Performance Optimizations:**

```pseudocode
CLASS PerformanceOptimization:
    PROPERTIES:
        cachedComposite: Image
        compositeNeedsUpdate: boolean = true
    
    METHOD invalidateComposite():
        compositeNeedsUpdate = true
    END METHOD
    
    METHOD getComposite():
        IF compositeNeedsUpdate THEN
            cachedComposite = layerManager.compositeImage()
            compositeNeedsUpdate = false
        END IF
        RETURN cachedComposite
}

// Only redraw when needed
private void requestRedraw() {
    if (!redrawScheduled) {
        redrawScheduled = true;
        Platform.runLater(() -> {
            redrawCanvas();
            redrawScheduled = false;
        });
    }
}
```

**📋 Checkpoint:**
- [ ] All keyboard shortcuts work correctly
- [ ] Zoom in/out works smoothly
- [ ] Application feels responsive (< 100ms response time)
- [ ] Large images (4096x4096) don't cause lag
- [ ] Memory usage is reasonable (< 500MB for typical use)
- [ ] Preferences can be saved and loaded
- [ ] Status bar shows useful real-time information
- [ ] Application can be packaged as executable JAR
- [ ] Error messages are user-friendly and helpful
- [ ] Application has professional appearance

**🎉🎉🎉 FINAL ACHIEVEMENT:**

Congratulations! You've completed PixelCraft! You now have:
- ✅ A professional-grade image editor
- ✅ Deep understanding of Java graphics programming
- ✅ Experience with JavaFX and UI design
- ✅ Knowledge of design patterns and best practices
- ✅ A portfolio project to showcase your skills

# 📚 Technical Reference Guide

This section provides detailed technical information for implementing the features above.

This section provides detailed technical information referenced throughout the milestones.

### 1. Image Loading and Display

The application must be able to handle image input and display it to the user in a viewable format.

- **Supported Formats:** Load common image formats (PNG, JPG/JPEG, BMP, GIF)
  - PNG: Lossless compression, supports transparency (alpha channel)
  - JPG/JPEG: Lossy compression, smaller file sizes, no transparency
  - BMP: Uncompressed or RLE compressed, simple format
  - GIF: Supports animation and transparency
- **Canvas Display:** Render the image on a canvas area with proper scaling
  - Display image at actual size when fully zoomed in (1:1 pixel ratio)
  - Support various resolutions from small thumbnails (32x32) to large images (4096x4096+)
  - Maintain aspect ratio during display transformations
- **Resolution Handling:** 
  - Display resolution information (width, height, DPI) in the status bar
  - Handle high-DPI displays appropriately
  - Provide visual feedback when loading large images
- **Memory Management:** Efficiently load and cache image data to prevent memory overflow

### 2. Basic Editing Tools

Implement a set of fundamental drawing and editing tools that respond to mouse input in real-time.

- **Pencil Tool:** Freehand drawing capability
  - Selectable color from a palette or RGB color picker
  - Adjustable brush size (1-50 pixels, recommended increments of 1px)
  - Brush shape options: Circle (soft edge), Square (hard edge)
  - Anti-aliasing support for smoother lines
  - Real-time cursor preview showing brush size and shape
  
- **Eraser Tool:** Remove pixels from the image
  - Adjustable eraser size matching pencil size range
  - Option to erase to transparency (PNG) or background color (JPG)
  - Soft vs. hard edge modes
  - Undo support for eraser strokes
  
- **Color Picker (Eyedropper):** Select colors from anywhere
  - Click to sample color from the image
  - Display current color in a swatch preview
  - Show color values in multiple formats (Hex, RGB, HSL)
  - Tooltip showing exact pixel coordinates
  
- **Fill Tool (Bucket Tool):** Flood-fill algorithm for color replacement
  - Selectable fill color
  - Adjustable threshold for color matching (0-100%, determines color similarity)
  - Fill contiguous regions or all connected areas of same color
  - Support for both opaque and semi-transparent fills
  - Preview of fill region before confirming
  
- **Zoom and Pan:** Navigate and view different magnification levels
  - Zoom levels: 10%, 25%, 50%, 75%, 100% (1:1), 150%, 200%, 400%, 800%
  - Zoom to fit window option
  - Mouse wheel scrolling for zoom in/out
  - Pan by holding spacebar and dragging, or using scroll bars
  - Display current zoom level in the status bar
  - Smooth transitions between zoom levels

### 3. Image Manipulation

Apply non-destructive (or with undo) transformations to the entire image or selected regions.

- **Brightness/Contrast Adjustment:** Modify image lighting
  - Brightness range: -100 to +100 (with preview)
  - Contrast range: -100 to +100 (with preview)
  - Reset to original values option
  - Real-time histogram display showing color distribution
  - Apply button to confirm changes or cancel to revert
  
- **Grayscale Conversion:** Convert color images to grayscale
  - Weighted grayscale conversion formula: 0.299R + 0.587G + 0.114B (standard luminosity method)
  - Option to preserve or discard after conversion
  - Grayscale mode indicator in status bar
  
- **Rotate and Flip:** Reorient the image
  - Rotate 90° clockwise
  - Rotate 90° counter-clockwise
  - Rotate 180°
  - Flip horizontally (mirror left-to-right)
  - Flip vertically (mirror top-to-bottom)
  - All operations update image dimensions and canvas accordingly
  - Display current rotation state visually
  
- **Color Adjustment:** Additional color manipulations
  - Saturation adjustment (0-200%)
  - Hue rotation (0-360°)
  - Invert colors (negative image effect)

### 4. Saving Images

Persist edited images to disk in a user-selected format.

- **Save Functionality:**
  - Save As dialog with file type selection
  - Default save format: PNG (preserves transparency)
  - Support export to JPG and BMP formats
  - Filename validation (prevent invalid characters, reserved names)
  - Overwrite confirmation for existing files
  
- **Save Options:**
  - Compression level selection for PNG (1-9)
  - JPG quality setting (1-100%)
  - Metadata preservation (EXIF data for JPG, timestamps)
  - Auto-save feature (optional): periodic snapshots to temporary location

## II. Advanced Features (Optional)

These features enhance the application's capabilities and demonstrate mastery of advanced concepts.

### 1. More Advanced Tools

Extend the basic toolset with shape drawing and advanced selection capabilities.

- **Line Tool:** Draw straight lines
  - Click two points to draw a line between them
  - Adjustable thickness (1-50 pixels)
  - Selectable color and line style (solid, dashed, dotted)
  - Preview line while dragging to second endpoint
  - Option to draw with anti-aliasing for smooth appearance
  
- **Rectangle/Ellipse Tools:** Draw geometric shapes
  - Draw filled or outlined shapes
  - For rectangles: Draw by clicking and dragging from corner to opposite corner
  - For ellipses: Draw by clicking and dragging from top-left to bottom-right of bounding box
  - Adjustable outline thickness
  - Toggle between filled and outline-only modes
  - Hold Shift to draw perfect squares/circles
  - Preview shape while dragging
  
- **Selection Tool:** Select regions for targeted editing
  - **Rectangular Selection:** Select rectangular regions
  - **Free-form/Lasso Selection:** Draw arbitrary shape selection marquee (marching ants animation) showing selected area
  - Cut, Copy, Paste operations on selections
  - Selection transformation (move, scale, rotate selected region)
  - Feather selection edges for smooth transitions
  - Invert selection, select all, deselect options
  
- **Clone Stamp Tool:** Copy pixels from one area to another
  - Ctrl+Click to set clone source point
  - Paint with sampled pixels from source
  - Useful for removing unwanted objects or duplicating regions
  - Show crosshair indicator at clone source
  - Adjustable brush size and opacity

### 2. Advanced Filters

Implement image processing algorithms for artistic and corrective effects.

- **Blur Filter:** Reduce image noise and detail
  - **Gaussian Blur:** Standard blur with adjustable radius (1-50 pixels)
  - **Motion Blur:** Directional blur with angle and distance parameters
  - **Box Blur:** Simple averaging blur
  - Real-time preview of blur effect
  - Intensity slider (1-100%)
  
- **Sharpen Filter:** Enhance edges and details
  - **Unsharp Mask:** Professional sharpening with radius and amount controls
  - **High Pass Sharpening:** Advanced technique for fine details
  - Amount adjustment (0-200%)
  - Radius adjustment (0.1-10 pixels)
  - Preview before applying
  
- **Edge Detection Filter:** Identify and highlight edges
  - **Sobel Operator:** Gradient-based edge detection
  - **Canny Edge Detection:** Advanced edge detection algorithm
  - **Laplacian:** Second-derivative edge detection
  - Threshold adjustment for sensitivity
  - Option to show detected edges as lines or highlighted regions
  
- **Additional Filters:**
  - Sepia tone for vintage effect
  - Pixelate/Mosaic effect
  - Oil painting effect
  - Emboss effect
  - Color posterization

### 3. Layers

Implement a non-destructive editing system with layer support.

- **Layer Management:**
  - Create, delete, and duplicate layers
  - Reorder layers (move up/down in stack)
  - Rename layers for organization
  - Layer visibility toggle (eye icon)
  - Layer opacity adjustment (0-100%)
  - Lock/unlock layers to prevent accidental editing
  
- **Layer Operations:**
  - Merge layers (combine two adjacent layers)
  - Flatten image (merge all layers into one)
  - Create layer groups for organization
  - Blending modes: Normal, Multiply, Screen, Overlay, etc.
  - Layer masks for selective visibility
  
- **Layer Panel UI:**
  - Thumbnail preview of each layer
  - Visual indication of active layer
  - Drag-and-drop layer reordering

### 4. Undo/Redo Functionality

Provide complete operation history for non-destructive editing.

- **History Stack:**
  - Undo (Ctrl+Z): Revert last action
  - Redo (Ctrl+Y): Restore undone action
  - Multiple undo/redo levels (minimum 20, recommended 50+)
  - Clear history on new image or save (configurable)
  
- **History Management:**
  - Visual history timeline showing thumbnail of each state
  - Jump to any point in history
  - Branch history when making changes after undo
  - Memory-efficient storage (delta compression or reference-based storage)
  - Status bar showing current position in history
  
- **Supported Operations:**
  - All drawing operations (pencil, eraser, shapes)
  - All image adjustments (brightness, contrast, rotation)
  - Crop operations
  - Layer operations
  - Paste operations

## III. Technical Considerations

### 1. Programming Language and Framework

**REQUIRED: Java with JavaFX**

This challenge must be completed using **Java** (11+) and **JavaFX** as the GUI framework. JavaFX is the recommended and required framework for this project because it provides modern UI controls, hardware-accelerated rendering, and better support for media and graphics than legacy toolkits.

#### Java Libraries (Required/Recommended):

**Image I/O and Processing:**
- **Java ImageIO** (Built-in): `javax.imageio` package
  - Load and save PNG, JPG, BMP formats
  - Standard Java image I/O solution

- **BufferedImage** (Built-in): `java.awt.image.BufferedImage` (useful when interoperating with ImageIO)
  - In-memory image representation
  - Direct pixel access and manipulation

**Graphics and Drawing (JavaFX):**
- **JavaFX Canvas & Scene Graph:** `javafx.scene.canvas.Canvas`, `javafx.scene.image.Image`, `javafx.scene.image.WritableImage`
  - Use `Canvas` for pixel-level drawing and `ImageView`/`WritableImage` for image display
  - Event handling via `javafx.scene.input` and `EventHandler`

**Collections and Data Structures:**
- **java.util.Stack** or **LinkedList**: For undo/redo history
- **java.util.ArrayList**: For layers, tool options
- **HashMap**: For caching, configuration

#### Why Java + JavaFX for This Project:

1. **Cross-platform:** Run on Windows, Mac, Linux without modifications
2. **Performance:** JavaFX provides hardware acceleration for smoother rendering
3. **Modern UI:** JavaFX includes modern controls, CSS styling, and FXML
4. **Learning Value:** Teaches GUI programming, event-driven design, and graphics
5. **Ecosystem:** Use Maven to manage OpenJFX dependencies and plugins

#### Setup Requirements:

- **Java Version:** Java 11 or higher (Java 17+ recommended)
- **IDE:** Visual Studio Code with Java Extension Pack (includes Java language support, debugging, and Maven integration)
- **Build Tool:** Maven (required) — manage JavaFX via `org.openjfx` dependencies in `pom.xml`
- **JavaFX Dependencies:** Add appropriate `org.openjfx` modules (platform-specific classifiers may be required for runtime)

### 2. Performance Optimization

Ensure the application remains responsive even with large images.

- **Image Rendering:**
  - Use double buffering to prevent flicker
  - Render only visible canvas area (culling/viewport optimization)
  - Cache scaled versions for zoom levels to avoid recalculation
  - Implement lazy loading for high-resolution images
  
- **Pixel Operations:**
  - Use efficient algorithms for flood-fill (BFS queue vs. recursion)
  - Batch pixel updates instead of individual pixel operations
  - Consider multi-threading for long operations (blur, filters)
  - Use lookup tables for color transformations
  
- **Memory Management:**
  - Implement image compression in undo history
  - Limit undo stack size based on available memory
  - Use streaming for very large image files
  - Clean up unused objects and temporary data structures
  
- **UI Responsiveness:**
  - Implement async operations for file I/O
  - Use progress indicators for long-running operations
  - Keep UI thread responsive with worker threads for processing
  - Throttle mouse events for drawing operations

### 3. User Interface Design

Create an intuitive interface that guides users effectively.

#### Main Components:

- **Menu Bar:**
  - File: New, Open, Save, Save As, Recent Files, Exit
  - Edit: Undo, Redo, Cut, Copy, Paste, Select All
  - View: Zoom levels, Fit to Window, Show Rulers, Show Grid
  - Image: Rotate, Flip, Resize, Crop
  - Filters: All filter options
  - Help: About, Documentation, Keyboard Shortcuts
  
- **Toolbar:**
  - Tool buttons with icons (pencil, eraser, shapes, etc.)
  - Quick access buttons for common operations
  - Keyboard shortcut hints in tooltips
  - Tool options panel for active tool settings
  
- **Left Panel (Toolbox):**
  - Tool selection with visual indication of active tool
  - Tool options: Size, opacity, color
  - Color swatches (foreground/background color selector)
  - Recently used colors
  
- **Right Panel (Layers/History):**
  - Tabs for switching between layers and history views
  - Layer thumbnails with visibility toggles
  - History timeline showing previous states
  - Operation descriptions (e.g., "Pencil Stroke", "Brightness +20")
  
- **Canvas Area:**
  - Main editing area with image display
  - Scrollbars for large images
  - Crosshair cursor showing brush size/shape
  - Grid overlay (optional, for alignment)
  - Rulers on top and left edges (optional)
  
- **Status Bar:**
  - Image dimensions and color mode
  - Current zoom level
  - Mouse coordinates (pixel position)
  - File path and modification indicator (*)
  - Operation feedback messages

#### Design Principles:

- **Discoverability:** Make tools and features obvious and easy to find
- **Feedback:** Provide immediate visual feedback for all actions
- **Consistency:** Use consistent icons, colors, and layouts throughout
- **Accessibility:** Support keyboard shortcuts for all major operations
- **Responsiveness:** All UI elements should respond within 100ms of user input

#### UI Wireframes:

**Main Application Window Layout:**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ PixelCraft - Image Editor [File] [Edit] [View] [Image] [Filters] [Help]   │
├─────────────────────────────────────────────────────────────────────────────┤
│ [≡] [📁] [💾] [↶] [↷] | [✏️] [🧹] [🎨] [🪣] [📏] [⬜] [⭕] [🔷] [🔨]  │
│                        | [B] [C] [S] [H] | 🔍+ 🔍- 100% Fit                │
├──────────────────┬──────────────────────────────────────────────┬───────────┤
│   TOOLBOX        │                                              │  LAYERS/  │
│ ┌──────────────┐ │                                              │  HISTORY  │
│ │ Tools:       │ │         CANVAS AREA                         │ ┌────────┐ │
│ │ [✏️ ]Pencil  │ │                                              │ │ Layers │ │
│ │ [🧹]Eraser   │ │      [Image displayed here                 │ │───────-│ │
│ │ [🎨]Picker   │ │       at current zoom level]               │ │ Layer 1│ │
│ │ [🪣]Fill     │ │                                              │ │ ✓ ───  │ │
│ │ [📏]Line     │ │                                              │ │ Layer 0│ │
│ │ [⬜]Rect     │ │      (Scrollable)                           │ │ ✓ ───  │ │
│ │ [⭕]Ellipse  │ │                                              │ │───────-│ │
│ │ [🔷]Select   │ │                                              │ │ History│ │
│ │ [🔨]Clone    │ │                                              │ │───────-│ │
│ ├──────────────┤ │                                              │ │ Pencil │ │
│ │ Size:  [◀─► ]│ │         ↓ Scrollbar                          │ │ Stroke │ │
│ │        12 px │ │                                              │ │───────-│ │
│ ├──────────────┤ │ ─────────────────────────────────────►      │ │Brighten│ │
│ │ Opacity:     │ │                                              │ │ +20    │ │
│ │ [◀─► ]100%  │ │                                              │ └────────┘ │
│ ├──────────────┤ │                                              │            │
│ │ ■ Foreground │ │                                              │            │
│ │ ■ Background │ │                                              │            │
│ │ [Swap ↔]     │ │                                              │            │
│ ├──────────────┤ │                                              │            │
│ │ Recent Colors:│ │                                              │            │
│ │ ■ ■ ■ ■ ■   │ │                                              │            │
│ │ ■ ■ ■ ■ ■   │ │                                              │            │
│ └──────────────┘ │                                              │            │
└──────────────────┴──────────────────────────────────────────────┴───────────┘
│ Image: 800×600 RGB | Zoom: 100% | Pos: (245, 156) | Mode: Drawing         │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Detailed Component Breakdown:**

```
┌─── MENU BAR ───────────────────────────────────────────────────┐
│ File    Edit    View         Image    Filters    Help           │
│ ├─New   ├─Undo  ├─Zoom In   ├─Rotate ├─Blur    └─About         │
│ ├─Open  ├─Redo  ├─Zoom Out  ├─Flip   ├─Sharpen   Help (F1)     │
│ ├─Save  ├─Cut   ├─Fit       ├─Crop   ├─Edge Det ├─Shortcuts    │
│ ├─Save… ├─Copy  ├─100%      ├─Resize ├─Grayscale│             │
│ ├─Recent├─Paste ├─Rulers    └─Canvas ├─Sepia    │             │
│ └─Exit  └─SelectAll          Size... └─More...   │             │
└─────────────────────────────────────────────────────────────────┘

┌─── TOOLBAR ────────────────────────────────────────────────────┐
│ [≡] [📁] [💾] [↶] [↷] | [Tool Icons] | Zoom: [100%▼] [🔍] [🔍-]│
│ File New  Save  Undo Redo  Tools         Level    Zoom In Out  │
└─────────────────────────────────────────────────────────────────┘

┌─── TOOLBOX (Left Panel) ────────────────────────────────────────┐
│ ┌─ TOOLS ──────────────────┐                                    │
│ │ [•] Pencil (P)           │  Current tool selected             │
│ │ [ ] Eraser (E)           │  (shown with radio button)         │
│ │ [ ] Color Picker (C)     │                                    │
│ │ [ ] Fill Bucket (B)      │                                    │
│ │ [ ] Line (L)             │                                    │
│ │ [ ] Rectangle (R)        │                                    │
│ │ [ ] Ellipse (O)          │                                    │
│ │ [ ] Selection (S)        │                                    │
│ │ [ ] Clone Stamp (K)      │                                    │
│ └──────────────────────────┘                                    │
│                                                                 │
│ ┌─ TOOL OPTIONS ───────────┐                                    │
│ │ Brush Size:              │                                    │
│ │ [◀────[●]────▶] 12 px   │                                    │
│ │                          │                                    │
│ │ Opacity:                 │                                    │
│ │ [◀────[●]────▶] 100%    │                                    │
│ │                          │                                    │
│ │ Brush Shape:             │                                    │
│ │ [◯ Circle] [ ] Square   │                                    │
│ └──────────────────────────┘                                    │
│                                                                 │
│ ┌─ COLOR SELECTOR ──────────┐                                   │
│ │ Foreground: ■ (Black)     │                                   │
│ │ Background: ■ (White)     │                                   │
│ │ [↔ SWAP COLORS]           │                                   │
│ │                           │                                   │
│ │ [Pick color from image]   │                                   │
│ │ (Click to select from     │                                   │
│ │  canvas with eyedropper)  │                                   │
│ └───────────────────────────┘                                   │
│                                                                 │
│ ┌─ RECENT COLORS ───────────┐                                   │
│ │ ■ ■ ■ ■ ■ ■ ■ ■           │                                   │
│ │ ■ ■ ■ ■ ■ ■ ■ ■           │                                   │
│ └───────────────────────────┘                                   │
└─────────────────────────────────────────────────────────────────┘

┌─── RIGHT PANEL (Layers & History) ─────────────────────────────┐
│ [Layers ▼] [History ▼]                                         │
│                                                                 │
│ ┌─ LAYERS TAB ──────────────────────────────────────────────┐  │
│ │ [+ Add Layer] [Delete] [Duplicate]                        │  │
│ │                                                            │  │
│ │ Layer 2 (Current)                                          │  │
│ │ [Thumbnail] ○ Opacity: [◀────●──▶] 100%                 │  │
│ │ ├─ Blend Mode: [Normal ▼]                                │  │
│ │ ├─ [✓] Visible [🔒] Lock                                 │  │
│ │ └─ Layer 2 - Shapes                                       │  │
│ │                                                            │  │
│ │ Layer 1                                                    │  │
│ │ [Thumbnail] ◌ Opacity: [◀────●──▶] 85%                  │  │
│ │ ├─ Blend Mode: [Normal ▼]                                │  │
│ │ ├─ [✓] Visible [  ] Lock                                 │  │
│ │ └─ Layer 1 - Edits                                        │  │
│ │                                                            │  │
│ │ Layer 0 (Background)                                       │  │
│ │ [Thumbnail] ◌ Opacity: [◀────●──▶] 100%                 │  │
│ │ ├─ Blend Mode: [Normal ▼]                                │  │
│ │ ├─ [ ] Visible [🔒] Lock                                 │  │
│ │ └─ Background                                             │  │
│ └─────────────────────────────────────────────────────────────┘  │
│                                                                 │
│ ┌─ HISTORY TAB ─────────────────────────────────────────────┐  │
│ │ [Clear History]                                            │  │
│ │                                                            │  │
│ │ ▲ [Thumbnail] Pencil Stroke (28 bytes)                  │  │
│ │ ▲ [Thumbnail] Brightness +20 (16 bytes)                 │  │
│ │ ▲ [Thumbnail] Flip Horizontal (8 bytes)                 │  │
│ │ ● [Thumbnail] Rotate 90° (8 bytes) ← Current State      │  │
│ │   [Thumbnail] Color Change (12 bytes) ← Undone           │  │
│ │   [Thumbnail] Grayscale (8 bytes) ← Undone              │  │
│ │                                                            │  │
│ └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘

┌─── STATUS BAR ──────────────────────────────────────────────────┐
│ Image: 1920×1080 RGB | Zoom: 75% | Pos: (512, 768) | Ready     │
└─────────────────────────────────────────────────────────────────┘
```

**Common Dialog Layouts:**

```
┌─────────────── Open Image ──────────────────┐
│ Look in: [Documents ▼]                      │
│ ┌──────────────────────────────────────────┐│
│ │ 📁 Folder 1           📄 image.png      ││
│ │ 📁 Folder 2           📄 photo.jpg      ││
│ │ 📁 Pictures           📄 drawing.bmp    ││
│ │                       📄 animation.gif  ││
│ └──────────────────────────────────────────┘│
│ File name: [image.png           ]         │
│ File type: [All Supported (*.png, *.jpg)] │
│            [PNG Images (*.png)]           │
│            [JPG Images (*.jpg, *.jpeg)]   │
│            [BMP Images (*.bmp)]           │
│            [All Files (*.*)]              │
│ [ Cancel ]        [ Open ]                │
└─────────────────────────────────────────────┘

┌─────────── Brightness/Contrast ──────────────┐
│ Preview: [✓] Enable                         │
│                                             │
│ Brightness:                                 │
│ [◀──────────[●]──────────▶]                │
│  -100              0              +100     │
│  Value: 0                                  │
│                                             │
│ Contrast:                                   │
│ [◀──────────[●]──────────▶]                │
│  -100              0              +100     │
│  Value: 0                                  │
│                                             │
│ [  Reset  ]    [ Cancel ]  [  Apply  ]    │
└─────────────────────────────────────────────┘

┌────────────── Color Picker ────────────────┐
│ ┌─────────────────────────────────────┐    │
│ │ [  Color Gradient Area      ]       │    │
│ │ │                         │         │    │
│ │ │     (Click to select)   │         │    │
│ │ │                         │         │    │
│ │ └─────────────────────────┘         │    │
│ │ Saturation / Value slider ▼         │    │
│ └─────────────────────────────────────┘    │
│                                             │
│ H: [180 ▼] S: [50 ▼] V: [80 ▼]           │
│ R: [102 ▼] G: [179 ▼] B: [204 ▼]        │
│ Hex: #66B3CC                               │
│                                             │
│ Current: ■    Previous: ■                 │
│                                             │
│ [ Cancel ]           [  OK  ]             │
└────────────────────────────────────────────┘
```

### 4. Error Handling and Validation

Prevent crashes and data loss through comprehensive error handling.

- **File Operations:**
  - Verify file format before attempting to load
  - Handle corrupted image files gracefully
  - Check available disk space before saving
  - Provide error messages with suggested solutions
  - Implement auto-save to prevent data loss
  
- **User Input Validation:**
  - Validate numeric inputs (brush size, opacity, etc.)
  - Prevent operations on unsupported image formats
  - Handle edge cases (0-pixel brush, empty selection, etc.)
  
- **Memory and Performance:**
  - Detect and handle out-of-memory conditions
  - Provide warnings for extremely large images
  - Implement operation timeouts for long processes
  
- **Exception Handling:**
  - Use try-catch blocks appropriately
  - Log errors for debugging
  - Display user-friendly error messages (not technical stack traces)
  - Provide recovery options when possible

## IV. Suggested Implementation Steps (Java-Specific)

Follow this structured approach to build the application incrementally in Java, testing as you go.

### Phase 1: Foundation (Java Project Setup)
1. **Java Project Setup:**
   - Create Java project structure: `src/`, `resources/`, `tests/`
  - Set up a `pom.xml` for Maven (manage JavaFX with `org.openjfx` dependencies)
   - Configure Java version (11+) in build configuration
   - Add JavaDoc comments to all classes
   - Set up version control (Git)
   
2. **Application Framework (JavaFX):**
  - Create main `Stage` and `Scene` window
  - Use layout panes (e.g., `BorderPane`) for menu, toolbar, canvas, status bar layout
  - Use `Canvas` or `ImageView` for image display
  - Implement `setOnCloseRequest` on the `Stage` for window events
  - Create placeholder for image canvas rendering (FXML optional)
  - Set application icon and window title

3. **Image Data Structure (Java-Specific):**
   - Create `Image` class wrapping `BufferedImage`
   - Implement methods to access/modify pixels using `getRGB()` and `setRGB()`
   - Add metadata getters (width, height, color model)
   - Plan command pattern for undo/redo with abstract `Command` class
   - Use `Stack<Command>` for history management

### Phase 2: Core Features (MVP)
4. **Image Loading and Display (Java ImageIO + JavaFX):**
  - Implement file open dialog using JavaFX `FileChooser`
  - Use `ImageIO.read(File)` or `javafx.scene.image.Image` to load images
  - Render `BufferedImage` to a `Canvas` or convert to `WritableImage`/`ImageView` for display
  - Implement scroll/pan behaviour with JavaFX `ScrollPane` or viewport transforms
   - Add error handling with `IOException` and `IllegalArgumentException`
   - Display file name in title bar
   
5. **Basic Tools - Pencil and Eraser (JavaFX Events):**
  - Use `Canvas` and its `GraphicsContext` for drawing
  - Implement mouse handlers (`setOnMousePressed`, `setOnMouseDragged`, `setOnMouseReleased`) for drawing
  - Create abstract `Tool` class with `onMousePressed()`, `onMouseDragged()`, `onMouseReleased()`
  - Implement `PencilTool` class that writes to the canvas or underlying pixel buffer
  - Implement `EraserTool` class that clears pixels or sets alpha to transparent
  - Create `ToolManager` class to switch between tools
  - Implement color picker with JavaFX `ColorPicker`
  - Add brush size with JavaFX `Slider`
  - Draw cursor preview using an overlay `Canvas` or custom cursor nodes

6. **Zoom and Pan (Graphics Transformation):**
  - Store zoom level as a double (0.1 to 8.0)
  - Use scene transforms (scale/translate) or `GraphicsContext` transforms for zoom rendering
  - Implement coordinate transforms to map mouse events to image pixels
  - Add predefined zoom levels in menu (10%, 25%, 50%, 75%, 100%, 200%, 400%)
  - Use `ScrollPane` or translate/pan gestures for manual pan
  - Update status bar with current zoom percentage

7. **Image Manipulation (BufferedImage / WritableImage Operations):**
  - Create `ImageProcessor` utility class with static methods
  - Implement brightness/contrast using pixel iteration and direct pixel writing
  - Implement grayscale using luminosity formula
  - Implement rotate/flip using transforms or manual pixel copying between `WritableImage`/`BufferedImage`
  - Wrap each operation in `Command` subclass for undo/redo

8. **Save Functionality (Java ImageIO + JavaFX):**
  - Implement save dialog using JavaFX `FileChooser` with file filters
  - Use `ImageIO.write(BufferedImage, "png", File)` for PNG export (convert `WritableImage` when needed)
  - Support JPG export with quality settings via `JPEGImageWriteParam`
  - Add file extension validation and auto-append
  - Implement overwrite confirmation dialog with JavaFX `Alert`
  - Test save and reload functionality

### Phase 3: Enhanced Features (Java Collections & Events)
9. **Additional Basic Tools (Tool Pattern):**
   - Implement `ColorPickerTool` using `getPixel()` method
   - Implement `FillTool` with BFS flood-fill algorithm
   - Use `Queue<Point>` for efficient BFS implementation
   - Implement `LineTool` with Bresenham's line algorithm
   - Implement `RectangleTool` and `EllipseTool` using `Graphics2D` shape rendering
   - Create drawable preview during tool operation
   
10. **Selection Tool (Custom Graphics):**
    - Implement `RectangleSelection` with visual marquee
    - Use `Timer` for marching ants animation effect
    - Implement free-form selection with polygon vertices
    - Implement cut/copy/paste using `BufferedImage` clipping
    - Add `Clipboard` integration for system copy/paste
    
11. **Robust Undo/Redo System (Command Pattern):**
    - Create abstract `Command` interface with `execute()` and `undo()`
    - Implement concrete commands: `DrawCommand`, `RotateCommand`, `BrightnessCommand`, etc.
    - Store `BufferedImage` snapshots in command history
    - Use `Stack<Command>` for undo and `Stack<Command>` for redo
    - Implement `JMenuItem` listeners for Ctrl+Z and Ctrl+Y
    - Add visual history timeline panel
    
12. **Layers System (Data Structure Management):**
    - Create `Layer` class containing `BufferedImage` and metadata
    - Create `LayerStack` class managing `ArrayList<Layer>`
    - Implement layer visibility (opacity tracking)
    - Create layers panel UI with `JList` and custom renderer
    - Implement layer operations (merge, delete, duplicate)
    - Composite layers when rendering using alpha blending

### Phase 4: Advanced Features (Algorithm Implementation)
13. **Advanced Filters (Image Processing Algorithms):**
    - Create abstract `Filter` class in `ImageProcessor`
    - Implement Gaussian blur using kernel convolution
    - Implement sharpen filter with kernel matrix
    - Implement Sobel edge detection using gradient calculation
    - Use `BufferedImage` raster for efficient pixel access
    - Implement progress dialog for long operations
    - Consider `javafx.concurrent.Task` or `Service` for non-blocking filter execution
    
14. **Advanced Tools (Sophisticated Drawing):**
    - Implement `CloneStampTool` with source tracking
    - Implement additional filters (sepia, posterize, oil painting)
    - Use double-click or Ctrl+click for clone source selection
    
15. **Performance Optimization (Java Best Practices):**
    - Use `BufferedImage.TYPE_INT_RGB` or `TYPE_INT_ARGB` for efficiency
    - Implement viewport culling - only render visible area
    - Cache scaled versions for zoom levels
    - Use `Graphics2D` rendering hints for quality
    - Profile with JProfiler or YourKit
    - Consider native array access for pixel operations
    - Use `java.util.concurrent` for multi-threaded filtering
    
16. **Polish (Professional Java Application):**
    - Add keyboard shortcuts using JavaFX `KeyCombination` and scene accelerators
    - Implement drag-and-drop using JavaFX drag-and-drop APIs
    - Create preferences dialog using JavaFX `Dialog` or a dedicated `Stage` for settings
    - Add comprehensive `Help` menu
    - Implement `setOnCloseRequest` on the primary `Stage` for save-on-exit confirmation
    - Package as executable JAR or use Launch4j for .exe
    - Add splash screen with `SplashScreen`

### Test-Driven Development (TDD) Strategy

**Approach:** Write tests first, then implement features to make tests pass.

#### Unit Tests (JUnit 5)
- **ImageProcessor utilities:** Test pixel manipulation, color conversions, transformations
- **Algorithms:** Test flood-fill, Bresenham's line, convolution kernels
- **Filters:** Test blur, sharpen, edge detection with known inputs/outputs
- **Command pattern:** Test undo/redo operations
- **Layer management:** Test compositing, opacity, blending

**Example TDD Workflow:**
```pseudocode
// 1. Write failing test first
TEST testGrayscaleConversion:
    input = CREATE test image with RGB(255, 0, 0)  // Pure red
    result = ImageProcessor.grayscale(input)
    grayValue = GET red channel from result pixel
    ASSERT grayValue equals 76  // Expected: 0.299 * 255
END TEST

// 2. Implement minimum code to pass
// 3. Refactor
```

#### E2E/Integration Tests (TestFX)
- **Tool workflows:** Test complete drawing operations
- **File operations:** Test save/load cycle with various formats
- **UI interactions:** Test menu actions, keyboard shortcuts, tool switching
- **Layer operations:** Test add/delete/merge layers through UI

**Example E2E Test:**
```pseudocode
TEST testDrawAndUndoWorkflow:
    CLICK on pencil tool button
    DRAG mouse from (100, 100) to (200, 200)
    PRESS Ctrl+Z
    ASSERT canvas is clear
END TEST
```

#### Performance Tests
- Measure filter execution time with large images (4096x4096)
- Test memory usage during layer composition
- Benchmark rendering performance

#### Best Practices
- **Write tests before implementation** whenever possible
- **Test edge cases:** empty images, null values, extreme sizes
- **Use test fixtures:** Create reusable test images and mocks
- **Aim for high coverage:** Minimum 80% code coverage for core logic
- **Run tests frequently:** Use continuous testing in VS Code

---

### Comprehensive Testing Examples

#### Example 1: Testing ImageProcessor with TDD

**Step 1: Write Failing Test**
```pseudocode
TEST testBrightnessIncrease:
    // Arrange: Create test image with known pixel
    input = CREATE 1x1 image
    SET pixel at (0,0) to RGB(100, 100, 100)
    
    // Act: Apply brightness +50
    result = ImageProcessor.adjustBrightness(input, 50)
    
    // Assert: Verify pixel is brighter
    resultColor = GET pixel at (0,0) from result
    ASSERT resultColor.red equals 150
    ASSERT resultColor.green equals 150
    ASSERT resultColor.blue equals 150
END TEST

TEST testBrightnessClampingAt255:
    input = CREATE 1x1 image
    SET pixel at (0,0) to RGB(230, 230, 230)
    
    result = ImageProcessor.adjustBrightness(input, 50)
    
    resultColor = GET pixel at (0,0) from result
    ASSERT resultColor.red equals 255  // Clamped at maximum
END TEST
```

**Step 2: Implement to Pass**
```pseudocode
FUNCTION adjustBrightness(inputImage, brightnessValue):
    resultImage = CREATE new image with same dimensions as input
    
    FOR y from 0 to image height:
        FOR x from 0 to image width:
            pixel = GET pixel at (x, y) from inputImage
            
            newRed = CLAMP(pixel.red + brightnessValue, 0, 255)
            newGreen = CLAMP(pixel.green + brightnessValue, 0, 255)
            newBlue = CLAMP(pixel.blue + brightnessValue, 0, 255)
            
            SET pixel at (x, y) in resultImage to RGB(newRed, newGreen, newBlue)
        END FOR
    END FOR
    
    RETURN resultImage
END FUNCTION
```

#### Example 2: Testing Flood Fill Algorithm

```pseudocode
TEST testFillConnectedRegion:
    // Create 3x3 white image with black center
    image = CREATE 3x3 image
    FOR y from 0 to 2:
        FOR x from 0 to 2:
            SET pixel(x, y) to WHITE
        END FOR
    END FOR
    SET pixel(1, 1) to BLACK
    
    // Fill from corner with red
    FloodFill.fill(image, startX=0, startY=0, fillColor=RED, tolerance=0)
    
    // Assert: All white pixels become red, black pixel unchanged
    ASSERT pixel(0, 0) equals RED
    ASSERT pixel(2, 2) equals RED
    ASSERT pixel(1, 1) equals BLACK  // Barrier not filled
END TEST

TEST testFillWithTolerance:
    image = CREATE 2x1 image
    SET pixel(0, 0) to RGB(100, 100, 100)
    SET pixel(1, 0) to RGB(110, 110, 110)  // Similar but not exact
    
    // Fill with 15% tolerance (should fill both similar colors)
    FloodFill.fill(image, 0, 0, RED, tolerance=15)
    
    ASSERT pixel(0, 0) equals RED
    ASSERT pixel(1, 0) equals RED  // Filled due to tolerance
END TEST
```

#### Example 3: Testing Command Pattern with Mocking

```pseudocode
TEST testExecuteAndUndo:
    // Arrange
    originalImage = CREATE 10x10 blank image
    modifiedImage = CREATE 10x10 blank image
    SET pixel(5, 5) in modifiedImage to RED
    
    imageModel = CREATE mock Image object
    MOCK imageModel.getData() to return originalImage
    
    command = CREATE DrawCommand(imageModel, modifiedImage)
    
    // Act: Execute
    command.execute()
    VERIFY imageModel.setData was called with modifiedImage
    
    // Act: Undo
    command.undo()
    VERIFY imageModel.setData was called with originalImage
END TEST

TEST testCommandHistoryUndoRedo:
    history = CREATE CommandHistory with limit 10
    cmd1 = CREATE mock Command
    cmd2 = CREATE mock Command
    
    history.executeCommand(cmd1)
    history.executeCommand(cmd2)
    
    history.undo()
    VERIFY cmd2.undo() was called
    
    history.redo()
    VERIFY cmd2.execute() was called
END TEST
```

#### Example 4: E2E Test (UI Automation)

```pseudocode
TEST testCompleteDrawingWorkflow:
    // Setup
    LAUNCH PixelCraft application
    
    // Create new image
    CLICK on menu "File" -> "New"
    TYPE "100" into width field
    TYPE "100" into height field
    CLICK "Create" button
    
    // Select pencil tool
    CLICK on pencil tool button
    
    // Draw a line
    DRAG mouse from (10, 10) to (50, 50)
    
    // Verify canvas is not empty
    canvas = FIND canvas element
    ASSERT canvas is NOT empty
    
    // Test undo
    PRESS Ctrl+Z
    
    // Verify canvas is empty again
    ASSERT canvas is empty
END TEST
    
    @Test
    public void testSaveAndLoadCycle(FxRobot robot) throws IOException {
        // Draw something
        robot.clickOn("#pencilTool");
        robot.drag(20, 20).dropTo(80, 80);
        
        // Save
        Path tempFile = Files.createTempFile("pixelcraft-test", ".png");
        robot.clickOn("File").clickOn("Save As");
        // ... file chooser interaction ...
        
        // Clear canvas
        CLICK on menu "File" -> "New"
        
        // Load
        CLICK on menu "File" -> "Open"
        // ... file chooser interaction ...
        
        // Verify canvas has drawing
        canvas = FIND canvas element
        ASSERT canvas is NOT empty
        
        DELETE temporary file
    END TEST
    
    HELPER METHOD isCanvasEmpty(canvas):
        // Check if canvas has any non-background pixels
        FOR each pixel in canvas:
            IF pixel is not background color THEN
                RETURN false
            END IF
        END FOR
        RETURN true
    END METHOD
END TEST SUITE
```

#### Example 5: Test Fixtures and Helpers

```pseudocode
CLASS TestFixtures:
    
    FUNCTION createSolidColorImage(width, height, color):
        image = CREATE new image (width x height)
        FILL entire image with color
        RETURN image
    END FUNCTION
    
    FUNCTION createCheckerboard(size, color1, color2):
        image = CREATE new image (size x size)
        FOR y from 0 to size:
            FOR x from 0 to size:
                IF (x + y) is even THEN
                    color = color1
                ELSE
                    color = color2
                END IF
                SET pixel(x, y) to color
            END FOR
        END FOR
        RETURN image
    END FUNCTION
    
    FUNCTION assertImagesEqual(expectedImage, actualImage):
        ASSERT expectedImage.width equals actualImage.width
        ASSERT expectedImage.height equals actualImage.height
        
        FOR y from 0 to height:
            FOR x from 0 to width:
                expectedPixel = GET pixel(x, y) from expectedImage
                actualPixel = GET pixel(x, y) from actualImage
                ASSERT expectedPixel equals actualPixel
                    WITH message "Pixel mismatch at (x, y)"
            END FOR
        END FOR
    END FUNCTION
END CLASS
```

---

## IV.5. Expected Java Project Structure

This section outlines the recommended folder and file organization for the PixelCraft project. Follow this structure to ensure clean, maintainable, professional-grade code.

### Project Root Directory Layout

```
PixelCraft/
├── pom.xml                          # Maven build configuration
├── README.md                        # Project documentation
├── .gitignore                       # Git ignore rules
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/pixelcraft/
│   │   │       ├── PixelCraft.java              # Main application entry point
│   │   │       ├── ui/
│   │   │       │   ├── MainWindow.java          # Main JavaFX window (uses Stage/Scene)
│   │   │       │   ├── CanvasPanel.java         # Image drawing canvas (uses JavaFX Canvas)
│   │   │       │   ├── ToolboxPanel.java        # Toolbox UI component
│   │   │       │   ├── LayersPanel.java         # Layers and history panel
│   │   │       │   ├── StatusBar.java           # Status bar component
│   │   │       │   ├── MenuBar.java             # Menu bar setup
│   │   │       │   └── dialogs/
│   │   │       │       ├── BrightnessContrastDialog.java
│   │   │       │       ├── ColorPickerDialog.java
│   │   │       │       ├── ResizeDialog.java
│   │   │       │       └── PreferencesDialog.java
│   │   │       ├── model/
│   │   │       │   ├── Image.java               # Wrapper for BufferedImage
│   │   │       │   ├── Layer.java               # Layer data structure
│   │   │       │   ├── LayerStack.java          # Layer management
│   │   │       │   └── ColorModel.java          # Color representation
│   │   │       ├── tools/
│   │   │       │   ├── Tool.java                # Abstract base class for all tools
│   │   │       │   ├── PencilTool.java          # Drawing tool
│   │   │       │   ├── EraserTool.java          # Eraser tool
│   │   │       │   ├── ColorPickerTool.java     # Eyedropper tool
│   │   │       │   ├── FillTool.java            # Bucket fill tool
│   │   │       │   ├── LineTool.java            # Line drawing tool
│   │   │       │   ├── RectangleTool.java       # Rectangle shape tool
│   │   │       │   ├── EllipseTool.java         # Ellipse shape tool
│   │   │       │   ├── SelectionTool.java       # Selection tool
│   │   │       │   ├── CloneStampTool.java      # Clone stamp tool
│   │   │       │   └── ToolManager.java         # Tool management and switching
│   │   │       ├── commands/
│   │   │       │   ├── Command.java             # Abstract command (undo/redo)
│   │   │       │   ├── DrawCommand.java         # Drawing command
│   │   │       │   ├── RotateCommand.java       # Rotate image command
│   │   │       │   ├── FlipCommand.java         # Flip image command
│   │   │       │   ├── BrightnessCommand.java   # Brightness adjustment command
│   │   │       │   ├── GrayscaleCommand.java    # Grayscale conversion command
│   │   │       │   ├── FilterCommand.java       # Filter application command
│   │   │       │   └── CommandHistory.java      # Undo/redo stack management
│   │   │       ├── processor/
│   │   │       │   ├── ImageProcessor.java      # Image manipulation utilities
│   │   │       │   ├── PixelOperations.java     # Pixel-level operations
│   │   │       │   ├── ColorOperations.java     # Color manipulation
│   │   │       │   ├── TransformOperations.java # Rotate, flip, resize
│   │   │       │   └── filters/
│   │   │       │       ├── Filter.java          # Abstract filter class
│   │   │       │       ├── BlurFilter.java      # Gaussian blur
│   │   │       │       ├── SharpenFilter.java   # Sharpen filter
│   │   │       │       ├── EdgeDetectionFilter.java
│   │   │       │       ├── SepiaFilter.java     # Sepia tone
│   │   │       │       └── FilterFactory.java   # Filter creation
│   │   │       ├── algorithms/
│   │   │       │   ├── FloodFill.java           # Flood fill algorithm
│   │   │       │   ├── Bresenham.java           # Line drawing algorithm
│   │   │       │   ├── Ellipse.java             # Ellipse drawing algorithm
│   │   │       │   └── BitmapFont.java          # Text rendering
│   │   │       ├── file/
│   │   │       │   ├── FileManager.java         # File I/O operations
│   │   │       │   ├── ImageLoader.java         # Image loading using ImageIO
│   │   │       │   ├── ImageSaver.java          # Image saving using ImageIO
│   │   │       │   └── RecentFiles.java         # Recent files tracking
│   │   │       ├── util/
│   │   │       │   ├── Constants.java           # Application constants
│   │   │       │   ├── AppConfig.java           # Configuration management
│   │   │       │   ├── Logger.java              # Custom logging utility
│   │   │       │   └── GeometryUtils.java       # Geometry calculations
│   │   │       └── event/
│   │   │           ├── PixelCraftListener.java  # Custom event interface
│   │   │           ├── ToolChangeListener.java  # Tool change events
│   │   │           └── ImageChangeListener.java # Image change events
│   │   └── resources/
│   │       ├── icons/
│   │       │   ├── pencil.png
│   │       │   ├── eraser.png
│   │       │   ├── bucket.png
│   │       │   ├── color_picker.png
│   │       │   ├── line.png
│   │       │   ├── rectangle.png
│   │       │   ├── ellipse.png
│   │       │   ├── selection.png
│   │       │   ├── clone.png
│   │       │   ├── rotate_cw.png
│   │       │   ├── rotate_ccw.png
│   │       │   ├── flip_h.png
│   │       │   ├── flip_v.png
│   │       │   ├── save.png
│   │       │   ├── open.png
│   │       │   ├── undo.png
│   │       │   └── redo.png
│   │       ├── config/
│   │       │   ├── application.properties      # App configuration
│   │       │   └── keyboard_shortcuts.properties
│   │       ├── sample_images/
│   │       │   ├── sample1.png
│   │       │   ├── sample2.jpg
│   │       │   └── sample3.bmp
│   │       └── splash.png                       # Splash screen image
│   └── test/
│       └── java/
│           └── com/pixelcraft/
│               ├── ImageProcessorTest.java
│               ├── PixelOperationsTest.java
│               ├── ColorOperationsTest.java
│               ├── FloodFillTest.java
│               ├── BresenhamTest.java
│               ├── ImageLoaderTest.java
│               ├── CommandHistoryTest.java
│               └── integration/
│                   ├── ToolIntegrationTest.java
│                   └── UIIntegrationTest.java
└── docs/
    ├── API.md                       # API documentation
    ├── ARCHITECTURE.md              # Architecture overview
    ├── CODING_STANDARDS.md          # Code style guide
    ├── SETUP.md                     # Setup and build instructions
    └── SCREENSHOTS/                 # Application screenshots
        ├── main_window.png
        ├── tools_demo.png
        └── filters_demo.png
```

### Maven pom.xml Configuration Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.pixelcraft</groupId>
    <artifactId>pixelcraft</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>PixelCraft Image Editor</name>
    <description>A feature-rich image editor built with JavaFX</description>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- JUnit 5 for Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.9.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.9.2</version>
            <scope>test</scope>
        </dependency>

        <!-- TestFX for JavaFX GUI Testing (example dependency) -->
        <dependency>
          <groupId>org.testfx</groupId>
          <artifactId>testfx-junit5</artifactId>
          <version>4.0.16-alpha</version>
          <scope>test</scope>
        </dependency>

        <!-- SLF4J for Logging (optional) -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.5</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.5</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                </configuration>
            </plugin>

            <!-- JAR Plugin for Executable JAR -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.pixelcraft.PixelCraft</mainClass>
                            <addClasspath>true</addClasspath>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>

            <!-- Surefire Plugin for Testing -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0</version>
            </plugin>
        </plugins>

        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
            </resource>
        </resources>
    </build>
</project>
```

### Key Java Class Examples

#### 1. Main Application Entry Point

```pseudocode
// PixelCraft - Main Application

CLASS PixelCraft EXTENDS Application:
    
    METHOD start(primaryStage):
        // Initialize and show main window
        mainWindow = CREATE new MainWindow(primaryStage)
        SHOW primaryStage
    END METHOD
    
    MAIN METHOD:
        LAUNCH application
    END MAIN
END CLASS
```

#### 2. Image Model

```pseudocode
// Image - Wrapper class for image data

CLASS Image:
    PROPERTIES:
        data: BufferedImage (pixel data)
        filepath: string
        modified: boolean
    
    CONSTRUCTOR(width, height):
        this.data = CREATE new BufferedImage(width, height, ARGB format)
        this.modified = false
    END CONSTRUCTOR
    
    METHOD getPixel(x, y):
        RETURN data.getRGB(x, y)
    END METHOD
    
    METHOD setPixel(x, y, color):
        data.setRGB(x, y, color)
        modified = true
    END METHOD
    
    METHOD getData():
        RETURN data
    END METHOD
    
    METHOD getWidth():
        RETURN data.getWidth()
    END METHOD
    
    METHOD getHeight():
        RETURN data.getHeight()
    END METHOD
END CLASS
```

#### 3. Abstract Tool Base Class

```pseudocode
// Tool - Abstract base class for all drawing tools

ABSTRACT CLASS Tool:
    PROPERTIES:
        image: Image
        name: string
        size: integer = 5
        color: integer = BLACK
    
    CONSTRUCTOR(name, image):
        this.name = name
        this.image = image
    END CONSTRUCTOR
    
    ABSTRACT METHODS:
        onMousePressed(event)
        onMouseDragged(event)
        onMouseReleased(event)
    
    METHOD setSize(size):
        this.size = CLAMP(size, 1, 50)
    END METHOD
    
    METHOD setColor(color):
        this.color = color
    END METHOD
    
    METHOD getName():
        RETURN name
    END METHOD
END CLASS
```

#### 4. Pencil Tool Implementation

```pseudocode
// PencilTool - Tool for freehand drawing

CLASS PencilTool EXTENDS Tool:
    PROPERTIES:
        lastX, lastY: integers
    
    CONSTRUCTOR(image):
        CALL parent constructor("Pencil", image)
    END CONSTRUCTOR
    
    METHOD onMousePressed(event):
        lastX = event.getX()
        lastY = event.getY()
        drawPixel(lastX, lastY)
    END METHOD
    
    METHOD onMouseDragged(event):
        x = event.getX()
        y = event.getY()
        drawLine(lastX, lastY, x, y)  // Bresenham's algorithm
        lastX = x
        lastY = y
    END METHOD
    
    METHOD onMouseReleased(event):
        // Finalize drawing operation
    END METHOD
    
    PRIVATE METHOD drawPixel(x, y):
        IF isInBounds(x, y) THEN
            image.setPixel(x, y, color)
        END IF
    END METHOD
    
    PRIVATE METHOD drawLine(x1, y1, x2, y2):
        // Use Bresenham's line algorithm
        // Draw pixels along line from (x1,y1) to (x2,y2)
    END METHOD
    
    PRIVATE METHOD isInBounds(x, y):
        RETURN x >= 0 AND x < image.width AND
               y >= 0 AND y < image.height
    END METHOD
END CLASS
```

#### 5. Command Pattern for Undo/Redo

```pseudocode
// Command - Interface for undo/redo functionality

INTERFACE Command:
    METHODS:
        execute()
        undo()
        getDescription() -> string
END INTERFACE

// Example: BrightnessCommand
CLASS BrightnessCommand IMPLEMENTS Command:
    PROPERTIES:
        image: Image
        backup: Image
        brightness: integer
    
    CONSTRUCTOR(image, brightness):
        this.image = image
        this.brightness = brightness
        this.backup = CREATE copy of image
    END CONSTRUCTOR
    
    METHOD execute():
        // Save backup of current state
        backup = COPY image
        // Apply brightness adjustment to image
        FOR each pixel in image:
            ADJUST pixel brightness by brightness value
        END FOR
    END METHOD
    
    METHOD undo():
        // Restore image from backup
        image = RESTORE from backup
    END METHOD
    
    METHOD getDescription():
        IF brightness > 0 THEN
            RETURN "Brightness +" + brightness
        ELSE
            RETURN "Brightness " + brightness
        END IF
    END METHOD
END CLASS
```

#### 6. Image Processor Utilities

```pseudocode
// ImageProcessor - Utility class for image operations

CLASS ImageProcessor:
    
    STATIC METHOD grayscale(image):
        result = CREATE new Image(image.width, image.height)
        
        FOR y from 0 to image.height:
            FOR x from 0 to image.width:
                rgb = image.getPixel(x, y)
                gray = toGrayscale(rgb)
                result.setPixel(x, y, gray)
            END FOR
        END FOR
        
        RETURN result
    END METHOD
    
    PRIVATE STATIC METHOD toGrayscale(rgb):
        // Extract color channels
        red = EXTRACT red channel from rgb
        green = EXTRACT green channel from rgb
        blue = EXTRACT blue channel from rgb
        
        // Apply luminosity formula
        gray = (0.299 * red) + (0.587 * green) + (0.114 * blue)
        
        // Combine into grayscale RGB value
        RETURN CREATE rgb with (gray, gray, gray)
    END METHOD
    
    STATIC METHOD adjustBrightness(image, brightness):
        // Adjust brightness of each pixel
        // See brightness algorithm in Milestone 6
    END METHOD
    
    STATIC METHOD rotateCW(image):
        // Rotate image 90 degrees clockwise
        // Transpose and flip
    }
}
```

### Directory Creation Guide

To create this structure, use these commands:

```bash
# Create main package structure
mkdir -p src/main/java/com/pixelcraft/{ui,model,tools,commands,processor,file,util,event,algorithms}
mkdir -p src/main/java/com/pixelcraft/{ui/dialogs,processor/filters}
mkdir -p src/main/resources/{icons,config,sample_images}
mkdir -p src/test/java/com/pixelcraft/integration
mkdir -p docs

# Create resources
touch src/main/resources/icons/{pencil,eraser,bucket}.png
touch src/main/resources/config/application.properties
```

### Naming Conventions

Follow these Java naming conventions throughout the project:

- **Packages:** lowercase, reverse domain style (e.g., `com.pixelcraft.ui`)
- **Classes:** PascalCase (e.g., `PencilTool.java`)
- **Methods:** camelCase (e.g., `drawPixel()`)
- **Variables:** camelCase (e.g., `brushSize`)
- **Constants:** UPPER_SNAKE_CASE (e.g., `MAX_BRUSH_SIZE`)
- **Interfaces:** PascalCase prefixed with 'I' or suffix 'able' (e.g., `IDrawable`, `Drawable`)

---

# 🛠️ Developer Resources

### Icon Libraries (Free and Open Source)

Enhance your UI with professional-looking icons for tools and menus.

#### General Purpose Icon Sets:
- **Material Design Icons** - https://fonts.google.com/icons
  - 4000+ free icons by Google
  - Multiple sizes and styles (outline, filled, rounded, sharp)
  - Available as SVG, PNG, and font files
  - Perfect for modern UI design

- **Font Awesome** - https://fontawesome.com/
  - 7000+ icons (free version)
  - Professional quality icons widely used in web design
  - Available as SVG, PNG, or web fonts
  - Great for drawing tools and UI elements

- **Feather Icons** - https://feathericons.com/
  - Minimalist 24x24 icons
  - Clean, simple design
  - 286 icons in SVG format
  - Lightweight and responsive

- **Tabler Icons** - https://tabler-icons.io/
  - 4000+ icons perfect for applications
  - SVG format with customizable stroke width
  - Design-focused icon set

#### Specialized Icon Sets:
- **Game Icons** - https://game-icons.net/
  - 3000+ game-style icons (drawing, tools, effects)
  - SVG format with customizable colors
  - Perfect for creative applications

- **Simple Icons** - https://simpleicons.org/
  - 3000+ brand icons
  - Useful for showing supported file formats

### Image Resources and Stock Photos

Use sample images for testing and documentation.

- **Unsplash** - https://unsplash.com/
  - Free high-quality images
  - No attribution required
  - Great for testing with various image types

- **Pexels** - https://www.pexels.com/
  - Free stock photos
  - High resolution
  - Good for demo images

- **Pixabay** - https://pixabay.com/
  - Free images and vectors
  - Large collection
  - No copyright restrictions

- **Lorem Picsum** - https://picsum.photos/
  - Placeholder images with customizable dimensions
  - Great for automated testing
  - Example: `https://picsum.photos/800/600`

### UI/UX Design Tools

Plan and prototype your interface before coding.

#### Free Design Tools:
- **Figma** - https://www.figma.com/
  - Collaborative design platform
  - Free tier with 3 projects
  - Perfect for UI/UX mockups
  - Component library support

- **Penpot** - https://penpot.app/
  - Open-source design tool
  - Similar to Figma
  - Self-hosted option available

- **Draw.io** - https://www.draw.io/
  - Free diagramming tool
  - Great for UI wireframes and architecture diagrams
  - Desktop and web versions

### Color Palette Generators

Create professional color schemes for your application.

- **Coolors.co** - https://coolors.co/
  - Generate color palettes
  - Lock colors and explore variations
  - Export in multiple formats

- **Adobe Color** - https://color.adobe.com/
  - Create and browse color palettes
  - Explore color harmonies

- **Paletton** - https://paletton.com/
  - Advanced color scheme generator
  - Shows how colors work together

- **Dribble** - https://dribbble.com/
  - Browse design inspiration
  - Find color schemes used by professionals

### Image Processing Libraries

Pre-built libraries for image manipulation tasks.

#### Java Libraries (For This Project):

**Built-in Standard Libraries (No Installation Required):**
- **Java ImageIO** - `javax.imageio`
  - Built-in Java image loading/saving
  - Supports PNG, JPG, BMP, GIF formats
  - Part of Java standard library
  - Official Documentation: https://docs.oracle.com/javase/8/docs/technotes/guides/imageio/

- **BufferedImage** - `java.awt.image.BufferedImage`
  - In-memory image representation
  - Direct pixel access with `getRGB()` and `setRGB()`
  - Official Documentation: https://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferedImage.html

- **Java 2D Graphics** - `java.awt.Graphics2D`
  - Drawing operations and rendering
  - Transformations and filters
  - Official Documentation: https://docs.oracle.com/javase/tutorial/2d/

**Optional Advanced Java Libraries (For Optimization):**
- **ImageJ** - https://imagej.net/
  - Open-source image processing library written in Java
  - Extensive filters and algorithms
  - Good for performance optimization

- **OpenCV (Java Binding)** - https://opencv.org/
  - Professional computer vision library
  - Java bindings available
  - Advanced image processing algorithms
  - Maven dependency: `org.opencv:opencv-java`

- **TwelveMonkeys ImageIO** - https://github.com/haraldk/TwelveMonkeys
  - Extended image I/O support for Java
  - Supports more formats than standard Java
  - Maven dependency available

### UI Component Libraries

Pre-built UI components to accelerate development.

#### Java:
- **JFoenix (Material Design for JavaFX)** - https://github.com/jfoenixadmin/JFoenix
  - Material Design components for JavaFX

- **FXMLLoader (JavaFX)** - https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/FXMLLoader.html
  - Load UI from XML definitions

- **Apache Pivot** - https://pivot.apache.org/
  - Java framework for rich UI applications

### Documentation and Learning Resources

#### Java-Specific Tutorials:
- **JavaFX Tutorial (OpenJFX)** - https://openjfx.io/
  - Official JavaFX documentation and guides
  - Tutorials, samples and deployment guides

- **Java 2D Graphics Tutorial** - https://docs.oracle.com/javase/tutorial/2d/
  - Graphics rendering and transformation
  - Essential for drawing and image manipulation

- **Java 2D Graphics Tutorial** - https://docs.oracle.com/javase/tutorial/2d/
  - Graphics rendering and transformation
  - Essential for drawing and image manipulation

- **Image Processing 101** - https://docs.opencv.org/master/d9/df8/tutorial_root.html
  - General image processing concepts (applicable to Java)

- **Khan Academy - Pixels and Color** - https://www.khanacademy.org/computing/pixar/effect-virtual-cinematography/virtual-cinematography/a/color
  - Educational resource on digital images

#### Java Algorithm Resources:
- **GeeksforGeeks - Image Processing** - https://www.geeksforgeeks.org/image-processing/
  - Detailed explanations of image algorithms (implement in Java)

- **Algorithm Visualizer** - https://algorithm-visualizer.org/
  - Visual learning of algorithms (flood-fill, line drawing, etc.)

- **Java Concurrency** - https://docs.oracle.com/javase/tutorial/essential/concurrency/
  - For multi-threaded filter operations

#### Design and UX:
- **Nielsen Norman Group** - https://www.nngroup.com/
  - UX research and guidelines
  - Free articles on usability

- **Google Design** - https://design.google/
  - Google's design principles and resources
  - Material Design system

- **Interaction Design Foundation** - https://www.interaction-design.org/
  - Free courses on UX/UI design

### Version Control and Collaboration

- **GitHub** - https://github.com/
  - Host and share your Java project repository
  - Collaborative development

### Recommended Tools for Java Development

**IDE (Integrated Development Environment) - REQUIRED:**
- **Visual Studio Code** - https://code.visualstudio.com/
  - Lightweight yet powerful code editor
  - **Extension Pack for Java** (REQUIRED) - includes:
    - Language Support for Java by Red Hat
    - Debugger for Java
    - Test Runner for Java
    - Maven for Java
    - Project Manager for Java
    - Visual Studio IntelliCode
  - Download: https://code.visualstudio.com/
  - Install Extensions: Search for "Extension Pack for Java" in VS Code marketplace
  - Excellent for this project with great JavaFX support

**Alternative IDEs** (if you prefer):
- **IntelliJ IDEA Community Edition** - https://www.jetbrains.com/idea/
  - Full-featured Java IDE with built-in JavaFX support

**Build Tools:**
- **Apache Maven** - https://maven.apache.org/
  - Required for this project; use `pom.xml` to manage dependencies (including OpenJFX)
  - Tutorial: https://maven.apache.org/guides/getting-started/

**Development Assistance:**
- **GitHub Copilot** - https://github.com/features/copilot
  - AI-powered code completion
  - Excellent for Java syntax and algorithm implementation
  - Available in most IDEs

- **JUnit 5** - https://junit.org/junit5/
  - Testing framework for Java
  - Essential for unit testing your code
  - Maven: `<dependency>org.junit.jupiter:junit-jupiter</dependency>`

-- **TestFX** - https://github.com/TestFX/TestFX
  - GUI testing toolkit for JavaFX applications
  - Maven: `<dependency>org.testfx:testfx-junit5</dependency>`

**Performance and Debugging:**
- **JProfiler** - https://www.ej-technologies.com/products/jprofiler/overview.html
  - Professional profiling tool (Commercial, but free trial available)
  - Excellent for performance optimization

- **Java Mission Control** - Built into Java
  - Profiling and monitoring tool included with JDK
  - No installation required

- **Visual Studio Code** - https://code.visualstudio.com/
  - Lightweight code editor
  - Great extensions for Python, JavaScript, Java

- **ImageMagick** - https://imagemagick.org/
  - Command-line tool for image conversion
  - Useful for batch processing test images

### Example Code and Tutorials

- **Tutorials Point - Image Processing** - https://www.tutorialspoint.com/dip/
  - Introduction to digital image processing

- **OpenCV Tutorials** - https://docs.opencv.org/master/d9/df8/tutorial_root.html
  - Comprehensive guide with code examples

- **JavaFX Documentation** - https://openjfx.io/
  - Official JavaFX documentation with examples
