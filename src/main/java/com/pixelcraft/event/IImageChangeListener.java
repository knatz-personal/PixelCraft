package com.pixelcraft.event;

import java.io.File;
import java.util.Optional;

import com.pixelcraft.model.RasterImage;

public interface IImageChangeListener {

    void onImageChanged(Optional<RasterImage> image);

    void onFileChanged(Optional<File> file);

    void onModificationStateChanged(boolean modified);

}
