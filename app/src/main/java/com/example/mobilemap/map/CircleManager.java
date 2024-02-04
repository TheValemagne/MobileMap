package com.example.mobilemap.map;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CircleManager {
    private final MapView mapView;
    private final MapManager mapManager;
    private Polygon circle;
    private final static int MAX_ANGLE_IN_DEGREE = 360;
    private final InfoWindow infoWindow;

    public CircleManager(MapView mapView, MapManager mapManager) {
        this.mapView = mapView;
        this.mapManager = mapManager;

        infoWindow = new CustomInfoWindow(org.osmdroid.library.R.layout.bonuspack_bubble, mapView);
    }

    public void drawCircle(int index, double radiusInMeters, long categoryFilter) {
        if (circle != null) {
            removeCircle();
        }

        OverlayItem centerItem = mapManager.getOverlayItemItemizedOverlay().getItem(index);
        // centerItem.setMarker(ResourcesCompat.getDrawable(activity.getResources(), org.osmdroid.library.R.drawable.marker_default, activity.getTheme()));
        //mapManager.showInfoWindow(centerItem, infoWindow);

        IGeoPoint center = centerItem.getPoint();

        // Ajoutez le cercle à la carte
        circle = createCircle(center, radiusInMeters);
        mapView.getOverlayManager().add(circle);

        List<OverlayItem> filteredItems = mapManager.getOverlayItems(categoryFilter);

        if(!filteredItems.contains(centerItem)) { // affiche toujours le point centrale du cercle
            filteredItems.add(centerItem);
        }

        showOnlyPoisInsideCircle(filteredItems, center, radiusInMeters);

        // Sauvegarde les données du circle pour retracer le cercle si l'application est mise en pause
        saveCircleValues(index, radiusInMeters, categoryFilter, center);
    }

    private Polygon createCircle(IGeoPoint center, double radiusInMeters) {
        Polygon circle = new Polygon();
        // intérieur du cercle
        circle.getFillPaint().setColor(Color.TRANSPARENT);
        // bordure du cercle
        circle.getOutlinePaint().setColor(Color.BLUE);
        circle.getOutlinePaint().setStrokeWidth(2f);

        // Générez les points du périmètre du cercle
        List<GeoPoint> circlePoints = generateCirclePerimeterPoints(center, radiusInMeters);
        circle.setPoints(circlePoints);

        return circle;
    }

    private void saveCircleValues(int index, double radiusInMeters, long categoryFilter, IGeoPoint center) {
        final SharedPreferences.Editor edit = mapManager.getSharedPreferences().edit();
        edit.putString(SharedPreferencesConstant.CIRCLE_LATITUDE_STRING, String.valueOf(center.getLatitude()));
        edit.putString(SharedPreferencesConstant.CIRCLE_LONGITUDE_STRING, String.valueOf(center.getLongitude()));
        edit.putString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, String.valueOf(radiusInMeters));
        edit.putLong(SharedPreferencesConstant.CIRCLE_CATEGORY_FILTER, categoryFilter);
        edit.putInt(SharedPreferencesConstant.CIRCLE_ITEM_INDEX, index);
        edit.apply();
    }

    public void removeCircle() {
        if (circle != null) {
            mapView.getOverlayManager().remove(circle);
            circle = null;
        }

        if (infoWindow.isOpen()) {
            infoWindow.close();
        }

        // Supprimer les données du cercle
        deleteSavedSettings();

        mapManager.updateMarkers();
    }

    private void deleteSavedSettings() {
        final SharedPreferences.Editor edit = mapManager.getSharedPreferences().edit();
        edit.putString(SharedPreferencesConstant.CIRCLE_LATITUDE_STRING, SharedPreferencesConstant.EMPTY_STRING);
        edit.putString(SharedPreferencesConstant.CIRCLE_LONGITUDE_STRING, SharedPreferencesConstant.EMPTY_STRING);
        edit.putString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING);
        edit.apply();
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

        Location centerLocation = new Location("Center");
        centerLocation.setLatitude(center.getLatitude());
        centerLocation.setLongitude(center.getLongitude());

        List<OverlayItem> itemsInsideCircle = items.stream()
                .filter(item -> isInsideCircle(item, centerLocation, radiusInMeters))
                .collect(Collectors.toList());

        mapManager.getOverlayItemItemizedOverlay().addItems(itemsInsideCircle);
        mapView.invalidate();
    }

    private boolean isInsideCircle(OverlayItem item, Location centerLocation, double radiusInMeters) {
        GeoPoint markerPosition = (GeoPoint) item.getPoint();

        Location markerLocation = new Location("Marker");
        markerLocation.setLatitude(markerPosition.getLatitude());
        markerLocation.setLongitude(markerPosition.getLongitude());

        float distance = centerLocation.distanceTo(markerLocation);

        return distance <= radiusInMeters;
    }

    public boolean hasSavedCircle() {
        SharedPreferences sharedPreferences = mapManager.getSharedPreferences();

        String circleRadius = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING);
        int itemIndex = sharedPreferences.getInt(SharedPreferencesConstant.CIRCLE_ITEM_INDEX, SharedPreferencesConstant.NOT_FOUND_ID);

        return !circleRadius.isEmpty() && itemIndex != -1;
    }

    public void restorePreviousCircle() {
        if(!hasSavedCircle()) {
            return;
        }

        SharedPreferences sharedPreferences = mapManager.getSharedPreferences();

        // récupérer le centre du cercle
        String circleLatitude = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_LATITUDE_STRING, SharedPreferencesConstant.DEFAULT_POSITION_STRING);
        String circleLongitude = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_LONGITUDE_STRING, SharedPreferencesConstant.DEFAULT_POSITION_STRING);
        IGeoPoint center = new GeoPoint(Double.parseDouble(circleLatitude), Double.parseDouble(circleLongitude));

        String circleRadius = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING);
        int itemIndex = sharedPreferences.getInt(SharedPreferencesConstant.CIRCLE_ITEM_INDEX, SharedPreferencesConstant.NOT_FOUND_ID);
        long categoryFilter = sharedPreferences.getLong(SharedPreferencesConstant.CIRCLE_CATEGORY_FILTER, SharedPreferencesConstant.NOT_FOUND_ID);

        drawCircle(itemIndex, Double.parseDouble(circleRadius), categoryFilter);
        mapManager.getMarkerGestureListener().setLastCircleCenterItemUid(center);
    }
}
