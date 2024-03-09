package com.example.mobilemap.map.overlays.initiators;

import android.content.Context;

import com.example.mobilemap.map.overlays.MapNorthCompassOverlay;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;

/**
 * Initialisateur de l'overlay de la boussole indiquant le sens d'orientation de la carte
 *
 * @author J.Houdé
 */
public class CompassOverlayInitiator extends OverlayInitiator{
    private static final int COMPASS_X_OFFSET = 40;
    private static final int COMPASS_Y_OFFSET = 55;
    private final Context context;

    /**
     * Initialisateur de l'overlay de la boussole indiquant le sens d'orientation de la carte
     *
     * @param mapView vue de la carte
     * @param context contexte de l'application
     */
    public CompassOverlayInitiator(MapView mapView, Context context) {
        super(mapView);

        this.context = context;
    }

    @Override
    public CompassOverlay init() {
        CompassOverlay mapNorthCompassOverlay = new MapNorthCompassOverlay(context, mapView);
        mapNorthCompassOverlay.enableCompass();
        mapNorthCompassOverlay.setCompassCenter(COMPASS_X_OFFSET, COMPASS_Y_OFFSET); // placement de la boussole sur la carte

        return mapNorthCompassOverlay;
    }
}
