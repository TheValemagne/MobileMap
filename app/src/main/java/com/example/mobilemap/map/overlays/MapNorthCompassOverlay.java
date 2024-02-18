package com.example.mobilemap.map.overlays;

import android.content.Context;
import android.graphics.Canvas;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.compass.CompassOverlay;

/**
 * Overlay de boussole liée à la rotation de la carte
 *
 * @author J.Houdé
 */
public class MapNorthCompassOverlay extends CompassOverlay {
    public MapNorthCompassOverlay(Context context, MapView mapView) {
        super(context, mapView);
    }

    @Override
    public void draw(Canvas c, Projection pProjection) {
        // rotation de la boussole liée à la rotation de la carte
        drawCompass(c, -mMapView.getMapOrientation(), pProjection.getScreenRect());
    }
}
