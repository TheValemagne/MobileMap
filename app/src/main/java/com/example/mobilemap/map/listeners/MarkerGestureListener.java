package com.example.mobilemap.map.listeners;

import com.example.mobilemap.R;
import com.example.mobilemap.map.PoiInfoWindow;
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
    private final Map<String, PoiInfoWindow> infoWindowMap;
    private final MainActivity activity;

    private String currentCircleCenterItemUid;

    /**
     * Actualisation de l'identifiant du site actuellement utilisé comme centre du cercle
     * @param uid identifiant du site au centre du cercle
     */
    public void setCurrentCircleCenterItemUid(String uid) {
        this.currentCircleCenterItemUid = uid;
    }

    /**
     * Ecouteur pour la gestion des gestes de la ItemizedIconOverlay
     *
     * @param mapView           vue de la carte
     * @param mapManager        gestionnaire de la carte
     * @param infoWindowMap map d'infoWindows
     * @param activity          activité principale
     */
    public MarkerGestureListener(MapView mapView, MapManager mapManager, Map<String, PoiInfoWindow> infoWindowMap, MainActivity activity) {
        this.mapView = mapView;
        this.mapManager = mapManager;
        this.infoWindowMap = infoWindowMap;
        this.activity = activity;

        currentCircleCenterItemUid = "";
    }

    @Override
    public boolean onItemSingleTapUp(int index, OverlayItem item) {
        String uid = item.getUid();

        if (!infoWindowMap.containsKey(uid)) { // création d'une infoWindows pour le marqueur
            infoWindowMap.put(uid,
                    new PoiInfoWindow(R.layout.poi_info_window, (GeoPoint) item.getPoint(), mapView, mapManager.getCircleManager(), activity));
        }

        InfoWindow infoWindow = infoWindowMap.get(uid);

        if (infoWindow != null && infoWindow.isOpen()) { // fermeture de l'infoWindow si elle est déjà ouverte
            infoWindow.close();
            return true;
        }

        mapView.getOverlays().add(mapManager.createOverlayWithIW(item, infoWindow)); // ouverture de l'infoWindows et affichage sur la carte

        return true;
    }

    @Override
    public boolean onItemLongPress(int index, OverlayItem item) {
        if (currentCircleCenterItemUid.equals(item.getUid())) { // s'il y a déjà un cercle pour le site sélectionné
            mapManager.removeCircle(); // suppression du cercle
            currentCircleCenterItemUid = ""; // suppression de l'identfiant enregistré
            return true;
        }

        mapManager.showAddCircleAroundPoiDialog(item);

        return true;
    }

}
