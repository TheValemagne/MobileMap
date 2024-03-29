package com.example.mobilemap.map.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;

import androidx.preference.PreferenceManager;

import com.example.mobilemap.map.AddCircleAroundPoiDialogBuilder;
import com.example.mobilemap.map.PoiInfoWindow;
import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.map.SharedPreferencesConstant;
import com.example.mobilemap.map.overlays.initiators.CompassOverlayInitiator;
import com.example.mobilemap.map.overlays.initiators.ItemizedIconOverlayInitiator;
import com.example.mobilemap.map.overlays.initiators.LocationOverlayInitiator;
import com.example.mobilemap.map.overlays.initiators.RotationOverlayInitiator;
import com.example.mobilemap.map.overlays.initiators.ScaleBarInitiator;
import com.example.mobilemap.map.listeners.MarkerGestureListener;
import com.example.mobilemap.map.overlays.AddMarkerOverlay;
import com.example.mobilemap.map.overlays.PoiOverlayWithIW;
import com.example.mobilemap.pois.PoisActivity;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.tables.Poi;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Classe de gestion de la carte principale
 *
 * @author J.Houdé
 */
public final class MapManager {
    private static final double MIN_ZOOM = 3.0;
    private final MapView mapView;
    private final MainActivity activity;
    private final Context context;

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private final SharedPreferences sharedPreferences;

    public CircleManager getCircleManager() {
        return circleManager;
    }

    private final CircleManager circleManager;

    public ItemizedIconOverlay<OverlayItem> getItemizedOverlay() {
        return itemizedOverlay;
    }

    private ItemizedIconOverlay<OverlayItem> itemizedOverlay;

    public MarkerGestureListener getMarkerGestureListener() {
        return markerGestureListener;
    }

    private final Map<String, PoiInfoWindow> infoWindowMap;

    private final MarkerGestureListener markerGestureListener;

    private MyLocationNewOverlay myLocationNewOverlay;

    /**
     * Classe de gestion de la carte principale
     *
     * @param mapView  vue de la carte à gérer
     * @param activity activité mère affichant la carte
     */
    public MapManager(MapView mapView, MainActivity activity) {
        this.mapView = mapView;
        this.activity = activity;
        this.context = activity.getApplicationContext();

        sharedPreferences = context.getSharedPreferences(SharedPreferencesConstant.PREFS_NAME, Context.MODE_PRIVATE);
        infoWindowMap = new HashMap<>();
        circleManager = new CircleManager(mapView, activity, this, infoWindowMap);
        markerGestureListener = new MarkerGestureListener(mapView, this, infoWindowMap, activity);
    }

    /**
     * Initialisation de la carte
     */
    public void initMap() {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        initMapDefaultSettings(mapView);

        mapView.setScrollableAreaLimitLatitude(MapView.getTileSystem().getMaxLatitude(),
                MapView.getTileSystem().getMinLatitude(), 0);
        mapView.setMinZoomLevel(MIN_ZOOM);
        mapView.getController().setZoom(SharedPreferencesConstant.DEFAULT_ZOOM);

        initMapOverlays();
    }

    /**
     * Initialisatin de la carte avec les paramètres par défaut
     *
     * @param mapView vue de la carte
     */
    public static void initMapDefaultSettings(MapView mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getZoomController()
                .setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapView.setMultiTouchControls(true);
        mapView.setVerticalMapRepetitionEnabled(false);
    }

    /**
     * Initialisation des couches de la carte
     */
    private void initMapOverlays() {
        myLocationNewOverlay = new LocationOverlayInitiator(mapView, activity, this).init();
        itemizedOverlay = new ItemizedIconOverlayInitiator(mapView, this, activity, markerGestureListener).init();

        // hint l'ordre des overlays est important pour les supporpositions et le dernier de la liste sera la première couche à réagir aux actions de l'utilisateur
        List<Overlay> overlays = new ArrayList<>(Arrays.asList(
                new AddMarkerOverlay(activity),
                myLocationNewOverlay,
                new RotationOverlayInitiator(mapView).init(),
                itemizedOverlay,
                new CompassOverlayInitiator(mapView, context).init(),
                new ScaleBarInitiator(mapView, context).init(),
                new CopyrightOverlay(context)));

        mapView.getOverlays().addAll(overlays);
    }

