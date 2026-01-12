package com.pixelcraft.manager;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.pixelcraft.event.IImageChangeListener;
import com.pixelcraft.model.RasterImage;

/**
 * Unit tests for FileManager class
 * 
 * @author Nathan Khupe
 */
class FileManagerTest {

    private FileManager fileManager;
    private IImageChangeListener mockListener;

    @BeforeEach
    void setUp() {
        fileManager = new FileManager();
        mockListener = mock(IImageChangeListener.class);
        fileManager.setListener(mockListener);
    }

    @Test
    @DisplayName("Create new image with specified dimensions")
    void testCreateNewImage() {
        fileManager.createNewImage(100, 200);
        
        Optional<RasterImage> image = fileManager.getCurrentImage();
        assertTrue(image.isPresent(), "Image should be present");
        assertEquals(100, image.get().getWidth());
        assertEquals(200, image.get().getHeight());
        assertTrue(fileManager.isModified(), "New image should be marked as modified");
    }

    @Test
    @DisplayName("Create new image notifies listener")
    void testCreateNewImageNotifiesListener() {
        fileManager.createNewImage(50, 50);
        
        verify(mockListener).onImageChanged(any());
        verify(mockListener).onFileChanged(Optional.empty());
        verify(mockListener).onModificationStateChanged(true);
    }

    @Test
    @DisplayName("Create new image fills with white")
    void testCreateNewImageFillsWithWhite() {
        fileManager.createNewImage(10, 10);
        
        Optional<RasterImage> image = fileManager.getCurrentImage();
        assertTrue(image.isPresent());
        
        // Check that pixels are white
        int white = 0xFFFFFFFF;
        assertEquals(white, image.get().getPixel(0, 0));
        assertEquals(white, image.get().getPixel(5, 5));
        assertEquals(white, image.get().getPixel(9, 9));
    }

    @Test
    @DisplayName("Load image from file")
    void testLoadImage(@TempDir File tempDir) {
        File testFile = new File(tempDir, "test.png");
        
        fileManager.loadImage(testFile);
        
        Optional<File> currentFile = fileManager.getCurrentFile();
        assertTrue(currentFile.isPresent());
        assertEquals(testFile, currentFile.get());
        assertFalse(fileManager.isModified(), "Newly loaded image should not be modified");
    }

    @Test
    @DisplayName("Load image notifies listener")
    void testLoadImageNotifiesListener(@TempDir File tempDir) {
        File testFile = new File(tempDir, "test.png");
        
        fileManager.loadImage(testFile);
        
        verify(mockListener).onImageChanged(any());
        verify(mockListener).onFileChanged(Optional.of(testFile));
        verify(mockListener).onModificationStateChanged(false);
    }

    @Test
    @DisplayName("Save without current file returns false")
    void testSaveWithoutCurrentFile() {
        fileManager.createNewImage(10, 10);
        
        boolean result = fileManager.save();
        
        assertFalse(result, "Save should fail when no file is set");
    }

    @Test
    @DisplayName("Save with current file returns true")
    void testSaveWithCurrentFile(@TempDir File tempDir) {
        File testFile = new File(tempDir, "test.png");
        fileManager.loadImage(testFile);
        fileManager.markModified();
        
        boolean result = fileManager.save();
        
        assertTrue(result, "Save should succeed when file is set");
        assertFalse(fileManager.isModified(), "Image should not be modified after save");
    }

    @Test
    @DisplayName("SaveAs sets new file and saves")
    void testSaveAs(@TempDir File tempDir) {
        File originalFile = new File(tempDir, "original.png");
        fileManager.loadImage(originalFile);
        File newFile = new File(tempDir, "newfile.png");
        
        boolean result = fileManager.saveAs(newFile);
        
        assertTrue(result, "SaveAs should succeed");
        assertEquals(newFile, fileManager.getCurrentFile().get());
        assertFalse(fileManager.isModified(), "Image should not be modified after saveAs");
    }

    @Test
    @DisplayName("Mark modified changes state")
    void testMarkModified(@TempDir File tempDir) {
        File testFile = new File(tempDir, "test.png");
        fileManager.loadImage(testFile);
        // Now modified state should be false
        
        assertFalse(fileManager.isModified());
        
        fileManager.markModified();
        
        assertTrue(fileManager.isModified());
    }

