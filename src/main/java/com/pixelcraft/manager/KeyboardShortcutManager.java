package com.pixelcraft.manager;

import com.pixelcraft.ui.MainController;

import javafx.scene.Scene;

/**
 * Utility class that installs keyboard shortcuts for common editing and view actions
 * into a JavaFX Scene. This class is not instantiable and exposes a single static
 * setup(...) method to register an event filter that maps keyboard combinations to
 * methods on a provided MainController.
 *
 * <p>Supported shortcuts (require Ctrl modifier):
 * <ul>
 *   <li>Ctrl + Plus / Ctrl + Add / Ctrl + Equals -> onZoomIn</li>
 *   <li>Ctrl + Minus / Ctrl + Subtract -> onZoomOut</li>
 *   <li>Ctrl + 0 / Ctrl + Numpad0 -> onZoomReset</li>
 *   <li>Ctrl + F -> onZoomFitWindow</li>
 *   <li>Ctrl + Z -> onUndo</li>
 *   <li>Ctrl + Shift + Z -> onRedo</li>
 *   <li>Ctrl + Y -> onRedo</li>
 * </ul>
 *
 * <p>The installed event filter consumes handled KeyEvent instances so they do not
 * propagate to other handlers. Keys not listed above are ignored by this manager.
 *
 * <p>Note: setup(...) must be called on the JavaFX Application Thread. The method
 * does not perform null checks on its arguments; callers should ensure that both
 * the Scene and controller are non-null.
 */
public final class KeyboardShortcutManager {
    
    private KeyboardShortcutManager() {
        // Keyboard Shortcut Manager must not be instantiated
    }

    /**
     * Registers a KeyEvent filter on the provided Scene to handle application keyboard
     * shortcuts by delegating to the given MainController.
     *
     * @param scene the JavaFX Scene to attach the keyboard event filter to; must be
     *              created on the JavaFX Application Thread
     * @param controller the controller whose action methods (e.g. onZoomIn, onUndo)
     *                   will be invoked when corresponding shortcuts are triggered
     */
    public static void setup(Scene scene, MainController controller) {
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (!event.isControlDown()) return;
            
            switch (event.getCode()) {
                case PLUS, ADD, EQUALS -> {
                    controller.onZoomIn(null);
                    event.consume();
                }
                case MINUS, SUBTRACT -> {
                    controller.onZoomOut(null);
                    event.consume();
                }
                case DIGIT0, NUMPAD0 -> {
                    controller.onZoomReset(null);
                    event.consume();
                }
                case F -> {
                    controller.onZoomFitWindow(null);
                    event.consume();
                }
                case Z -> {
                    if (event.isShiftDown()) {
                        controller.onRedo(null);
                    } else {
                        controller.onUndo(null);
                    }
                    event.consume();
                }
                case Y -> {
                    controller.onRedo(null);
                    event.consume();
                }
                default -> {
                    // Ignore other keys
                }
            }
        });
    }
}