    /**
     * Affichage du dialogue de filtrage autour d'un marqueur choisi
     */
    public void showAddCircleAroundPoiDialog(OverlayItem item) {
        AddCircleAroundPoiDialogBuilder builder = new AddCircleAroundPoiDialogBuilder(activity, this, item);
        builder.show();
    }

    /**
     * Affichage du dialogue de filtrage autour d'une localisation recherchée
     *
     * @param address adresse recherchée
     */
    public void showAddCircleAroundSearch(Address address) {
        showAddCircleAroundPoiDialog(new OverlayItem(
                "0", // le marqueur n'existe pas dans la base de données et l'identifiant 0 n'est jamais donnée par défaut avec l'autoincrement
                address.getAddressLine(0), // adresse complète
                address.getLocality(), // nom de la ville et du pays
                new GeoPoint(address.getLatitude(), address.getLongitude())));
    }

    /**
     * Affichage du dialogue de filtrage pour la position actuelle
     */
    public void showAddCircleAroundMeDialog() {
        showAddCircleAroundPoiDialog(null);
    }

    /**
     * Vérifie si un cercle a été sauvegardé dans les préférences
     *
     * @return vrai s'il y a un cercle sauvegardé dans les préférences
     */
    public boolean hasSavedCircle() {
        return circleManager.hasSavedCircle();
    }

    /**
     * Actualisation des éléments affichés sur la carte
     */
    public void updateMap() {
        if (hasSavedCircle()) { // actualise le cercle avec les sites dedans
            circleManager.restorePreviousCircle();
        } else { // actualise tous les sites de la carte
            itemizedOverlay.removeAllItems();
            itemizedOverlay.addItems(getOverlayItems());
        }

        updateOpenedInfoWindows();

        mapView.invalidate();
    }

    /**
     * Actualisation de toutes les infoWindows ouvertes
     */
    public void updateOpenedInfoWindows() {
        infoWindowMap.values().stream()
                .filter(InfoWindow::isOpen)
                .forEach(this::updateInfoWindow);
    }

    /**
     * Actualisation du centenu affiché par l'infoWindow
     *
     * @param infoWindow infoWindows à actualiser
     */
    private void updateInfoWindow(PoiInfoWindow infoWindow) {
        infoWindow.close(); // ferme les infoWindows pour les actualiser ou supprimer
        findItem(infoWindow.getPoint())
                .ifPresent(overlayItem -> createOverlayWithIW(overlayItem, infoWindow));
    }

    /**
     * Retourne l'overlayItem avec la localisation voulue
     *
     * @param center point central du cercle
     * @return overlayItem avec la localisation voulue
     */
    public Optional<OverlayItem> findItem(IGeoPoint center) {
        return this.getOverlayItems().stream()
                .filter(item -> item.getPoint().getLatitude() == center.getLatitude() && item.getPoint().getLongitude() == center.getLongitude())
                .findFirst();
    }

    /**
     * Création et ouverture du contenu à afficher dans l'infoWindow
     *
     * @param item       connu à afficher
     * @param infoWindow infoWindow à initialiser
     * @return contenu d'information à ajouter à la carte
     */
    public OverlayWithIW createOverlayWithIW(OverlayItem item, InfoWindow infoWindow) {
        OverlayWithIW overlayWithIW = new PoiOverlayWithIW(item);

        overlayWithIW.setInfoWindow(infoWindow);
        overlayWithIW.getInfoWindow().open(overlayWithIW, (GeoPoint) item.getPoint(),
                PoiInfoWindow.OFFSET_X, PoiInfoWindow.OFFSET_Y);

        return overlayWithIW;
    }

