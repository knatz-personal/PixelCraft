package com.pixelcraft.manager;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RecentFilesManager {

    //#region Constants
    private static final Logger LOGGER = Logger.getLogger(RecentFilesManager.class.getName());
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

    //#region   Constructors
    public RecentFilesManager() {
        this(DEFAULT_MAX);
    }

    public RecentFilesManager(int max) {
        maxEntries = max < 1 ? DEFAULT_MAX : max;
        load();
    }
    //#endregion

    //#region Methods
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

    public boolean isEmpty() {
        return recent.isEmpty();
    }

    public void save() {
        String joined = String.join(SAFE_SEP, recent);
        preferences.put(PREFERENCE_KEY, joined);
    }

    public void load() {
        String joined = preferences.get(PREFERENCE_KEY, "");
        if (!joined.isEmpty()) {
            String[] paths = joined.split(SAFE_SEP);
            recent.setAll(paths);
        }
    }

    public void clear() {
        recent.clear();
        preferences.remove(SAFE_SEP);
    }
    //#endregion
}
