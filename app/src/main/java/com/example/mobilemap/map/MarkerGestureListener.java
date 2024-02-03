package com.example.mobilemap.map;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class MarkerGestureListener implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
    private final MapManager mapManager;
    private final MapView mapView;
    private final Map<Integer, InfoWindow> integerInfoWindowMap;

    private String lastCircleCenterItemUid;

    public void setLastCircleCenterItemUid(IGeoPoint point) {
        this.lastCircleCenterItemUid = getItemUid(point);
    }

    public MarkerGestureListener(MapView mapView, MapManager mapManager) {
        this.mapView = mapView;
        this.mapManager = mapManager;

        integerInfoWindowMap = new HashMap<>();
        lastCircleCenterItemUid = "";
    }

    @Override
    public boolean onItemSingleTapUp(int index, OverlayItem item) {
        if (!integerInfoWindowMap.containsKey(index)) {
            integerInfoWindowMap.put(index, new CustomInfoWindow(org.osmdroid.library.R.layout.bonuspack_bubble, mapView));
        }

        InfoWindow infoWindow = integerInfoWindowMap.get(index);

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

        if (lastCircleCenterItemUid.equals(getItemUid(point))) {
            mapManager.removeCircle();
            lastCircleCenterItemUid = "";
            return true;
        }

        mapManager.addOverlayItemCircle(index);
        lastCircleCenterItemUid = getItemUid(point);

        return true;
    }

    private String getItemUid(IGeoPoint point) {
        return MessageFormat.format("{0}:{1}", point.getLatitude(), point.getLongitude());
    }
}
