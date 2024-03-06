package com.example.mobilemap.map.overlays.initiators;

import androidx.core.content.res.ResourcesCompat;

import com.example.mobilemap.R;
import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.map.manager.MapManager;
import com.example.mobilemap.map.listeners.MarkerGestureListener;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.Objects;

/**
 * Initialisateur de l'overlay des marqueurs
 *
 * @author J.Houdé
 */
public class ItemizedIconOverlayInitiator extends OverlayInitiator{
    private final MapManager mapManager;
    private final MarkerGestureListener markerGestureListener;
    private final MainActivity activity;

    /**
     * Initialisateur de l'overlay des marqueurs
     *
     * @param mapView vue de la carte
     * @param mapManager gestionaire de la carte
     * @param markerGestureListener ecouteur de geste
     * @param activity activité mère
     */
    public ItemizedIconOverlayInitiator(MapView mapView, MapManager mapManager, MarkerGestureListener markerGestureListener, MainActivity activity) {
        super(mapView);

        this.mapManager = mapManager;
        this.activity = activity;

        this.markerGestureListener = markerGestureListener;
    }

    @Override
    public ItemizedIconOverlay<OverlayItem> init() {

        return new ItemizedIconOverlay<>(mapManager.getOverlayItems(),
                Objects.requireNonNull(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.small_marker, activity.getTheme())),
                markerGestureListener,
                activity.getApplicationContext());
    }
}
