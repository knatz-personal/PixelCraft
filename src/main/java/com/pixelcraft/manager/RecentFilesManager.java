package com.pixelcraft.manager;

import java.util.prefs.Preferences;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Manages a list of recently accessed files with persistence support.
 * 
 * <p>This class maintains an observable list of recent file paths that can be
 * bound to UI components. The list is automatically persisted to user preferences
 * and restored when a new instance is created.</p>
 * 
 * <p>Features include:</p>
 * <ul>
 *   <li>Automatic persistence using Java Preferences API</li>
 *   <li>Move-to-front behavior when adding duplicate entries</li>
 *   <li>Configurable maximum number of entries</li>
 *   <li>Observable list for JavaFX data binding</li>
 * </ul>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * RecentFilesManager manager = new RecentFilesManager(5);
 * manager.add("/path/to/file.txt");
 * ObservableList<String> recents = manager.getRecents();
 * }</pre>
 * 
 * @see java.util.prefs.Preferences
 * @see javafx.collections.ObservableList
 */
public final class RecentFilesManager {

    //#region Constants
    private static final String PREFERENCE_KEY = "recent_files";
    private static final String SAFE_SEP = "\u241E";
    private static final int DEFAULT_MAX = 10;
    //#endregion

    //#region fields
    private final Preferences preferences = Preferences.userNodeForPackage(RecentFilesManager.class);
    private final ObservableList<String> recent = FXCollections.observableArrayList();
    private int maxEntries = DEFAULT_MAX;
    //#endregion final fields

    //#region Getters and Setters
    public ObservableList<String> getRecents() {
        return recent;
    }
    //#endregion

    //#region Constructors
    /**
     * Constructs a new RecentFilesManager with the default maximum number of recent files.
     * This constructor delegates to the parameterized constructor using DEFAULT_MAX as the limit.
     */
    public RecentFilesManager() {
        this(DEFAULT_MAX);
    }

    /**
     * Constructs a new RecentFilesManager with the specified maximum number of entries.
     * If the provided maximum is less than 1, the default maximum value is used instead.
     * Upon construction, the manager loads any previously saved recent files.
     *
     * @param max the maximum number of recent file entries to maintain;
     *            if less than 1, DEFAULT_MAX will be used
     */
    public RecentFilesManager(int max) {
        maxEntries = max < 1 ? DEFAULT_MAX : max;
        load();
    }
    //#endregion

    //#region Methods
    /**
     * Adds a file path to the recent files list.
     * If the path is null or empty, no action is taken.
     * If the path already exists in the list, it is moved to the front.
     * The list is trimmed to maintain the maximum number of entries.
     * Changes are persisted automatically after modification.
     *
     * @param path the file path to add to the recent files list
     */
    public void add(String path) {
        if (path == null || path.isEmpty()) {
            return;
        }

        // move-to-front if already present
        recent.remove(path);
        recent.add(0, path);

        // restrict number of recents
        while (recent.size() > maxEntries) {
            recent.remove(recent.size() - 1);
        }
        
        // persist after change
        save();
    }

    /**
     * Checks if the recent files list is empty.
     *
     * @return {@code true} if there are no recent files in the list, {@code false} otherwise
     */
    public boolean isEmpty() {
        return recent.isEmpty();
    }

    /**
     * Saves the list of recent files to the user preferences.
     * The recent files are joined into a single string using a safe separator
     * and stored in the preferences under the designated preference key.
     */
    public void save() {
        String joined = String.join(SAFE_SEP, recent);
        preferences.put(PREFERENCE_KEY, joined);
    }

    /**
     * Loads the list of recent files from user preferences.
     * Retrieves the stored paths as a single joined string from preferences,
     * splits them using the safe separator, and populates the recent files collection.
     * If no recent files are stored, the collection remains unchanged.
     */
    public void load() {
        String joined = preferences.get(PREFERENCE_KEY, "");
        if (!joined.isEmpty()) {
            String[] paths = joined.split(SAFE_SEP);
            recent.setAll(paths);
        }
    }

    /**
     * Clears all recent files from the list and removes the stored preferences.
     * This method empties the in-memory recent files collection and removes
     * the associated data from the persistent preferences storage.
     */
    public void clear() {
        recent.clear();
        preferences.remove(SAFE_SEP);
    }
    //#endregion
}
