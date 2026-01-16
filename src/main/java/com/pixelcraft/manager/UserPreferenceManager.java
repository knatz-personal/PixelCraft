package com.pixelcraft.manager;

import java.io.File;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Centralized service for managing user preferences with persistence.
 * <p>
 * This class provides a clean API for storing and retrieving user preferences
 * such as last used directories, window positions, and application settings.
 * Preferences are automatically persisted using the Java Preferences API.
 * </p>
 * <p>
 * Using dependency injection of this service instead of static fields improves:
 * <ul>
 *   <li>Testability - preferences can be mocked in unit tests</li>
 *   <li>Single Responsibility - commands don't manage their own persistence</li>
 *   <li>Consistency - all preferences in one place</li>
 * </ul>
 * </p>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * UserPreferenceManager prefs = new UserPreferenceManager();
 * prefs.setLastOpenDirectory(new File("/path/to/dir"));
 * Optional<File> lastDir = prefs.getLastOpenDirectory();
 * }</pre>
 * 
 * @see java.util.prefs.Preferences
 */
public final class UserPreferenceManager {

    //#region Constants
    private static final String KEY_LAST_OPEN_DIRECTORY = "lastOpenDirectory";
    private static final String KEY_LAST_SAVE_DIRECTORY = "lastSaveDirectory";
    private static final String KEY_WINDOW_WIDTH = "windowWidth";
    private static final String KEY_WINDOW_HEIGHT = "windowHeight";
    private static final String KEY_WINDOW_X = "windowX";
    private static final String KEY_WINDOW_Y = "windowY";
    private static final String KEY_PIXEL_ART_MODE = "pixelArtMode";
    //#endregion

    private final Preferences preferences;

    /**
     * Constructs a new UserPreferenceManager instance using the default preferences node.
     */
    public UserPreferenceManager() {
        this.preferences = Preferences.userNodeForPackage(UserPreferenceManager.class);
    }

    /**
     * Constructs a new UserPreferenceManager instance with a custom preferences node.
     * Useful for testing or using a different preferences namespace.
     *
     * @param preferences the Preferences node to use for storage
     */
    public UserPreferenceManager(Preferences preferences) {
        this.preferences = preferences;
    }

    //#region Directory Preferences

    /**
     * Gets the last directory used for opening files.
     *
     * @return an Optional containing the last open directory if it exists and is valid,
     *         or empty if not set or the directory no longer exists
     */
    public Optional<File> getLastOpenDirectory() {
        String path = preferences.get(KEY_LAST_OPEN_DIRECTORY, null);
        if (path == null || path.isEmpty()) {
            return Optional.empty();
        }
        File dir = new File(path);
        return dir.isDirectory() ? Optional.of(dir) : Optional.empty();
    }

    /**
     * Sets the last directory used for opening files.
     *
     * @param directory the directory to remember; if null, the preference is cleared
     */
    public void setLastOpenDirectory(File directory) {
        if (directory == null) {
            preferences.remove(KEY_LAST_OPEN_DIRECTORY);
        } else {
            preferences.put(KEY_LAST_OPEN_DIRECTORY, directory.getAbsolutePath());
        }
    }

    /**
     * Gets the last directory used for saving files.
     *
     * @return an Optional containing the last save directory if it exists and is valid,
     *         or empty if not set or the directory no longer exists
     */
    public Optional<File> getLastSaveDirectory() {
        String path = preferences.get(KEY_LAST_SAVE_DIRECTORY, null);
        if (path == null || path.isEmpty()) {
            return Optional.empty();
        }
        File dir = new File(path);
        return dir.isDirectory() ? Optional.of(dir) : Optional.empty();
    }

    /**
     * Sets the last directory used for saving files.
     *
     * @param directory the directory to remember; if null, the preference is cleared
     */
    public void setLastSaveDirectory(File directory) {
        if (directory == null) {
            preferences.remove(KEY_LAST_SAVE_DIRECTORY);
        } else {
            preferences.put(KEY_LAST_SAVE_DIRECTORY, directory.getAbsolutePath());
        }
    }

    //#endregion

    //#region Window Preferences

    /**
     * Gets the last saved window width.
     *
     * @param defaultValue the value to return if no preference is stored
     * @return the stored window width, or defaultValue if not set
     */
    public double getWindowWidth(double defaultValue) {
        return preferences.getDouble(KEY_WINDOW_WIDTH, defaultValue);
    }

    /**
     * Sets the window width preference.
     *
     * @param width the window width to store
     */
    public void setWindowWidth(double width) {
        preferences.putDouble(KEY_WINDOW_WIDTH, width);
    }

    /**
     * Gets the last saved window height.
     *
     * @param defaultValue the value to return if no preference is stored
     * @return the stored window height, or defaultValue if not set
     */
    public double getWindowHeight(double defaultValue) {
        return preferences.getDouble(KEY_WINDOW_HEIGHT, defaultValue);
    }

    /**
     * Sets the window height preference.
     *
     * @param height the window height to store
     */
    public void setWindowHeight(double height) {
        preferences.putDouble(KEY_WINDOW_HEIGHT, height);
    }

    /**
     * Gets the last saved window X position.
     *
     * @param defaultValue the value to return if no preference is stored
     * @return the stored window X position, or defaultValue if not set
     */
    public double getWindowX(double defaultValue) {
        return preferences.getDouble(KEY_WINDOW_X, defaultValue);
    }

    /**
     * Sets the window X position preference.
     *
     * @param x the window X position to store
     */
    public void setWindowX(double x) {
        preferences.putDouble(KEY_WINDOW_X, x);
    }

    /**
     * Gets the last saved window Y position.
     *
     * @param defaultValue the value to return if no preference is stored
     * @return the stored window Y position, or defaultValue if not set
     */
    public double getWindowY(double defaultValue) {
        return preferences.getDouble(KEY_WINDOW_Y, defaultValue);
    }

    /**
     * Sets the window Y position preference.
     *
     * @param y the window Y position to store
     */
    public void setWindowY(double y) {
        preferences.putDouble(KEY_WINDOW_Y, y);
    }

    //#endregion

    //#region Application Preferences

    /**
     * Gets the pixel art mode preference.
     *
     * @return true if pixel art mode is enabled, false otherwise
     */
    public boolean isPixelArtMode() {
        return preferences.getBoolean(KEY_PIXEL_ART_MODE, false);
    }

    /**
     * Sets the pixel art mode preference.
     *
     * @param enabled true to enable pixel art mode, false to disable
     */
    public void setPixelArtMode(boolean enabled) {
        preferences.putBoolean(KEY_PIXEL_ART_MODE, enabled);
    }

    //#endregion

    /**
     * Clears all stored preferences.
     * Use with caution - this removes all user settings.
     */
    public void clearAll() {
        try {
            preferences.clear();
        } catch (BackingStoreException e) {
            // Preferences API may throw BackingStoreException
        }
    }
}
