package com.example.mobilemap.map.overlays;

import android.content.Context;
import android.graphics.Canvas;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.compass.CompassOverlay;

public class MapNorthCompassOverlay extends CompassOverlay {
    public MapNorthCompassOverlay(Context context, MapView mapView) {
        super(context, mapView);
    }

    @Override
    public void draw(Canvas c, Projection pProjection) {
        drawCompass(c, -mMapView.getMapOrientation(), pProjection.getScreenRect());
    }
}
