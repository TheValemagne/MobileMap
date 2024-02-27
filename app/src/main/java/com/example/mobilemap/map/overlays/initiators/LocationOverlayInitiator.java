package com.example.mobilemap.map.overlays.initiators;

import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.map.manager.MapManager;
import com.example.mobilemap.map.overlays.MyLocationOverlay;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * Initialisateur de l'overlay de la localisation
 *
 * @author J.Houdé
 */
public class LocationOverlayInitiator extends OverlayInitiator{
    private final MainActivity activity;
    private final MapManager mapManager;

    /**
     * Initialisateur de l'overlay de la localisation
     *
     * @param mapView vue de la carte
     * @param mapManager gestionaire de la carte
     * @param activity activité mère
     */
    public LocationOverlayInitiator(MapView mapView, MainActivity activity, MapManager mapManager) {
        super(mapView);
        this.activity = activity;
        this.mapManager = mapManager;
    }

    @Override
    public MyLocationNewOverlay init() {
        MyLocationNewOverlay myLocationNewOverlay = new MyLocationOverlay(new GpsMyLocationProvider(activity.getApplicationContext()), mapView, activity, mapManager);
        myLocationNewOverlay.enableMyLocation();

        return myLocationNewOverlay;
    }
}