    @Test
    @DisplayName("Mark modified notifies listener")
    void testMarkModifiedNotifiesListener(@TempDir File tempDir) {
        File testFile = new File(tempDir, "test.png");
        fileManager.loadImage(testFile);
        reset(mockListener); // Clear previous interactions
        
        fileManager.markModified();
        
        verify(mockListener).onModificationStateChanged(true);
    }

    @Test
    @DisplayName("Mark modified is idempotent")
    void testMarkModifiedIdempotent(@TempDir File tempDir) {
        File testFile = new File(tempDir, "test.png");
        fileManager.loadImage(testFile);
        reset(mockListener);
        
        fileManager.markModified();
        fileManager.markModified();
        fileManager.markModified();
        
        // Should only notify once
        verify(mockListener, times(1)).onModificationStateChanged(true);
    }

    @Test
    @DisplayName("Get current image returns empty when no image")
    void testGetCurrentImageEmpty() {
        Optional<RasterImage> image = fileManager.getCurrentImage();
        
        assertFalse(image.isPresent(), "Should return empty when no image");
    }

    @Test
    @DisplayName("Get current file returns empty when no file")
    void testGetCurrentFileEmpty() {
        Optional<File> file = fileManager.getCurrentFile();
        
        assertFalse(file.isPresent(), "Should return empty when no file");
    }

    @Test
    @DisplayName("Get display title with no file")
    void testGetDisplayTitleNoFile() {
        String title = fileManager.getDisplayTitle();
        
        assertEquals("PixelCraft", title);
    }

    @Test
    @DisplayName("Get display title with file")
    void testGetDisplayTitleWithFile(@TempDir File tempDir) {
        File testFile = new File(tempDir, "myimage.png");
        fileManager.loadImage(testFile);
        
        String title = fileManager.getDisplayTitle();
        
        assertEquals("PixelCraft - myimage.png", title);
    }

    @Test
    @DisplayName("Get display title with modified file")
    void testGetDisplayTitleWithModifiedFile(@TempDir File tempDir) {
        File testFile = new File(tempDir, "myimage.png");
        fileManager.loadImage(testFile);
        fileManager.markModified();
        
        String title = fileManager.getDisplayTitle();
        
        assertEquals("PixelCraft - myimage.png *", title);
    }

    @Test
    @DisplayName("Get display title with modified new image")
    void testGetDisplayTitleWithModifiedNewImage() {
        fileManager.createNewImage(10, 10);
        
        String title = fileManager.getDisplayTitle();
        
        assertEquals("PixelCraft *", title);
    }

    @Test
    @DisplayName("Get file extension with valid extension")
    void testGetFileExtensionValid() {
        File file = new File("test.png");
        
        String ext = fileManager.getFileExtension(file);
        
        assertEquals("png", ext);
    }

    @Test
    @DisplayName("Get file extension converts to lowercase")
    void testGetFileExtensionLowercase() {
        File file = new File("TEST.PNG");
        
        String ext = fileManager.getFileExtension(file);
        
        assertEquals("png", ext);
    }

    @Test
    @DisplayName("Get file extension with no extension")
    void testGetFileExtensionNoExtension() {
        File file = new File("testfile");
        
        String ext = fileManager.getFileExtension(file);
        
        assertEquals("bmp", ext, "Should return default 'bmp'");
    }

    @Test
    @DisplayName("Get file extension with multiple dots")
    void testGetFileExtensionMultipleDots() {
        File file = new File("my.test.file.jpg");
        
        String ext = fileManager.getFileExtension(file);
        
        assertEquals("jpg", ext);
    }

    @Test
    @DisplayName("Is modified returns true for modified image")
    void testIsModifiedWithModifiedImage() {
        fileManager.createNewImage(10, 10);
        
        assertTrue(fileManager.isModified());
    }

    @Test
    @DisplayName("Is modified returns false for unmodified image")
    void testIsModifiedWithUnmodifiedImage(@TempDir File tempDir) {
        File testFile = new File(tempDir, "test.png");
        fileManager.loadImage(testFile);
        
        assertFalse(fileManager.isModified());
    }

    @Test
    @DisplayName("Set listener without crashing")
    void testSetListener() {
        IImageChangeListener newListener = mock(IImageChangeListener.class);
        
        assertDoesNotThrow(() -> fileManager.setListener(newListener));
    }

    @Test
    @DisplayName("Operations work without listener")
    void testOperationsWithoutListener() {
        FileManager fm = new FileManager();
        
        assertDoesNotThrow(() -> {
            fm.createNewImage(10, 10);
            fm.markModified();
        });
    }
}
