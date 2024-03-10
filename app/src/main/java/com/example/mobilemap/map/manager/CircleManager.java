package com.example.mobilemap.map.manager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.core.content.res.ResourcesCompat;

import com.example.mobilemap.map.PoiInfoWindow;
import com.example.mobilemap.map.SharedPreferencesConstant;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Gestion des circles de filtrage de la carte
 *
 * @author J.Houdé
 */
public class CircleManager {
    private final MapView mapView;
    private final Activity activity;
    private final MapManager mapManager;
    private Polygon circle;
    private final Map<String, PoiInfoWindow> infoWindowMap;
    private final Marker lastUserLocation;

    /**
     * Gestion des circles de filtrage de la carte
     *
     * @param mapView           vue de la map
     * @param activity          activité principale
     * @param mapManager        gestionnaire de la carte
     * @param infoWindowMap map des infoWindow
     */
    public CircleManager(MapView mapView, Activity activity, MapManager mapManager, Map<String, PoiInfoWindow> infoWindowMap) {
        this.mapView = mapView;
        this.activity = activity;
        this.mapManager = mapManager;
        this.infoWindowMap = infoWindowMap;

        this.lastUserLocation = initMarker(mapView, activity);
    }

    /**
     * Initialisation du marqueur pour la dernière localisation de l'utilisateur
     * @param mapView vue de la map
     * @param activity activité principale
     * @return le marqueur de position
     */
    private Marker initMarker(MapView mapView, Activity activity) {
        Marker marker = new Marker(mapView, activity);

        marker.setAnchor(.5f, .8125f);
        marker.setInfoWindow(null);
        marker.setIcon(ResourcesCompat.getDrawable(activity.getResources(),
                org.osmdroid.library.R.drawable.person, activity.getTheme()));

        return marker;
    }

    /**
     * Retourne le drawable pour le centre du cercle
     *
     * @return Drawable du marqueur
     */
    public Drawable getCenterPointDrawable() {
        return ResourcesCompat.getDrawable(activity.getResources(),
                org.osmdroid.library.R.drawable.osm_ic_center_map, activity.getTheme());
    }

    /**
     * Dessine un cercle par rapport à un marqueur de la carte
     *
     * @param centerItem     élément central du cercle
     * @param radiusInMeters rayon du cercle en mètre
     * @param categoryFilter identifiant de la catégorie à afficher
     */
    public void drawCircle(OverlayItem centerItem, double radiusInMeters, long categoryFilter) {
        if (circle != null) { // suppression du cercle actuellement affiché
            removeCircle();
        }

        GeoPoint center = (GeoPoint) centerItem.getPoint();

        // Ajoutez le cercle à la carte
        circle = createCircle(center, radiusInMeters);
        mapView.getOverlays()
                .add(mapView.getOverlays().indexOf(mapManager.getItemizedOverlay()), circle); // insertion du cercle derrière les marqueurs

        // Récuperation des sites avec de la catégorie voulue
        List<OverlayItem> filteredItems = mapManager.getOverlayItems(categoryFilter);

        updateCenterPointMarker(centerItem, filteredItems); // affiche toujours le point centrale du cercle
        showOnlyPoisInsideCircle(filteredItems, center, radiusInMeters); // affichage des sites à l'intérieur du cercle

        // Sauvegarde les données du circle pour retracer le cercle si l'application est mise en pause
        saveCircleValues(center, radiusInMeters, categoryFilter).apply();
        mapManager.updateOpenedInfoWindows();
    }

