package com.example.mobilemap.map;

import android.view.MotionEvent;

import com.example.mobilemap.MainActivity;
import com.example.mobilemap.PoisActivity;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

public class AddMarkerOverlay extends Overlay {
    private final MainActivity activity;

    public AddMarkerOverlay(MainActivity activity) {
        // vide
        this.activity = activity;
    }

    @Override
    public boolean onLongPress(MotionEvent event, MapView mapView) {
        // récupération de la position du futur marqueur
        double latitude = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY()).getLatitude();
        double longitude = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY()).getLongitude();

        // lancement de l'activité gérant les sites
        activity.poiActivityLauncher.launch(PoisActivity.createIntent(activity, latitude, longitude));
        return true;
    }
}
