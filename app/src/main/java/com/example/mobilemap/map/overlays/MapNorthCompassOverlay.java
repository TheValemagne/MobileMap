package com.example.mobilemap.map.overlays;

import android.content.Context;
import android.graphics.Canvas;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.compass.CompassOverlay;

/**
 * Overlay de boussole indiquant le sens d'orientation de la carte
 *
 * @author J.Houdé
 */
public class MapNorthCompassOverlay extends CompassOverlay {
    /**
     * Overlay de boussole indiquant le sens d'orientation de la carte
     *
     * @param context contexte de l'appication
     * @param mapView vue de la carte
     */
    public MapNorthCompassOverlay(Context context, MapView mapView) {
        super(context, mapView);
    }

    @Override
    public void draw(Canvas canvas, Projection projection) {
        // rotation de la boussole en fonction de la rotation de la carte pour afficher le nord de la carte
        drawCompass(canvas, -mMapView.getMapOrientation(), projection.getScreenRect());
    }
}
