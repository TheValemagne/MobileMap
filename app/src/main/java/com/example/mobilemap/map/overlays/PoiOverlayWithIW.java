package com.example.mobilemap.map.overlays;

import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayWithIW;

/**
 * Overlay pour afficher le détail d'un marqueur de la carte
 *
 * @author J.Houdé
 */
public class PoiOverlayWithIW extends OverlayWithIW {
    /**
     * Overlay pour afficher le détail d'un marqueur de la carte
     *
     * @param item élément à représenter dans la fenêtre
     */
    public PoiOverlayWithIW(OverlayItem item) {
        super();

        setId(item.getUid());
        setTitle(item.getTitle());
        setSnippet(item.getSnippet());
    }
}
