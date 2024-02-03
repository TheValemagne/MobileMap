package com.example.mobilemap.map;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;

import androidx.core.content.res.ResourcesCompat;

import com.example.mobilemap.MainActivity;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.List;

public class CircleManager {
    private final MapView mapView;
    private final MapManager mapManager;
    private final MainActivity activity;
    private Polygon circleOverlay;
    private final static int MAX_ANGLE_IN_DEGREE = 360;
    private final InfoWindow infoWindow;

    // Pour le stockage le circle actuellement affiché
    private static final String CIRCLE_LATITUDE_STRING = "circleLatitudeString";
    private static final String CIRCLE_LONGITUDE_STRING = "circleLongitudeString";
    private static final String CIRCLE_ITEM_INDEX = "circleItemIndex";
    private static final String CIRCLE_RADIUS_STRING = "cicleRadiusString";
    private static final String CIRCLE_CATEGORY_FILTER = "cicleCategoryFilter";

    public CircleManager(MapView mapView, MainActivity activity, MapManager mapManager) {
        this.mapView = mapView;
        this.activity = activity;
        this.mapManager = mapManager;
        infoWindow = new CustomInfoWindow(org.osmdroid.library.R.layout.bonuspack_bubble, mapView);
    }

    public void drawCircle(int index, double radiusInMeters, long categoryFilter) {
        if (circleOverlay != null) {
            removeCircle();
        }

        OverlayItem centerItem = mapManager.getOverlayItemItemizedOverlay().getItem(index);
        centerItem.setMarker(ResourcesCompat.getDrawable(activity.getResources(), org.osmdroid.library.R.drawable.marker_default, activity.getTheme()));
        //mapManager.showInfoWindow(centerItem, infoWindow);

        IGeoPoint center = centerItem.getPoint();

        // Générez les points du périmètre du cercle
        List<GeoPoint> circlePoints = generateCirclePerimeterPoints(center, radiusInMeters);
        // Ajoutez le cercle à la carte
        circleOverlay = createCircle(circlePoints);
        mapView.getOverlayManager().add(circleOverlay);

        List<OverlayItem> filteredItems = mapManager.getOverlayItems(categoryFilter);

        if(!filteredItems.contains(centerItem)) { // affiche toujour le point centrale du cercle
            filteredItems.add(centerItem);
        }

        showOnlyPoisInsideCircle(filteredItems, center, radiusInMeters);

        // Sauvegarde les données du circle pour retracer le cercle si l'application est mise en pause
        saveCircleValues(index, radiusInMeters, categoryFilter, center);
    }

    private Polygon createCircle(List<GeoPoint> circlePoints) {
        Polygon circleOverlay = new Polygon();
        // intérieur du cercle
        circleOverlay.getFillPaint().setColor(Color.TRANSPARENT);
        // bordure du cercle
        circleOverlay.getOutlinePaint().setColor(Color.BLUE);
        circleOverlay.getOutlinePaint().setStrokeWidth(2f);

        circleOverlay.setPoints(circlePoints);

        return circleOverlay;
    }

    private void saveCircleValues(int index, double radiusInMeters, long categoryFilter, IGeoPoint center) {
        final SharedPreferences.Editor edit = mapManager.getSharedPreferences().edit();
        edit.putString(CIRCLE_LATITUDE_STRING, String.valueOf(center.getLatitude()));
        edit.putString(CIRCLE_LONGITUDE_STRING, String.valueOf(center.getLongitude()));
        edit.putString(CIRCLE_RADIUS_STRING, String.valueOf(radiusInMeters));
        edit.putLong(CIRCLE_CATEGORY_FILTER, categoryFilter);
        edit.putInt(CIRCLE_ITEM_INDEX, index);
        edit.apply();
    }

    public void removeCircle() {
        if (circleOverlay != null) {
            mapView.getOverlayManager().remove(circleOverlay);
            circleOverlay = null;
        }

        if (infoWindow.isOpen()) {
            infoWindow.close();
        }

        // Supprimer les données du cercle
        final SharedPreferences.Editor edit = mapManager.getSharedPreferences().edit();
        edit.putString(CIRCLE_LATITUDE_STRING, "");
        edit.putString(CIRCLE_LONGITUDE_STRING, "");
        edit.putString(CIRCLE_RADIUS_STRING, "");
        edit.apply();

        mapManager.updateMarkers();
    }

    private List<GeoPoint> generateCirclePerimeterPoints(IGeoPoint center, double radiusInMeters) {
        List<GeoPoint> points = new ArrayList<>();

        for (int angleInDegree = 0; angleInDegree < MAX_ANGLE_IN_DEGREE; angleInDegree += 5) {
            double angleInRadians = Math.toRadians(angleInDegree);

            double latitude = center.getLatitude()
                    + (radiusInMeters / 111300) * Math.sin(angleInRadians);
            double longitude = center.getLongitude()
                    + (radiusInMeters / (111300 * Math.cos(Math.toRadians(center.getLatitude())))) * Math.cos(angleInRadians);

            points.add(new GeoPoint(latitude, longitude)); // point au périmètre du cercle
        }
        return points;
    }

    private void showOnlyPoisInsideCircle(List<OverlayItem> items, IGeoPoint center, double radiusInMeters) {
        mapManager.getOverlayItemItemizedOverlay().removeAllItems(); // supprime tous les marqueurs de sites affichés

        ArrayList<OverlayItem> overlayItems = new ArrayList<>();

        Location centerLocation = new Location("Center");
        centerLocation.setLatitude(center.getLatitude());
        centerLocation.setLongitude(center.getLongitude());

        for (OverlayItem item : items) {
            GeoPoint markerPosition = (GeoPoint) item.getPoint();

            Location markerLocation = new Location("Marker");
            markerLocation.setLatitude(markerPosition.getLatitude());
            markerLocation.setLongitude(markerPosition.getLongitude());

            float distance = centerLocation.distanceTo(markerLocation);
            if (distance <= radiusInMeters) {
                overlayItems.add(item);
            }
        }

        mapManager.getOverlayItemItemizedOverlay().addItems(overlayItems);
        mapView.invalidate();
    }

    public void restorePreviousCircle() {
        SharedPreferences sharedPreferences = mapManager.getSharedPreferences();

        String circleRadius = sharedPreferences.getString(CIRCLE_RADIUS_STRING, "");
        int itemIndex = sharedPreferences.getInt(CIRCLE_ITEM_INDEX, -1);

        if(circleRadius.isEmpty() || itemIndex == -1) {
            return;
        }
        String circleLatitude = sharedPreferences.getString(CIRCLE_LATITUDE_STRING, "0.0");
        String circleLongitude = sharedPreferences.getString(CIRCLE_LONGITUDE_STRING, "0.0");
        IGeoPoint center = new GeoPoint(Double.parseDouble(circleLatitude), Double.parseDouble(circleLongitude));

        long categoryFilter = sharedPreferences.getLong(CIRCLE_CATEGORY_FILTER, -1);

        drawCircle(itemIndex, Double.parseDouble(circleRadius), categoryFilter);
        mapManager.getMarkerGertureListener().setLastCircleCenterItemUid(center);
    }
}
