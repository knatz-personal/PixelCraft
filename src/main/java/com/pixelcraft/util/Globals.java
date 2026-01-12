package com.pixelcraft.util;

public final class Globals {

    private Globals() {
        /* Do not allow creation of instances */ }

    public static final double MIN_ZOOM = 0.01;
    public static final double MAX_ZOOM = 10.00;

    public static final double ZOOM_STEP = 1.1; //10% Zoom
    public static final double DOUBLE_DELTA = 0.0001;

    public static final int DEFAULT_HEIGHT = 600;
    public static final int DEFAULT_WIDTH = 800;

    public static final double MAX_TEXTURE_SIZE = 8192.0;
    public static final double MAX_IMAGE_DIMENSION = 16384.0;
}
