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
import java.util.Optional;

/**
 * Gestion des circles de filtrage de la carte
 */
public class CircleManager {
    private final MapView mapView;
    private final Activity activity;
    private final MapManager mapManager;
    private Polygon circle;
    private final Map<String, InfoWindow> itemInfoWindowMap;

    /**
     * @param mapView           vue de la map
     * @param activity          activité mère
     * @param mapManager        gestionnaire de la carte
     * @param itemInfoWindowMap map des infoWindow
     */
    public CircleManager(MapView mapView, Activity activity, MapManager mapManager, Map<String, InfoWindow> itemInfoWindowMap) {
        this.mapView = mapView;
        this.activity = activity;
        this.mapManager = mapManager;
        this.itemInfoWindowMap = itemInfoWindowMap;
    }

    /**
     * Retourne le marqueur pour le centre du cercle
     *
     * @return Drawable du marqueur
     */
    public Drawable getCenterMarker() {
        return ResourcesCompat.getDrawable(activity.getResources(), org.osmdroid.library.R.drawable.osm_ic_center_map, activity.getTheme());
    }

    /**
     * Dessine un cercle par rapport à un marqueur de la carte
     *
     * @param centerItem     élément central du cercle
     * @param radiusInMeters rayon du cercle en mètre
     * @param categoryFilter identifiant de la catégorie à afficher
     */
    public void drawCircle(OverlayItem centerItem, double radiusInMeters, long categoryFilter) {
        if (circle != null) {
            removeCircle();
        }

        GeoPoint center = (GeoPoint) centerItem.getPoint();

        // Ajoutez le cercle à la carte
        circle = createCircle(center, radiusInMeters);
        mapView.getOverlayManager().add(circle);

        // Récuperation des sites avec de la catégorie voulue
        List<OverlayItem> filteredItems = mapManager.getOverlayItems(categoryFilter);
        filteredItems.forEach(item -> System.out.println(item.getTitle()));

        updateCenterPointMarker(centerItem, filteredItems); // affiche toujours le point centrale du cercle
        showOnlyPoisInsideCircle(filteredItems, center, radiusInMeters); // affichage des sites à l'intérieur du cercle

        // Sauvegarde les données du circle pour retracer le cercle si l'application est mise en pause
        saveCircleValues(center, radiusInMeters, categoryFilter);
    }

    /**
     * Actualise le marqueur du centre du cercle
     *
     * @param centerItem élément centrale du cercle
     * @param items      liste de sites à afficher
     */
    private void updateCenterPointMarker(OverlayItem centerItem, List<OverlayItem> items) {
        Optional<OverlayItem> item = findOverlayItem(items, centerItem.getPoint());

        item.ifPresent(items::remove); // supprimer le marqueur avec l'image classique s'il est de la catégorie voulue

        OverlayItem circleCenter = item.orElse(centerItem);
        circleCenter.setMarker(getCenterMarker()); // modifier le marqueur au centre du cercle
        items.add(circleCenter);
    }

    /**
     * Retourne l'OverlayItem avec la localisation voulue
     *
     * @param items liste des marqueurs connus
     * @param point localisation voulue
     * @return position de l'overlayItem dans la liste
     */
    private Optional<OverlayItem> findOverlayItem(List<OverlayItem> items, IGeoPoint point) {
        for (int index = 0; index < items.size(); index++) {
            IGeoPoint itemPoint = items.get(index).getPoint();

            if (itemPoint.getLatitude() == point.getLatitude() && itemPoint.getLongitude() == point.getLongitude()) {
                return Optional.ofNullable(items.get(index));
            }
        }

        return Optional.empty();
    }

    /**
     * Dessine un cercle autour de la position actuelle
     *
     * @param userLocation   localisation de l'utilisateur
     * @param radiusInMeters rayon du cercle en mètre
     * @param categoryFilter identifiant de la catégorie à afficher
     */
    public void drawCircleAroundMe(GeoPoint userLocation, double radiusInMeters, long categoryFilter) {
        if (circle != null) {
            removeCircle();
        }

        // Ajoutez le cercle à la carte
        circle = createCircle(userLocation, radiusInMeters);
        mapView.getOverlayManager().add(circle);

        List<OverlayItem> filteredItems = mapManager.getOverlayItems(categoryFilter);

        showOnlyPoisInsideCircle(filteredItems, userLocation, radiusInMeters);

        // Sauvegarde les données du circle pour retracer le cercle si l'application est mise en pause
        saveCircleAroundMeValues(userLocation, radiusInMeters, categoryFilter);
    }

    /**
     * Création du cercle avec le centre et le diamètre voulu
     *
     * @param center point central du cercle
     * @param radiusInMeters rayon du cercle en mètre
     * @return le cercle créé
     */
    private Polygon createCircle(GeoPoint center, double radiusInMeters) {
        Polygon circle = new Polygon();
        circle.setOnClickListener((polygon, mapView1, eventPos) -> false); // le cercle doit transmetre les événements "click" aux autres couches
        // intérieur du cercle
        circle.getFillPaint().setColor(Color.TRANSPARENT);
        // bordure du cercle
        circle.getOutlinePaint().setColor(Color.BLUE);
        circle.getOutlinePaint().setStrokeWidth(2f);

        // Générez les points du périmètre du cercle
        circle.setPoints(Polygon.pointsAsCircle(center, radiusInMeters));

        return circle;
    }

    /**
     * Filtre les sites affichés pour afficher uniquement ceux à l'intérrieur du cercle
     *
     * @param items liste de marqueurs
     * @param center point centrale du cercle
     * @param radiusInMeters rayon du cercle en mètre
     */
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

