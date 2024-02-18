package com.example.mobilemap.map.overlays.initiators;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

/**
 * Initialisateur d'overlay
 *
 * @author J.Houdé
 */
public abstract class OverlayInitiator {
    protected final MapView mapView;

    /**
     * Initialisateur d'overlay
     *
     * @param mapView vue de la carte
     */
    public OverlayInitiator(MapView mapView) {
        this.mapView = mapView;
    }

    /**
     * Initialisation de l'overlay
     * @return instance de l'overlay
     */
    public abstract Overlay init();
}
