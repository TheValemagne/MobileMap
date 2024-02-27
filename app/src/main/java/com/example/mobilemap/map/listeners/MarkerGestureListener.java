package com.example.mobilemap.map.listeners;

import com.example.mobilemap.R;
import com.example.mobilemap.map.CustomInfoWindow;
import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.map.manager.MapManager;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.Map;

/**
 * Ecouteur pour afficher plus de détails sur un site sélectionné
 *
 * @author J.Houdé
 */
public class MarkerGestureListener implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
    private final MapManager mapManager;
    private final MapView mapView;
    private final Map<String, CustomInfoWindow> infoWindowMap;
    private final MainActivity activity;

    private String lastCircleCenterItemUid;

    public void setLastCircleCenterItemUid(String uid) {
        this.lastCircleCenterItemUid = uid;
    }

    /**
     * Ecouteur pour la gestion des gestes de la ItemizedIconOverlay
     *
     * @param mapView           vue de la carte
     * @param mapManager        gestionnaire de la carte
     * @param infoWindowMap map d'infoWindows
     * @param activity          activité mère
     */
    public MarkerGestureListener(MapView mapView, MapManager mapManager, Map<String, CustomInfoWindow> infoWindowMap, MainActivity activity) {
        this.mapView = mapView;
        this.mapManager = mapManager;
        this.infoWindowMap = infoWindowMap;
        this.activity = activity;

        lastCircleCenterItemUid = "";
    }

    @Override
    public boolean onItemSingleTapUp(int index, OverlayItem item) {
        String uid = item.getUid();

        if (!infoWindowMap.containsKey(uid)) { // création d'une infoWindows pour le marqueur
            infoWindowMap.put(uid,
                    new CustomInfoWindow(R.layout.poi_info_window, (GeoPoint) item.getPoint(), mapView, mapManager.getCircleManager(), activity));
        }

        InfoWindow infoWindow = infoWindowMap.get(uid);

        if (infoWindow != null && infoWindow.isOpen()) { // fermeture de l'infoWindow si elle est déjà ouverte
            infoWindow.close();
            return true;
        }

        mapView.getOverlays().add(mapManager.createOverlayWithIW(item, infoWindow)); // ouverte de l'infoWindows et affichage sur la carte

        return true;
    }

    @Override
    public boolean onItemLongPress(int index, OverlayItem item) {
        if (lastCircleCenterItemUid.equals(item.getUid())) { // s'il y a déjà un cercle pour le site sélectionné
            mapManager.removeCircle();
            lastCircleCenterItemUid = "";
            return true;
        }

        mapManager.showAddCircleAroundPoiDialog(item);

        return true;
    }

}
