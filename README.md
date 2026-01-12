# PixelCraft Image Editor

A professional image editor built with JavaFX, featuring drawing tools, filters, layers, and a modern user interface.

## 📋 Overview

PixelCraft is a fully functional image editor designed to provide essential image manipulation capabilities. Built with Java 21 and JavaFX 13, it demonstrates modern software design patterns and test-driven development practices.

### ✨ Features

- **Drawing Tools**: Pencil, eraser, line, and shape tools
- **Image Processing**: Advanced filters including blur, edge detection, and convolution
- **Layer System**: Multi-layer support for complex editing
- **Undo/Redo**: Full command history with unlimited undo/redo
- **Zoom & Pan**: Smooth navigation for precise editing
- **File Management**: Support for common image formats (PNG, JPG, BMP)
- **Recent Files**: Quick access to recently opened images
- **Keyboard Shortcuts**: Efficient workflow with hotkeys

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+** for dependency management
- **Git** for version control

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd pixelcraft
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn javafx:run
```

## 🏗️ Project Structure

```
pixelcraft/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/pixelcraft/
│   │   │       ├── App.java              # Main application entry
│   │   │       ├── MainController.java    # Primary UI controller
│   │   │       ├── algorithms/            # Image processing algorithms
│   │   │       ├── commands/              # Command pattern implementation
│   │   │       ├── event/                 # Event handling
│   │   │       ├── file/                  # File I/O operations
│   │   │       ├── manager/               # Application managers
│   │   │       ├── model/                 # Data models
│   │   │       ├── processor/filters/     # Image filters
│   │   │       ├── tools/                 # Drawing tools
│   │   │       ├── ui/dialogs/            # UI dialogs
│   │   │       └── util/                  # Utility classes
│   │   └── resources/
│   │       ├── com/pixelcraft/main.fxml   # Main UI layout
│   │       ├── config/                    # Configuration files
│   │       └── icons/                     # Application icons
│   └── test/
│       └── java/
│           └── com/pixelcraft/            # Test suites
├── docs/
│   └── Requirements.md                     # Detailed development guide
├── pom.xml                                 # Maven configuration
└── README.md                               # This file
```

## 🛠️ Technology Stack

- **Language**: Java 21
- **UI Framework**: JavaFX 13
- **Build Tool**: Maven
- **Testing**: JUnit 5, TestFX, Mockito
- **Icons**: Ikonli FontAwesome

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify
```

## 📖 Development Guide

For a comprehensive step-by-step development guide, see [Requirements.md](docs/Requirements.md). The guide covers:

- Milestone-based development roadmap
- Test-Driven Development (TDD) approach
- Design patterns and architecture
- Performance optimization techniques
- Best practices for JavaFX applications

## 🎯 Key Design Patterns

- **Command Pattern**: Undo/redo functionality
- **Observer Pattern**: Event-driven UI updates
- **Factory Pattern**: Tool and filter creation
- **Strategy Pattern**: Pluggable image processing algorithms
- **Model-View-Controller (MVC)**: UI architecture

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Write tests for new functionality
4. Ensure all tests pass (`mvn test`)
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👤 Author

PixelCraft Image Editor

## 🙏 Acknowledgments

- JavaFX community for excellent documentation
- TestFX for UI testing capabilities
- Ikonli for icon integration

---

**Happy Editing! 🎨**
