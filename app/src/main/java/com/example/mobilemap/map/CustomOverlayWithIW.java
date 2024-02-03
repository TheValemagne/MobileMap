package com.example.mobilemap.map;

import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayWithIW;

public class CustomOverlayWithIW extends OverlayWithIW {
    public CustomOverlayWithIW(OverlayItem item) {
        super();
        setTitle(item.getTitle());
        setSnippet(item.getSnippet());
    }

    @Override
    public void closeInfoWindow() {
        super.closeInfoWindow();
    }
}
