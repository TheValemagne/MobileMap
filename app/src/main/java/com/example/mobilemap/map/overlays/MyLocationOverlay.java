package com.example.mobilemap.map.overlays;

import android.location.Location;

import com.example.mobilemap.map.MainActivity;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MyLocationOverlay extends MyLocationNewOverlay {
    private final MainActivity activity;
    public MyLocationOverlay(GpsMyLocationProvider gpsMyLocationProvider, MapView mapView, MainActivity activity) {
        super(gpsMyLocationProvider, mapView);
        this.activity = activity;
    }

    @Override
    public void onLocationChanged(Location location, IMyLocationProvider source) {
        activity.shouldShowLocationBtn(location != null);

        super.onLocationChanged(location, source);
    }
}