    /**
     * Actualise le marqueur du centre du cercle
     *
     * @param centerItem élément centrale du cercle
     * @param items      liste de sites à afficher
     */
    private void updateCenterPointMarker(OverlayItem centerItem, List<OverlayItem> items) {
        Optional<OverlayItem> item = items.stream()
                .filter(overlayItem -> overlayItem.getUid().equals(centerItem.getUid()))
                .findFirst();

        item.ifPresent(items::remove); // supprimer le marqueur avec l'image classique s'il est de la catégorie voulue

        OverlayItem circleCenter = item.orElse(centerItem);
        circleCenter.setMarker(getCenterPointDrawable()); // modifier le marqueur au centre du cercle
        circleCenter.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER); // centrage du marqueur avec la position sur la carte
        items.add(circleCenter); // ajout du marqueur du centre du cercle à la liste des marqueurs à afficher
    }

    /**
     * Dessine un cercle autour de la position actuelle
     *
     * @param userLocation   localisation de l'utilisateur
     * @param radiusInMeters rayon du cercle en mètre
     * @param categoryFilter identifiant de la catégorie à afficher
     */
    public void drawCircleAroundMe(GeoPoint userLocation, double radiusInMeters, long categoryFilter) {
        if (circle != null) { // suppression du cercle actuellement affiché
            removeCircle();
        }

        // Ajoutez le cercle à la carte
        circle = createCircle(userLocation, radiusInMeters);
        mapView.getOverlays()
                .add(mapView.getOverlays().indexOf(mapManager.getItemizedOverlay()), circle); // insertion du cercle derrière les marqueurs

        List<OverlayItem> filteredItems = mapManager.getOverlayItems(categoryFilter);

        showOnlyPoisInsideCircle(filteredItems, userLocation, radiusInMeters);

        // Sauvegarde les données du circle pour retracer le cercle si l'application est mise en pause
        saveCircleAroundMeValues(userLocation, radiusInMeters, categoryFilter).apply();
        mapManager.updateOpenedInfoWindows();
    }

    /**
     * Création du cercle avec le centre et le diamètre voulu
     *
     * @param center         point central du cercle
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
     * Filtre les sites affichés pour afficher uniquement ceux à l'interrieur du cercle
     *
     * @param items          liste de marqueurs
     * @param center         point centrale du cercle
     * @param radiusInMeters rayon du cercle en mètre
     */
    private void showOnlyPoisInsideCircle(List<OverlayItem> items, IGeoPoint center, double radiusInMeters) {
        mapManager.getItemizedOverlay().removeAllItems(); // supprime tous les marqueurs de sites affichés

        Location centerLocation = new Location("Center");
        centerLocation.setLatitude(center.getLatitude());
        centerLocation.setLongitude(center.getLongitude());

        List<OverlayItem> itemsInsideCircle = new ArrayList<>();

        for (int index = 0; index < items.size(); index++) {
            OverlayItem item = items.get(index);

            if (isInsideCircle(item, centerLocation, radiusInMeters)) { // ajout des marqueurs contenus dans le cercle
                itemsInsideCircle.add(item);
            } else if (infoWindowMap.containsKey(item.getUid())) { // fermeture des infoWindow des marqueurs à retirer
                Objects.requireNonNull(infoWindowMap.get(item.getUid())).close();
            }
        }

        mapManager.getItemizedOverlay().addItems(itemsInsideCircle); // ajout des marqueurs respectant le filtre
        mapView.invalidate();
    }

    /**
     * Vérifie si un point est à l'intérieur du cercle
     *
     * @param item           élément à vérifier
     * @param centerLocation localisation de centre du cercle
     * @param radiusInMeters rayon du cercle en mètre
     * @return vrai si le point est à l'intérieur du cercle
     */
    private boolean isInsideCircle(OverlayItem item, Location centerLocation, double radiusInMeters) {
        IGeoPoint markerPosition = item.getPoint();

        Location markerLocation = new Location("Marker");
        markerLocation.setLatitude(markerPosition.getLatitude());
        markerLocation.setLongitude(markerPosition.getLongitude());

        float distanceToCircleCenter = centerLocation.distanceTo(markerLocation); // distance entre le centre du cercle et le point actuel en metre

        return distanceToCircleCenter <= radiusInMeters;
    }

    /**
     * Vérifie si un cercle a été sauvegardé dans les préférences
     *
     * @return vrai s'il y a un cercle sauvegardé dans les préférences
     */
    public boolean hasSavedCircle() {
        return !mapManager.getSharedPreferences()
                .getString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING)
                .trim().isEmpty();
    }

    /**
     * Création de l'enregistrement des données du cercle autour de la position actuelle
     *
     * @param center         point central du cercle
     * @param radiusInMeters rayon du cercle en mètre
     * @param categoryFilter identifiant de la catégorie à afficher
     * @return SharedPreferences.Editor
     */
    private SharedPreferences.Editor saveCircleAroundMeValues(IGeoPoint center, double radiusInMeters, long categoryFilter) {
        return saveCircleValues(center, radiusInMeters, categoryFilter)
                .putBoolean(SharedPreferencesConstant.CIRCLE_IS_AROUND_ME, true);
    }

    /**
     * Création de l'enregistrement des données du cercle autour d'un marqueur
     *
     * @param center         point central du cercle
     * @param radiusInMeters rayon du cercle en mètre
     * @param categoryFilter identifiant de la catégorie à afficher
     * @return SharedPreferences.Editor
     */
    private SharedPreferences.Editor saveCircleValues(IGeoPoint center, double radiusInMeters, long categoryFilter) {
        return mapManager.getSharedPreferences().edit()
                .putString(SharedPreferencesConstant.CIRCLE_LATITUDE_STRING, String.valueOf(center.getLatitude()))
                .putString(SharedPreferencesConstant.CIRCLE_LONGITUDE_STRING, String.valueOf(center.getLongitude()))
                .putString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, String.valueOf(radiusInMeters))
                .putLong(SharedPreferencesConstant.CIRCLE_CATEGORY_FILTER, categoryFilter);
    }

    /**
     * Supprimer le cercle affiché sur la carte
     */
    public void removeCircle() {
        mapView.getOverlayManager().remove(circle); // suppression du cercle affiché sur la carte
        circle = null;
        mapView.getOverlayManager().remove(lastUserLocation); // suppression de la dernière position enregistrée si affichée

        // Supprimer les données enregistrées du cercle
        deleteSavedSettings();

        mapManager.updateMap(); // actualisation de la carte
    }

    /**
     * Supprimer les données sauvegardées du cercle
     */
    private void deleteSavedSettings() {
        mapManager.getSharedPreferences().edit()
                .putString(SharedPreferencesConstant.CIRCLE_LATITUDE_STRING, SharedPreferencesConstant.EMPTY_STRING)
                .putString(SharedPreferencesConstant.CIRCLE_LONGITUDE_STRING, SharedPreferencesConstant.EMPTY_STRING)
                .putString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING)
                .putBoolean(SharedPreferencesConstant.CIRCLE_IS_AROUND_ME, false)
                .apply();
    }

    /**
     * Restauration du cercle précédent
     */
    public void restorePreviousCircle() {
        if (!hasSavedCircle()) {
            return;
        }

        // récupérer le centre du cercle
        GeoPoint center = getCircleCenter();
        double circleRadius = getCircleRadius();
        long categoryFilter = getCategoryFilter();

        if (mapManager.isCircleAroundMe()) { // restaurer le cercle autour de l'utilisateur
            lastUserLocation.setPosition(center); // affichage de la dernière position enregistrée en lien avec le cercle tracé
            int index = mapView.getOverlays().indexOf(mapManager.getItemizedOverlay()); // insertion du marqueur derrière les marqueurs
            mapView.getOverlays().add(index, lastUserLocation);

            drawCircleAroundMe(center, circleRadius, categoryFilter);
            return;
        }

        Optional<OverlayItem> item = mapManager.findItem(center);

        item.ifPresent(overlayItem -> {
            mapManager.getMarkerGestureListener().setCurrentCircleCenterItemUid(overlayItem.getUid());
            drawCircle(overlayItem, circleRadius, categoryFilter); // restaurer le cercle autour d'un site
        });

    }

    /**
     * Retourne la catégorie de filtrage pour le cercle
     *
     * @return retourne la catégorie de filtrage s'il existe un cercle sur la carte, sinon retourne -1
     */
    public long getCategoryFilter() {
        return mapManager.getSharedPreferences()
                .getLong(SharedPreferencesConstant.CIRCLE_CATEGORY_FILTER, SharedPreferencesConstant.NOT_FOUND_ID);
    }

    /**
     * Retourne le rayon du cercle en mètre
     *
     * @return retourne le rayon du cercle si définie, sinon retourne 0
     */
    public double getCircleRadius() {
        String value = mapManager.getSharedPreferences()
                .getString(SharedPreferencesConstant.CIRCLE_RADIUS_STRING, SharedPreferencesConstant.EMPTY_STRING);

        if (value.trim().isEmpty()) {
            return 0.0;
        }

        return Double.parseDouble(value);
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

        return new GeoPoint(Double.parseDouble(circleLatitude.trim()), Double.parseDouble(circleLongitude.trim()));
    }
}
