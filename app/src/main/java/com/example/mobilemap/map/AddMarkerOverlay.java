package com.example.mobilemap.map;

import android.util.Log;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

public class AddMarkerOverlay extends Overlay {

    public AddMarkerOverlay() {
        // vide
    }

    @Override
    public boolean onLongPress(MotionEvent event, MapView mapView) {
        double latitude = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY()).getLatitude();
        double longitude = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY()).getLongitude();

        Log.d("LongPress", "Latitude: " + latitude + ", Longitude: " + longitude);
        return true;
    }
}
