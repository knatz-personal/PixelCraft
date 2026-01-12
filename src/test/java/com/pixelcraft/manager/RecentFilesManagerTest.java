package com.pixelcraft.manager;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecentFilesManager
 * 
 * @author Nathan Khupe
 */
public class RecentFilesManagerTest {

    private RecentFilesManager manager;
    private Preferences testPrefs;

    @BeforeEach
    void setUp() {
        // Create a fresh manager for each test
        manager = new RecentFilesManager(5);
        testPrefs = Preferences.userNodeForPackage(RecentFilesManager.class);
    }

    @AfterEach
    void tearDown() {
        // Clean up preferences after each test
        try {
            testPrefs.clear();
            testPrefs.flush();
        } catch (BackingStoreException e) {
            // Ignore cleanup errors
        }
    }

    @Test
    @DisplayName("Constructor creates empty manager")
    void testConstructorCreatesEmptyManager() {
        RecentFilesManager newManager = new RecentFilesManager();
        assertNotNull(newManager.getRecents(), "Recent files list should not be null");
    }

    @Test
    @DisplayName("Constructor with max entries")
    void testConstructorWithMaxEntries() {
        RecentFilesManager customManager = new RecentFilesManager(3);
        assertNotNull(customManager.getRecents(), "Recent files list should not be null");
        assertTrue(customManager.isEmpty(), "New manager should be empty");
    }

    @Test
    @DisplayName("Add file to recent list")
    void testAddFileToRecentList() {
        String filePath = "C:/test/image.png";
        manager.add(filePath);
        
        assertFalse(manager.isEmpty(), "Manager should not be empty after adding file");
        assertEquals(1, manager.getRecents().size(), "Should have one file in recent list");
        assertEquals(filePath, manager.getRecents().get(0), "Added file should be first in list");
    }

    @Test
    @DisplayName("Add multiple files to recent list")
    void testAddMultipleFiles() {
        manager.add("C:/test/image1.png");
        manager.add("C:/test/image2.jpg");
        manager.add("C:/test/image3.bmp");
        
        assertEquals(3, manager.getRecents().size(), "Should have three files in recent list");
        assertEquals("C:/test/image3.bmp", manager.getRecents().get(0), "Most recent file should be first");
        assertEquals("C:/test/image2.jpg", manager.getRecents().get(1), "Second file should be at index 1");
        assertEquals("C:/test/image1.png", manager.getRecents().get(2), "First file should be last");
    }

    @Test
    @DisplayName("Add null file does nothing")
    void testAddNullFile() {
        manager.add(null);
        assertTrue(manager.isEmpty(), "Manager should remain empty after adding null");
    }

    @Test
    @DisplayName("Add empty string does nothing")
    void testAddEmptyString() {
        manager.add("");
        assertTrue(manager.isEmpty(), "Manager should remain empty after adding empty string");
    }

    @Test
    @DisplayName("Add duplicate file moves to front")
    void testAddDuplicateFileMovesToFront() {
        manager.add("C:/test/image1.png");
        manager.add("C:/test/image2.jpg");
        manager.add("C:/test/image3.bmp");
        manager.add("C:/test/image1.png"); // Re-add first file
        
        assertEquals(3, manager.getRecents().size(), "Should still have three files");
        assertEquals("C:/test/image1.png", manager.getRecents().get(0), "Re-added file should be first");
        assertEquals("C:/test/image3.bmp", manager.getRecents().get(1), "Previous first should be second");
    }

    @Test
    @DisplayName("Respect max entries limit")
    void testRespectMaxEntriesLimit() {
        RecentFilesManager limitedManager = new RecentFilesManager(3);
        
        limitedManager.add("C:/test/image1.png");
        limitedManager.add("C:/test/image2.jpg");
        limitedManager.add("C:/test/image3.bmp");
        limitedManager.add("C:/test/image4.gif");
        
        assertEquals(3, limitedManager.getRecents().size(), "Should not exceed max entries");
        assertEquals("C:/test/image4.gif", limitedManager.getRecents().get(0), "Newest file should be first");
        assertFalse(limitedManager.getRecents().contains("C:/test/image1.png"), "Oldest file should be removed");
    }

