# Changelog

All notable changes to PixelCraft will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial project structure and Maven configuration
- JavaFX-based user interface with main window
- Canvas rendering system with checkerboard background
- Image loading and basic display functionality
- Zoom operations (Zoom In, Zoom Out, Reset, Fit to Viewport, Custom Zoom)
- Viewport management with ScrollPane integration
- Command Pattern implementation for undo/redo operations
- Command History system with undo/redo support
- Recent files management and menu
- Keyboard shortcuts system
- Status bar with image information and zoom level display
- File management system for image loading
- Basic RasterImage model for pixel manipulation
- Test infrastructure with JUnit 5, TestFX, and Mockito
- E2E and integration tests for zoom commands
- Comprehensive project documentation and requirements

### Known Issues
- Save and SaveAs functionality not yet implemented
- New Image dialog needs implementation
- Unsaved changes detection not implemented
- Drawing tools not yet implemented
- Image filters not yet implemented
- Layer system not yet implemented

## [1.0-SNAPSHOT] - 2025-12-22

### Added
- Initial release
- Basic project scaffolding
- Core architecture with Manager pattern
- Command Pattern for operations
- JavaFX UI foundation
