package com.example.mobilemap.map.overlays;

import android.content.SharedPreferences;
import android.location.Location;

import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.map.manager.MapManager;
import com.example.mobilemap.map.SharedPreferencesConstant;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * Overlay pour afficher la position actuelle
 *
 * @author J.Houdé
 */
public class MyLocationOverlay extends MyLocationNewOverlay {
    private final MainActivity activity;
    private final MapManager mapManager;

    /**
     * Overlay pour afficher la position actuelle
     *
     * @param gpsMyLocationProvider location provider d'Osmdroid
     * @param mapView               vue de la carte
     * @param activity              activité principale
     * @param mapManager            gestionnaire de carte
     */
    public MyLocationOverlay(GpsMyLocationProvider gpsMyLocationProvider, MapView mapView, MainActivity activity, MapManager mapManager) {
        super(gpsMyLocationProvider, mapView);
        this.activity = activity;
        this.mapManager = mapManager;
    }

    @Override
    public void onLocationChanged(Location location, IMyLocationProvider source) {
        super.onLocationChanged(location, source); // utilisation de hanlder.postAtTime dans le code source pour actualiser la position

        activity.shouldShowLocationBtn(location != null); // affichage ou non du bouton de localisation

        if (location == null || !mapManager.isCircleAroundMe()) {
            return;
        }

        SharedPreferences sharedPreferences = mapManager.getSharedPreferences(); // récupération des données sauvegardées

        String circleRadiusString = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING);
        double circleRadius = Double.parseDouble(circleRadiusString);

        long categoryFilter = sharedPreferences.getLong(SharedPreferencesConstant.CIRCLE_CATEGORY_FILTER, SharedPreferencesConstant.NOT_FOUND_ID);

        // mise à jour du cercle avec le déplacement de l'utilisateur
        GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapManager.drawCircleAroundMe(point, circleRadius, categoryFilter); // actualisation du cercle
    }
}
