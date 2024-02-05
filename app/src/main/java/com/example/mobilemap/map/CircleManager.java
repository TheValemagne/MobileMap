package com.example.mobilemap.map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.core.content.res.ResourcesCompat;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CircleManager {
    private final MapView mapView;
    private final Activity activity;
    private final MapManager mapManager;
    private Polygon circle;
    private final static int MAX_ANGLE_IN_DEGREE = 360;
    private final Map<String, InfoWindow> itemInfoWindowMap;

    public CircleManager(MapView mapView, Activity activity, MapManager mapManager, Map<String, InfoWindow> itemInfoWindowMap) {
        this.mapView = mapView;
        this.activity = activity;
        this.mapManager = mapManager;
        this.itemInfoWindowMap = itemInfoWindowMap;
    }

    public Drawable getCenterMarker() {
        return ResourcesCompat.getDrawable(activity.getResources(), org.osmdroid.library.R.drawable.osm_ic_center_map, activity.getTheme());
    }

    public void drawCircle(OverlayItem centerItem, double radiusInMeters, long categoryFilter) {
        if (circle != null) {
            removeCircle();
        }

        IGeoPoint center = centerItem.getPoint();

        // Ajoutez le cercle à la carte
        circle = createCircle(center, radiusInMeters);
        mapView.getOverlayManager().add(circle);

        List<OverlayItem> filteredItems = mapManager.getOverlayItems(categoryFilter);
        filteredItems.forEach(item -> System.out.println(item.getTitle()));

        updateCenterPointMarker(centerItem, center, filteredItems); // affiche toujours le point centrale du cercle

        showOnlyPoisInsideCircle(filteredItems, center, radiusInMeters);

        // Sauvegarde les données du circle pour retracer le cercle si l'application est mise en pause
        saveCircleValues(radiusInMeters, categoryFilter, center);
    }

    private void updateCenterPointMarker(OverlayItem centerItem, IGeoPoint center, List<OverlayItem> filteredItems) {
        centerItem.setMarker(getCenterMarker());

        if (filteredItems.stream().noneMatch(item -> item.getPoint().equals(centerItem.getPoint()))) {
            filteredItems.add(centerItem);
            return;
        }

        int index = findOverlayItemIndex(filteredItems, center);
        if (index != -1) {
            filteredItems.remove(index);
            filteredItems.add(centerItem);
        }
    }

    private int findOverlayItemIndex(List<OverlayItem> overlayItems, IGeoPoint point) {
        for (int index = 0; index < overlayItems.size(); index++) {
            IGeoPoint itemPoint = overlayItems.get(index).getPoint();

            if (itemPoint.getLatitude() == point.getLatitude() && itemPoint.getLongitude() == point.getLongitude()) {
                return index;
            }
        }

        return -1;
    }

    public void drawCircleAroundMe(IGeoPoint myLocation, double radiusInMeters, long categoryFilter) {
        if (circle != null) {
            removeCircle();
        }

        // Ajoutez le cercle à la carte
        circle = createCircle(myLocation, radiusInMeters);
        mapView.getOverlayManager().add(circle);

        List<OverlayItem> filteredItems = mapManager.getOverlayItems(categoryFilter);

        showOnlyPoisInsideCircle(filteredItems, myLocation, radiusInMeters);

        // Sauvegarde les données du circle pour retracer le cercle si l'application est mise en pause
        saveCircleAroundMeValues(radiusInMeters, categoryFilter, myLocation);
    }

    private Polygon createCircle(IGeoPoint center, double radiusInMeters) {
        Polygon circle = new Polygon();
        circle.setOnClickListener((polygon, mapView1, eventPos) -> false); // le cercle doit transmetre les événements "click" aux autres couches
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

        List<OverlayItem> itemsInsideCircle = new ArrayList<>();

        for (int index = 0; index < items.size(); index++) {
            OverlayItem item = items.get(index);

            if (isInsideCircle(item, centerLocation, radiusInMeters)) {
                itemsInsideCircle.add(item);
            } else if (itemInfoWindowMap.containsKey(MapManager.getItemUid(item.getPoint()))) {
                Objects.requireNonNull(itemInfoWindowMap.get(MapManager.getItemUid(item.getPoint()))).close();
            }
        }

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
        String circleRadius = mapManager.getSharedPreferences()
                .getString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING);

        return !circleRadius.isEmpty();
    }

    private void saveCircleAroundMeValues(double radiusInMeters, long categoryFilter, IGeoPoint center) {
        saveCircleValues(radiusInMeters, categoryFilter, center);
        final SharedPreferences.Editor edit = mapManager.getSharedPreferences().edit();
        edit.putBoolean(SharedPreferencesConstant.CIRCLE_IS_AROUND_ME, true);
        edit.apply();

    }

    private void saveCircleValues(double radiusInMeters, long categoryFilter, IGeoPoint center) {
        final SharedPreferences.Editor edit = mapManager.getSharedPreferences().edit();
        edit.putString(SharedPreferencesConstant.CIRCLE_LATITUDE_STRING, String.valueOf(center.getLatitude()));
        edit.putString(SharedPreferencesConstant.CIRCLE_LONGITUDE_STRING, String.valueOf(center.getLongitude()));
        edit.putString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, String.valueOf(radiusInMeters));
        edit.putLong(SharedPreferencesConstant.CIRCLE_CATEGORY_FILTER, categoryFilter);
        edit.apply();
    }

    public void removeCircle() {
        if (circle != null) {
            mapView.getOverlayManager().remove(circle);
            circle = null;
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
        edit.putBoolean(SharedPreferencesConstant.CIRCLE_IS_AROUND_ME, false);
        edit.apply();
    }

    public void restorePreviousCircle() {
        if (!hasSavedCircle()) {
            return;
        }

        SharedPreferences sharedPreferences = mapManager.getSharedPreferences();

        // récupérer le centre du cercle
        String circleLatitude = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_LATITUDE_STRING, SharedPreferencesConstant.DEFAULT_POSITION_STRING);
        String circleLongitude = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_LONGITUDE_STRING, SharedPreferencesConstant.DEFAULT_POSITION_STRING);
        IGeoPoint center = new GeoPoint(Double.parseDouble(circleLatitude), Double.parseDouble(circleLongitude));

        String circleRadiusString = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING);
        double circleRadius = Double.parseDouble(circleRadiusString);

        long categoryFilter = sharedPreferences.getLong(SharedPreferencesConstant.CIRCLE_CATEGORY_FILTER, SharedPreferencesConstant.NOT_FOUND_ID);

        if (mapManager.isCircleAroundMe()) {
            drawCircleAroundMe(center, circleRadius, categoryFilter);
            return;
        }

        OverlayItem item = findItem(center);

        if (item == null) {
            return;
        }

        mapManager.getMarkerGestureListener().setLastCircleCenterItemUid(center);
        drawCircle(item, circleRadius, categoryFilter);
    }

    private OverlayItem findItem(IGeoPoint center) {
        return mapManager.getOverlayItems().stream()
                .filter(item -> item.getPoint().getLatitude() == center.getLatitude() && item.getPoint().getLongitude() == center.getLongitude())
                .findFirst().orElse(null);
    }
}
