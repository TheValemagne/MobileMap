package com.example.mobilemap.map.listeners;

import com.example.mobilemap.R;
import com.example.mobilemap.map.CustomInfoWindow;
import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.map.MapManager;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.Map;

public class MarkerGestureListener implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
    private final MapManager mapManager;
    private final MapView mapView;
    private final Map<String, CustomInfoWindow> itemInfoWindowMap;
    private final MainActivity activity;

    private String lastCircleCenterItemUid;

    public void setLastCircleCenterItemUid(String uid) {
        this.lastCircleCenterItemUid = uid;
    }

    public MarkerGestureListener(MapView mapView, MapManager mapManager, Map<String, CustomInfoWindow> itemInfoWindowMap, MainActivity activity) {
        this.mapView = mapView;
        this.mapManager = mapManager;
        this.itemInfoWindowMap = itemInfoWindowMap;
        this.activity = activity;

        lastCircleCenterItemUid = "";
    }

    @Override
    public boolean onItemSingleTapUp(int index, OverlayItem item) {
        String uid = item.getUid();
        if (!itemInfoWindowMap.containsKey(uid)) {
            itemInfoWindowMap.put(uid, new CustomInfoWindow(R.layout.poi_info_window, (GeoPoint) item.getPoint(), mapView, mapManager.getCircleManager(), activity));
        }

        InfoWindow infoWindow = itemInfoWindowMap.get(uid);

        if (infoWindow != null && infoWindow.isOpen()) {
            infoWindow.close();
            return true;
        }

        mapView.getOverlays().add(mapManager.createOverlayWithIW(item, infoWindow));

        return true;
    }

    @Override
    public boolean onItemLongPress(int index, OverlayItem item) {
        if (lastCircleCenterItemUid.equals(item.getUid())) {
            mapManager.removeCircle();
            lastCircleCenterItemUid = "";
            return true;
        }

        mapManager.showAddCircleAroundPoiDialog(item);

        return true;
    }

}