    @Test
    @DisplayName("IsEmpty returns true for new manager")
    void testIsEmptyReturnsTrueForNewManager() {
        // Clear any existing preferences first
        try {
            testPrefs.clear();
            testPrefs.flush();
        } catch (BackingStoreException e) {
            // Ignore
        }
        
        RecentFilesManager freshManager = new RecentFilesManager();
        assertTrue(freshManager.isEmpty(), "New manager should be empty");
    }

    @Test
    @DisplayName("IsEmpty returns false after adding file")
    void testIsEmptyReturnsFalseAfterAddingFile() {
        manager.add("C:/test/image.png");
        assertFalse(manager.isEmpty(), "Manager should not be empty after adding file");
    }

    @Test
    @DisplayName("Clear removes all entries")
    void testClearRemovesAllEntries() {
        manager.add("C:/test/image1.png");
        manager.add("C:/test/image2.jpg");
        manager.add("C:/test/image3.bmp");
        
        manager.clear();
        
        assertTrue(manager.isEmpty(), "Manager should be empty after clear");
        assertEquals(0, manager.getRecents().size(), "Recent list should have no entries");
    }

    @Test
    @DisplayName("Save and load persists data")
    void testSaveAndLoadPersistsData() {
        manager.add("C:/test/image1.png");
        manager.add("C:/test/image2.jpg");
        manager.save();
        
        // Create new manager to load saved data
        RecentFilesManager newManager = new RecentFilesManager(5);
        newManager.load();
        
        assertEquals(2, newManager.getRecents().size(), "Loaded manager should have two files");
        assertEquals("C:/test/image2.jpg", newManager.getRecents().get(0), "Most recent file should be first");
        assertEquals("C:/test/image1.png", newManager.getRecents().get(1), "First file should be second");
    }

    @Test
    @DisplayName("GetRecents returns observable list")
    void testGetRecentsReturnsObservableList() {
        assertNotNull(manager.getRecents(), "getRecents should not return null");
        assertEquals(0, manager.getRecents().size(), "New manager should have empty recents list");
    }

    @Test
    @DisplayName("Constructor with invalid max uses default")
    void testConstructorWithInvalidMaxUsesDefault() {
        RecentFilesManager invalidManager = new RecentFilesManager(-5);
        assertNotNull(invalidManager.getRecents(), "Manager should handle invalid max entries");
        
        // Add more than the invalid number to verify default is used
        for (int i = 0; i < 15; i++) {
            invalidManager.add("C:/test/image" + i + ".png");
        }
        
        assertTrue(invalidManager.getRecents().size() <= 10, "Should use default max of 10");
    }

    @Test
    @DisplayName("Constructor with zero max uses default")
    void testConstructorWithZeroMaxUsesDefault() {
        RecentFilesManager zeroManager = new RecentFilesManager(0);
        assertNotNull(zeroManager.getRecents(), "Manager should handle zero max entries");
    }

    @Test
    @DisplayName("Handles file paths with special characters")
    void testHandlesFilePathsWithSpecialCharacters() {
        String specialPath = "C:/test/image with spaces & symbols!@#.png";
        manager.add(specialPath);
        
        assertEquals(1, manager.getRecents().size(), "Should handle special characters");
        assertEquals(specialPath, manager.getRecents().get(0), "Path should be preserved exactly");
    }

    @Test
    @DisplayName("Multiple adds are reflected in list")
    void testMultipleAddsReflectedInList() {
        for (int i = 0; i < 5; i++) {
            manager.add("C:/test/image" + i + ".png");
            assertEquals(i + 1, manager.getRecents().size(), "Size should increment with each add");
        }
        
        assertEquals(5, manager.getRecents().size(), "Should have all 5 files");
    }
}
