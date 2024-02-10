package com.example.mobilemap.map.listeners;

import android.view.View;

import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.pois.PoisActivity;

import org.osmdroid.views.overlay.OverlayWithIW;

public class PoiMoreInfoLinstener implements View.OnClickListener {
    private final MainActivity activity;
    private final OverlayWithIW overlayItem;

    public PoiMoreInfoLinstener(MainActivity activity, OverlayWithIW overlayItem) {
        this.activity = activity;
        this.overlayItem = overlayItem;
    }


    @Override
    public void onClick(View v) {
        activity.getPoiActivityLauncher().launch(PoisActivity.createIntent(activity, Long.parseLong(overlayItem.getId())));
    }
}
