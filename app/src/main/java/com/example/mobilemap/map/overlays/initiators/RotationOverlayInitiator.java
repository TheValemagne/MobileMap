package com.example.mobilemap.map.overlays.initiators;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

/**
 * Initialisateur de l'overlay pour les gestions de rotation
 *
 * @author J.Houdé
 */
public class RotationOverlayInitiator extends OverlayInitiator{

    /**
     * Initialisateur de l'overlay pour les gestions de rotation
     *
     * @param mapView vue de la carte
     */
    public RotationOverlayInitiator(MapView mapView) {
        super(mapView);
    }

    @Override
    public RotationGestureOverlay init() {
        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(mapView);
        rotationGestureOverlay.setEnabled(true);
        return rotationGestureOverlay;
    }
}
