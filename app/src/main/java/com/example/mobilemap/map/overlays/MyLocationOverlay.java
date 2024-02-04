package com.example.mobilemap.map.overlays;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.map.MapManager;
import com.example.mobilemap.map.SharedPreferencesConstant;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MyLocationOverlay extends MyLocationNewOverlay {
    private final MainActivity activity;
    private final MapManager mapManager;
    private final Handler handler;
    private final Object handlerToken = new Object();

    public MyLocationOverlay(GpsMyLocationProvider gpsMyLocationProvider, MapView mapView, MainActivity activity, MapManager mapManager) {
        super(gpsMyLocationProvider, mapView);
        this.activity = activity;
        this.mapManager = mapManager;

        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onLocationChanged(Location location, IMyLocationProvider source) {
        super.onLocationChanged(location, source);

        activity.shouldShowLocationBtn(location != null);

        if (getMyLocation() != null && mapManager.isCircleAroundMe()) { // mise à jour du circle avec le déplacement de l'utilisateur
            handler.postAtTime(() -> {
                SharedPreferences sharedPreferences = mapManager.getSharedPreferences();

                String circleRadiusString = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING);
                double circleRadius = Double.parseDouble(circleRadiusString);

                long categoryFilter = sharedPreferences.getLong(SharedPreferencesConstant.CIRCLE_CATEGORY_FILTER, SharedPreferencesConstant.NOT_FOUND_ID);

                mapManager.drawCircleAroundMe(circleRadius, categoryFilter);
            }, handlerToken, 10);
        }
    }
}
