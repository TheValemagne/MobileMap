package com.example.mobilemap.map.overlays;

import android.view.MotionEvent;

import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.pois.PoisActivity;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

/**
 * Overlay pour ajouter de nouveau marquer sur la carte
 */
public class AddMarkerOverlay extends Overlay {
    private final MainActivity activity;

    public AddMarkerOverlay(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onLongPress(MotionEvent event, MapView mapView) {
        // récupération des coordonnées du futur marqueur
        double latitude = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY()).getLatitude();
        double longitude = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY()).getLongitude();

        // lancement de l'activité gérant les sites our créer le nouveau marqueur
        activity.poiActivityLauncher.launch(PoisActivity.createIntent(activity, latitude, longitude));
        return true;
    }

}
