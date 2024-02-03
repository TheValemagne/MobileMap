package com.example.mobilemap.map;

import android.widget.TextView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class CustomInfoWindow extends InfoWindow {
    public static final int OFFSET_X = 0;
    public static final int OFFSET_Y = -50;
    public CustomInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }

    @Override
    public void onOpen(Object item) {
        OverlayWithIW overlayItem = (OverlayWithIW) item;
        TextView bubble_title = mView.findViewById(org.osmdroid.library.R.id.bubble_title);
        bubble_title.setText(overlayItem.getTitle());
        System.out.println(bubble_title.getText());

        TextView bubble_description = mView.findViewById(org.osmdroid.library.R.id.bubble_description);
        bubble_description.setText(overlayItem.getSnippet());
    }

    @Override
    public void onClose() {

    }
}