    /**
     * Vérifie si un point est à l'intérieur du cercle
     *
     * @param item élément à vérifier
     * @param centerLocation localisation de centre du cercle
     * @param radiusInMeters rayon du cercle en mètre
     * @return vrai si le point est à l'intérieur du cercle
     */
    private boolean isInsideCircle(OverlayItem item, Location centerLocation, double radiusInMeters) {
        IGeoPoint markerPosition = item.getPoint();

        Location markerLocation = new Location("Marker");
        markerLocation.setLatitude(markerPosition.getLatitude());
        markerLocation.setLongitude(markerPosition.getLongitude());

        float distance = centerLocation.distanceTo(markerLocation);

        return distance <= radiusInMeters;
    }

    /**
     * Vérifie s'il y a un cercle sauvegardé
     *
     * @return vrai s'il y a un cercle sauvegardé dans les préférences
     */
    public boolean hasSavedCircle() {
        String circleRadius = mapManager.getSharedPreferences()
                .getString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING);

        return !circleRadius.isEmpty();
    }

    /**
     * Enregistrement des données du cercle autour de la position actuelle
     *
     * @param center point central du cercle
     * @param radiusInMeters rayon du cercle en mètre
     * @param categoryFilter identifiant de la catégorie à afficher
     */
    private void saveCircleAroundMeValues(IGeoPoint center, double radiusInMeters, long categoryFilter) {
        saveCircleValues(center, radiusInMeters, categoryFilter);
        SharedPreferences.Editor edit = mapManager.getSharedPreferences().edit();
        edit.putBoolean(SharedPreferencesConstant.CIRCLE_IS_AROUND_ME, true);
        edit.apply();

    }

    /**
     * Enregistrement des données du cercle autour d'un marqueur
     *
     * @param center point central du cercle
     * @param radiusInMeters rayon du cercle en mètre
     * @param categoryFilter identifiant de la catégorie à afficher
     */
    private void saveCircleValues(IGeoPoint center, double radiusInMeters, long categoryFilter) {
        SharedPreferences.Editor edit = mapManager.getSharedPreferences().edit();
        edit.putString(SharedPreferencesConstant.CIRCLE_LATITUDE_STRING, String.valueOf(center.getLatitude()));
        edit.putString(SharedPreferencesConstant.CIRCLE_LONGITUDE_STRING, String.valueOf(center.getLongitude()));
        edit.putString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, String.valueOf(radiusInMeters));
        edit.putLong(SharedPreferencesConstant.CIRCLE_CATEGORY_FILTER, categoryFilter);
        edit.apply();
    }

    /**
     * Supprimer le cercle affiché sur la carte
     */
    public void removeCircle() {
        if (circle != null) {
            mapView.getOverlayManager().remove(circle);
            circle = null;
        }

        // Supprimer les données du cercle
        deleteSavedSettings();

        mapManager.updateMarkers();
    }

    /**
     * Supprimer les données sauvegardées du cercle
     */
    private void deleteSavedSettings() {
        SharedPreferences.Editor edit = mapManager.getSharedPreferences().edit();
        edit.putString(SharedPreferencesConstant.CIRCLE_LATITUDE_STRING, SharedPreferencesConstant.EMPTY_STRING);
        edit.putString(SharedPreferencesConstant.CIRCLE_LONGITUDE_STRING, SharedPreferencesConstant.EMPTY_STRING);
        edit.putString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING);
        edit.putBoolean(SharedPreferencesConstant.CIRCLE_IS_AROUND_ME, false);
        edit.apply();
    }

    /**
     * Restauration du cercle précédent
     */
    public void restorePreviousCircle() {
        if (!hasSavedCircle()) {
            return;
        }

        SharedPreferences sharedPreferences = mapManager.getSharedPreferences();

        // récupérer le centre du cercle
        GeoPoint center = getCircleCenter();

        String circleRadiusString = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING);
        double circleRadius = Double.parseDouble(circleRadiusString);

        long categoryFilter = sharedPreferences.getLong(SharedPreferencesConstant.CIRCLE_CATEGORY_FILTER, SharedPreferencesConstant.NOT_FOUND_ID);

        if (mapManager.isCircleAroundMe()) { // restaurer le cercle autour de l'utilisateur
            drawCircleAroundMe(center, circleRadius, categoryFilter);
            return;
        }

        OverlayItem item = findItem(center);

        if (item == null) {
            return;
        }

        mapManager.getMarkerGestureListener().setLastCircleCenterItemUid(center);
        drawCircle(item, circleRadius, categoryFilter); // restaurer le cercle autour d'un site
    }

    /**
     * Retourne le centre du cercle
     *
     * @return point central du cercle
     */
    public GeoPoint getCircleCenter() {
        SharedPreferences sharedPreferences = mapManager.getSharedPreferences();

        String circleLatitude = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_LATITUDE_STRING, SharedPreferencesConstant.DEFAULT_POSITION_STRING);
        String circleLongitude = sharedPreferences.getString(SharedPreferencesConstant.CIRCLE_LONGITUDE_STRING, SharedPreferencesConstant.DEFAULT_POSITION_STRING);

        return new GeoPoint(Double.parseDouble(circleLatitude), Double.parseDouble(circleLongitude));
    }

    /**
     * Retourne l'overlayItem avec la localisation voulue
     *
     * @param center point central du cercle
     * @return overlayItem avec la localisation voulue
     */
    private OverlayItem findItem(IGeoPoint center) {
        return mapManager.getOverlayItems().stream()
                .filter(item -> item.getPoint().getLatitude() == center.getLatitude() && item.getPoint().getLongitude() == center.getLongitude())
                .findFirst().orElse(null);
    }
}
