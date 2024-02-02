package com.example.mobilemap.listener;

import com.example.mobilemap.map.MapManager;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.text.MessageFormat;

public class MarkerGertureListener implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
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
        System.out.println("long Tap");
        System.out.println(lastItemUid.equals(getItemUid(item)));

        if(lastItemUid.equals(getItemUid(item))) {
            mapManager.removeCircle();
            lastItemUid = "";
            return true;
        }

        mapManager.addOverlayItemCircle(item);
        lastItemUid = getItemUid(item);

        return true;
    }

    private String getItemUid(OverlayItem item) {
        return MessageFormat.format("{0}:{1}", item.getPoint().getLatitude(), item.getPoint().getLongitude());
    }
}
