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
     * @param item élément à représenter dans la fenêtre d'information
     */
    public PoiOverlayWithIW(OverlayItem item) {
        super();

        setId(item.getUid()); // identifiant du site de la base de données
        setTitle(item.getTitle()); // nom du site
        setSnippet(item.getSnippet()); // résumé du site
    }
}
