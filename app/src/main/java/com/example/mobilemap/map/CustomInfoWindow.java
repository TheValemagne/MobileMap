package com.example.mobilemap.map;

import android.content.res.Resources;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import com.example.mobilemap.R;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.text.MessageFormat;

/**
 * Dialogue d'information d'un marqueur de la carte
 */
public class CustomInfoWindow extends InfoWindow {
    public static final int OFFSET_X = 0;
    public static final int OFFSET_Y = -50;
    private final CircleManager circleManager;
    private final Resources resources;
    private final IGeoPoint point;

    public CustomInfoWindow(int layoutResId, IGeoPoint point, MapView mapView, CircleManager circleManager, Resources resources) {
        super(layoutResId, mapView);

        this.point = point;
        this.circleManager = circleManager;
        this.resources = resources;
    }

    @Override
    public void onOpen(Object item) {
        OverlayWithIW overlayItem = (OverlayWithIW) item;

        TextView bubble_title = mView.findViewById(org.osmdroid.library.R.id.bubble_title);
        bubble_title.setText(overlayItem.getTitle());

        TextView bubble_description = mView.findViewById(org.osmdroid.library.R.id.bubble_description);
        bubble_description.setText(overlayItem.getSnippet());

        if(circleManager.hasSavedCircle()) {
            IGeoPoint center = circleManager.getCircleCenter();
            Location centerLocation = new Location("Center");
            centerLocation.setLatitude(center.getLatitude());
            centerLocation.setLongitude(center.getLongitude());

            Location actualPoint = new Location("Marker");
            actualPoint.setLatitude(point.getLatitude());
            actualPoint.setLongitude(point.getLongitude());

            TextView subDescription = mView.findViewById(org.osmdroid.library.R.id.bubble_subdescription);
            subDescription.setVisibility(View.VISIBLE);
            subDescription.setText(MessageFormat.format(resources.getString(R.string.distanceLabel), String.valueOf(centerLocation.distanceTo(actualPoint))));
        }

    }

    @Override
    public void onClose() {
        // vide
    }
}
