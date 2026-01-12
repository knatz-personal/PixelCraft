package com.pixelcraft.performance;

import com.pixelcraft.model.RasterImage;
import java.io.File;

/**
 * Simple performance test to measure image loading time.
 * Run this directly to test image loading performance.
 */
public class ImageLoadPerformanceTest {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java ImageLoadPerformanceTest <image-file-path>");
            System.out.println("Example: java ImageLoadPerformanceTest test.jpg");
            return;
        }
        
        File imageFile = new File(args[0]);
        if (!imageFile.exists()) {
            System.err.println("File not found: " + imageFile.getAbsolutePath());
            return;
        }
        
        System.out.println("Testing image load performance...");
        System.out.println("File: " + imageFile.getName());
        System.out.println("Size: " + (imageFile.length() / 1024) + " KB");
        System.out.println();
        
        // Warm-up run (JIT compilation)
        System.out.println("Warm-up run...");
        loadAndMeasure(imageFile);
        
        // Actual performance test runs
        System.out.println("\nPerformance test (5 runs):");
        long totalTime = 0;
        for (int i = 1; i <= 5; i++) {
            long time = loadAndMeasure(imageFile);
            totalTime += time;
            System.out.println("  Run " + i + ": " + time + " ms");
        }
        
        double avgTime = totalTime / 5.0;
        System.out.println("\nAverage load time: " + String.format("%.1f", avgTime) + " ms");
    }
    
    private static long loadAndMeasure(File file) {
        long start = System.currentTimeMillis();
        
        long t1 = System.currentTimeMillis();
        RasterImage image = new RasterImage(file);
        long imageLoadTime = System.currentTimeMillis() - t1;
        
        // Force conversion to JavaFX image to measure full pipeline
        long t2 = System.currentTimeMillis();
        image.toFXImage();
        long fxConversionTime = System.currentTimeMillis() - t2;
        
        long elapsed = System.currentTimeMillis() - start;
        
        // Print breakdown for first run
        if (imageLoadTime > 0) {
            System.out.println("    -> BufferedImage load: " + imageLoadTime + " ms");
            System.out.println("    -> JavaFX conversion: " + fxConversionTime + " ms");
        }
        
        // Clean up
        image.clearCache();
        
        return elapsed;
    }
}
