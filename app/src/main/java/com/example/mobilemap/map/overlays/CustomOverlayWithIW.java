package com.example.mobilemap.map.overlays;

import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayWithIW;

/**
 * Overlay pour afficher le détail d'un marqueur de la carte
 */
public class CustomOverlayWithIW extends OverlayWithIW {
    public CustomOverlayWithIW(OverlayItem item) {
        super();
        setId(item.getUid());
        setTitle(item.getTitle());
        setSnippet(item.getSnippet());
    }
}
