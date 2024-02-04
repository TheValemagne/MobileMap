package com.example.mobilemap.map;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.Map;

public class MarkerGestureListener implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
    private final MapManager mapManager;
    private final MapView mapView;
    private final Map<String, InfoWindow> integerInfoWindowMap;

    private String lastCircleCenterItemUid;

    public void setLastCircleCenterItemUid(IGeoPoint point) {
        this.lastCircleCenterItemUid = MapManager.getItemUid(point);
    }

    public MarkerGestureListener(MapView mapView, MapManager mapManager, Map<String, InfoWindow> integerInfoWindowMap) {
        this.mapView = mapView;
        this.mapManager = mapManager;
        this.integerInfoWindowMap = integerInfoWindowMap;

        lastCircleCenterItemUid = "";
    }

    @Override
    public boolean onItemSingleTapUp(int index, OverlayItem item) {
        String uid = MapManager.getItemUid(item.getPoint());
        if (!integerInfoWindowMap.containsKey(uid)) {
            integerInfoWindowMap.put(uid, new CustomInfoWindow(org.osmdroid.library.R.layout.bonuspack_bubble, mapView));
        }

        InfoWindow infoWindow = integerInfoWindowMap.get(uid);

        if (infoWindow != null && infoWindow.isOpen()) {
            infoWindow.close();
            return true;
        }

        mapManager.showInfoWindow(item, infoWindow);

        return true;
    }

    @Override
    public boolean onItemLongPress(int index, OverlayItem item) {
        IGeoPoint point = item.getPoint();

        if (lastCircleCenterItemUid.equals(MapManager.getItemUid(point))) {
            mapManager.removeCircle();
            lastCircleCenterItemUid = "";
            return true;
        }

        mapManager.showAddCircleAroundPoiDialog(item);

        return true;
    }

}
