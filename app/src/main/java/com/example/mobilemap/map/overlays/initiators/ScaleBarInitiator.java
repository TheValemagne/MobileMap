package com.example.mobilemap.map.overlays.initiators;

import android.content.Context;
import android.util.DisplayMetrics;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;

/**
 * Initialisateur de l'overlay de l'échelle de la carte
 *
 * @author J.Houdé
 */
public class ScaleBarInitiator extends OverlayInitiator{
    private static final int SCALE_BAR_Y_OFFSET = 10;
    private final Context context;

    /**
     * Initialisateur de l'overlay de l'échelle de la carte
     *
     * @param mapView vue de la carte
     * @param context contexte de l'application
     */
    public ScaleBarInitiator(MapView mapView, Context context) {
        super(mapView);

        this.context = context;
    }

    @Override
    public ScaleBarOverlay init() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(displayMetrics.widthPixels / 2, SCALE_BAR_Y_OFFSET);

        return scaleBarOverlay;
    }
}
