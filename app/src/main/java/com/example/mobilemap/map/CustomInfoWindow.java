package com.example.mobilemap.map;

import android.location.Location;
import android.os.LocaleList;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mobilemap.R;
import com.example.mobilemap.map.listeners.PoiMoreInfoListener;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Fenêtre d'information d'un marqueur de la carte
 *
 * @author J.Houdé
 */
public class CustomInfoWindow extends InfoWindow {
    public static final int OFFSET_X = 0;
    public static final int OFFSET_Y = -50;
    private final CircleManager circleManager;

    public GeoPoint getPoint() {
        return point;
    }

    private final GeoPoint point;
    private final MainActivity activity;

    /**
     * Fenêtre d'information d'un marqueur d'illustration
     *
     * @param layoutResId identifiant de la ressource graphique
     * @param mapView vue de la carte
     */
    public CustomInfoWindow(int layoutResId, MapView mapView) {
        this(layoutResId, null, mapView, null, null);
    }

    /**
     * Fenêtre d'information d'un marqueur intéractif
     *
     * @param layoutResId identifiant de la ressource graphique
     * @param point coordonnées du marquer associé
     * @param mapView vue de la carte
     * @param circleManager gestionnaire de cercles de filtrage
     * @param activity activité mère
     */
    public CustomInfoWindow(int layoutResId, GeoPoint point, MapView mapView, CircleManager circleManager, MainActivity activity) {
        super(layoutResId, mapView);

        this.point = point;
        this.circleManager = circleManager;
        this.activity = activity;
    }

    @Override
    public void onOpen(Object item) {
        OverlayWithIW overlayItem = (OverlayWithIW) item;

        TextView bubble_title = mView.findViewById(R.id.bubble_title);
        bubble_title.setText(overlayItem.getTitle());

        TextView bubble_description = mView.findViewById(R.id.bubble_description);
        bubble_description.setText(overlayItem.getSnippet());

        ImageButton bubbleInfo = mView.findViewById(R.id.bubble_moreinfo);

        if (activity == null) { // Pour la page de détail du site uniquement
            bubbleInfo.setEnabled(false);
            mView.setOnTouchListener((v, event) -> mView.performClick());
            return;
        }

        bubbleInfo.setOnClickListener(new PoiMoreInfoListener(activity, overlayItem));

        if (circleManager != null && point != null && circleManager.hasSavedCircle()) {
            TextView subDescription = mView.findViewById(R.id.bubble_subdescription);
            subDescription.setVisibility(View.VISIBLE);

            Locale locale = LocaleList.getDefault().get(0);
            subDescription.setText(MessageFormat.format(activity.getResources().getString(R.string.distanceLabel),
                    String.format(locale, "%.2f", getDistanceToCircleCenter())));
        }

    }

    /**
     * Retourne la distance du point par rapport au centre du cercle dessiné
     *
     * @return distance entre le point sélectionné et le centre du cercle dessiné
     */
    private float getDistanceToCircleCenter() {
        GeoPoint center = circleManager.getCircleCenter();
        Location centerLocation = new Location("Center");
        centerLocation.setLatitude(center.getLatitude());
        centerLocation.setLongitude(center.getLongitude());

        Location actualPoint = new Location("Marker");
        actualPoint.setLatitude(point.getLatitude());
        actualPoint.setLongitude(point.getLongitude());

        return centerLocation.distanceTo(actualPoint);
    }

    @Override
    public void onClose() {
        // vide
    }
}
