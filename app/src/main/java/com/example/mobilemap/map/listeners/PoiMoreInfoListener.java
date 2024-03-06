package com.example.mobilemap.map.listeners;

import android.view.View;

import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.pois.PoisActivity;

/**
 * Ecouteur pour afficher plus de détails sur un site sélectionné
 *
 * @author J.Houdé
 */
public class PoiMoreInfoListener implements View.OnClickListener {
    private final MainActivity activity;
    private final long itemId;

    /**
     * Ecouteur pour afficher plus de détails sur un site sélectionné
     *
     * @param activity      activité principale
     * @param overlayItemId identifiant du fichier xml
     */
    public PoiMoreInfoListener(MainActivity activity, String overlayItemId) {
        this.activity = activity;
        this.itemId = Long.parseLong(overlayItemId);
    }


    @Override
    public void onClick(View v) {
        activity.getPoiActivityLauncher().launch(PoisActivity.createIntent(activity, itemId));
    }
}
