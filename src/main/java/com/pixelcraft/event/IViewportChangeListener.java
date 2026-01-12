package com.pixelcraft.event;

public interface IViewportChangeListener {

    void onZoomChanged(double newZoom);

    void onPanChanged();
}
