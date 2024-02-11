package com.example.mobilemap.map.listeners;

import com.example.mobilemap.R;
import com.example.mobilemap.map.CustomInfoWindow;
import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.map.MapManager;
import com.example.mobilemap.map.overlays.CustomOverlayWithIW;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.Map;

public class MarkerGestureListener implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
    private final MapManager mapManager;
    private final MapView mapView;
    private final Map<String, InfoWindow> itemInfoWindowMap;
    private final MainActivity activity;

    private String lastCircleCenterItemUid;

    public void setLastCircleCenterItemUid(String uid) {
        this.lastCircleCenterItemUid = uid;
    }

    public MarkerGestureListener(MapView mapView, MapManager mapManager, Map<String, InfoWindow> itemInfoWindowMap, MainActivity activity) {
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
            itemInfoWindowMap.put(uid, new CustomInfoWindow(R.layout.poi_info_window, item.getPoint(), mapView, mapManager.getCircleManager(), activity));
        }

        InfoWindow infoWindow = itemInfoWindowMap.get(uid);

        if (infoWindow != null && infoWindow.isOpen()) {
            infoWindow.close();
            return true;
        }

        showInfoWindow(item, infoWindow);

        return true;
    }

    private void showInfoWindow(OverlayItem item, InfoWindow infoWindow) {
        OverlayWithIW overlayWithIW = new CustomOverlayWithIW(item);

        overlayWithIW.setInfoWindow(infoWindow);
        overlayWithIW.getInfoWindow().open(overlayWithIW, (GeoPoint) item.getPoint(),
                CustomInfoWindow.OFFSET_X, CustomInfoWindow.OFFSET_Y);

        mapView.getOverlays().add(overlayWithIW);
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