    /**
     * Mappage d'un Poi en OverlayItem
     *
     * @param poi site à convertir
     * @return OverlayItem correspondant au site
     */
    private OverlayItem poiToOverlayItemMapper(Poi poi) {
        return new OverlayItem(String.valueOf(poi.getId()), poi.getName(), poi.getResume(), new GeoPoint(poi.getLatitude(), poi.getLongitude()));
    }

    /**
     * Récupération de tous les marqueurs existants
     *
     * @return les marqueurs à afficher sur la carte
     */
    public List<OverlayItem> getOverlayItems() {
        List<Poi> pois = ContentResolverHelper.getPois(activity.getContentResolver());

        return pois.stream()
                .map(this::poiToOverlayItemMapper)
                .collect(Collectors.toList());
    }

    /**
     * Récupération de tous les marqueurs existants de la catégorie voulue
     *
     * @param categoryFilter catégorie de filtrage
     * @return les marqueurs à afficher sur la carte
     */
    public List<OverlayItem> getOverlayItems(long categoryFilter) {
        List<Poi> pois = ContentResolverHelper.getPois(activity.getContentResolver());
        List<OverlayItem> overlayItems = new ArrayList<>();

        for (Poi poi : pois) {
            if (poi.getCategoryId() == categoryFilter) {
                overlayItems.add(poiToOverlayItemMapper(poi)); // garde les marqueurs de la catégorie voulue
                continue;
            }

            String uid = String.valueOf(poi.getId());
            if (infoWindowMap.containsKey(uid)) { // ferme les infoWindow des marquers à retirer
                Objects.requireNonNull(infoWindowMap.get(uid)).close();
            }
        }

        return overlayItems;
    }

    /**
     * Enregistrement des paramètres de la carte avant fermeture
     */
    public void onPause() {
        // Sauvegarde des paramètres actuels de la carte
        sharedPreferences.edit()
                .putString(SharedPreferencesConstant.PREFS_TILE_SOURCE, mapView.getTileProvider().getTileSource().name())
                .putFloat(SharedPreferencesConstant.PREFS_ORIENTATION, mapView.getMapOrientation())
                .putString(SharedPreferencesConstant.PREFS_LATITUDE_STRING, String.valueOf(mapView.getMapCenter().getLatitude()))
                .putString(SharedPreferencesConstant.PREFS_LONGITUDE_STRING, String.valueOf(mapView.getMapCenter().getLongitude()))
                .putFloat(SharedPreferencesConstant.PREFS_ZOOM_LEVEL_DOUBLE, (float) mapView.getZoomLevelDouble())
                .apply();

        mapView.onPause();
    }

    /**
     * Restauration de la carte avec les paramètres sauvegardés
     */
    public void onResume() {
        mapView.onResume();

        if (sharedPreferences == null) {
            return;
        }

        restorePreviosSettings();

        boolean isAroundMe = sharedPreferences.getBoolean(SharedPreferencesConstant.CIRCLE_IS_AROUND_ME, false);

        if (isAroundMe) { // affichage du bouton de suppression du cercle
            activity.updateFilterAction(true);
        }
    }

    /**
     * Restauration des paramètres précédents sur la localisation, le zoom et l'orientation
     */
    private void restorePreviosSettings() {
        float zoomLevel = sharedPreferences.getFloat(SharedPreferencesConstant.PREFS_ZOOM_LEVEL_DOUBLE, SharedPreferencesConstant.DEFAULT_ZOOM);
        mapView.getController().setZoom(zoomLevel);

        float orientation = sharedPreferences.getFloat(SharedPreferencesConstant.PREFS_ORIENTATION, 0);
        mapView.setMapOrientation(orientation, false);

        String latitudeString = sharedPreferences.getString(SharedPreferencesConstant.PREFS_LATITUDE_STRING, SharedPreferencesConstant.DEFAULT_LATITUDE);
        String longitudeString = sharedPreferences.getString(SharedPreferencesConstant.PREFS_LONGITUDE_STRING, SharedPreferencesConstant.DEFAULT_LONGITUDE);
        mapView.setExpectedCenter(new GeoPoint(Double.parseDouble(latitudeString.trim()), Double.parseDouble(longitudeString.trim())));
    }

