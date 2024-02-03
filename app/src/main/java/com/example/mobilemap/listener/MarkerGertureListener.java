package com.example.mobilemap.listener;

import com.example.mobilemap.map.MapManager;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.text.MessageFormat;

public class MarkerGertureListener implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
    public void setLastItemUid(IGeoPoint point) {
        this.lastItemUid = getItemUid(point);
    }

    private String lastItemUid;
    private final MapManager mapManager;

    public MarkerGertureListener(MapManager mapManager) {
        this.mapManager = mapManager;
        this.lastItemUid = "";
    }

    @Override
    public boolean onItemSingleTapUp(int index, OverlayItem item) {
        System.out.println(item.getTitle());
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, OverlayItem item) {
        IGeoPoint point = item.getPoint();
        System.out.println("long Tap");
        System.out.println(lastItemUid.equals(getItemUid(point)));

        if(lastItemUid.equals(getItemUid(point))) {
            mapManager.removeCircle();
            lastItemUid = "";
            return true;
        }

        mapManager.addOverlayItemCircle(index);
        lastItemUid = getItemUid(point);

        return true;
    }

    private String getItemUid(IGeoPoint point) {
        return MessageFormat.format("{0}:{1}", point.getLatitude(), point.getLongitude());
    }
}
