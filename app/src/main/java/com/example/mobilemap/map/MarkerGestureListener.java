package com.example.mobilemap.map;

import android.content.res.Resources;

import com.example.mobilemap.map.overlays.CustomOverlayWithIW;

import org.osmdroid.api.IGeoPoint;
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
    private Resources resources;

    private String lastCircleCenterItemUid;

    public void setLastCircleCenterItemUid(IGeoPoint point) {
        this.lastCircleCenterItemUid = MapManager.getItemUid(point);
    }

    public MarkerGestureListener(MapView mapView, MapManager mapManager, Map<String, InfoWindow> itemInfoWindowMap, Resources resources) {
        this.mapView = mapView;
        this.mapManager = mapManager;
        this.itemInfoWindowMap = itemInfoWindowMap;
        this.resources = resources;

        lastCircleCenterItemUid = "";
    }

    @Override
    public boolean onItemSingleTapUp(int index, OverlayItem item) {
        String uid = MapManager.getItemUid(item.getPoint());
        if (!itemInfoWindowMap.containsKey(uid)) {
            itemInfoWindowMap.put(uid, new CustomInfoWindow(org.osmdroid.library.R.layout.bonuspack_bubble, item.getPoint(), mapView, mapManager.getCircleManager(), resources));
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