    /**
     * Centrage de la carte sur le centre du cercle de filtrage
     */
    public void centerToCircleCenter() {
        mapView.getController().animateTo(circleManager.getCircleCenter());
    }

    /**
     * Destruction de la carte
     */
    public void onDetach() {
        mapView.onDetach();
    }

    /**
     * Ajout d'un nouveau marqueur à la position actuelle de l'utilisateur
     */
    public void addMarkerToCurrentLocation() {
        if (myLocationNewOverlay == null || myLocationNewOverlay.getMyLocation() == null) {
            return;
        }

        GeoPoint point = myLocationNewOverlay.getMyLocation();
        // lancement de l'activité de gestion des sites pour ajouter un nouveau site
        activity.getPoiActivityLauncher()
                .launch(PoisActivity.createIntent(activity, point.getLatitude(), point.getLongitude()));
    }

    /**
     * Centrage de la carte sur la position actuelle de l'utilisateur
     */
    public void centerToUserLocation() {
        if (myLocationNewOverlay == null || myLocationNewOverlay.getMyLocation() == null) {
            return;
        }

        mapView.getController().animateTo(myLocationNewOverlay.getMyLocation()); // centrage de la carte sur la position actuelle
    }

    /**
     * Affichgae d'un cercle de filtrage de marquer autour d'un point donné
     *
     * @param item           élément central du cercle
     * @param radiusInMeters rayon du cercle en mètre
     * @param categoryFilter catégorie de marqueurs à afficher
     */
    public void drawCircle(OverlayItem item, double radiusInMeters, long categoryFilter) {
        if (hasSavedCircle()) {
            removeCircle();
        }

        circleManager.drawCircle(item, radiusInMeters, categoryFilter);
        // enregistrement du centre du cercle pour permettre la supression avec la prochaine action "long tap"
        markerGestureListener.setCurrentCircleCenterItemUid(item.getUid());
        mapView.invalidate(); // demande le rafraichissement de la carte
    }

    /**
     * Affichage du cercle autour de la postion actuelle de l'utilisateur
     *
     * @param point          élément central du cercle
     * @param radiusInMeters rayon du cercle en mètre
     * @param categoryFilter catégorie de marqueurs à afficher
     */
    public void drawCircleAroundMe(GeoPoint point, double radiusInMeters, long categoryFilter) {
        if (hasSavedCircle()) {
            removeCircle();
        }

        circleManager.drawCircleAroundMe(point, radiusInMeters, categoryFilter);
        mapView.invalidate(); // demande le rafraichissement de la carte si le cercle a été ajouté
        activity.updateFilterAction(true);
    }

    /**
     * Effacement du cercle actuellement affiché
     */
    public void removeCircle() {
        if (isCircleAroundMe()) { // affichage du bouton avec la loupe de recherche pour tracer un cercle autour de la position actuelle
            activity.updateFilterAction(false);
        }

        circleManager.removeCircle();
    }

    /**
     * Vérifie si le cercle a été tracé avec pour centre l'utilisateur et suit sa position
     *
     * @return vrai si le cercle suit l'utilisateur, sinon retourne faux si le cercle a pour centre un marqueur quelconque de la carte
     */
    public boolean isCircleAroundMe() {
        return sharedPreferences.getBoolean(SharedPreferencesConstant.CIRCLE_IS_AROUND_ME, false);
    }

    /**
     * Retourne la localisation actuelle de l'utilisateur
     *
     * @return localisateur actuelle sous forme de GeoPoint
     */
    public Optional<GeoPoint> getMyLocationPoint() {
        return myLocationNewOverlay == null ? Optional.empty() : Optional.ofNullable(myLocationNewOverlay.getMyLocation());
    }

}
