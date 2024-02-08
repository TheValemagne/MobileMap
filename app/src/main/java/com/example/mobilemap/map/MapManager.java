package com.example.mobilemap.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.example.mobilemap.map.overlays.AddMarkerOverlay;
import com.example.mobilemap.map.overlays.MapNorthCompassOverlay;
import com.example.mobilemap.map.overlays.MyLocationOverlay;
import com.example.mobilemap.pois.PoisActivity;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.tables.Poi;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class MapManager {
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

    public ItemizedIconOverlay<OverlayItem> getOverlayItemItemizedOverlay() {
        return overlayItemItemizedOverlay;
    }

    private ItemizedIconOverlay<OverlayItem> overlayItemItemizedOverlay;

    public MarkerGestureListener getMarkerGestureListener() {
        return markerGestureListener;
    }
    private final Map<String, InfoWindow> itemInfoWindowMap;

    private final MarkerGestureListener markerGestureListener;
    private MyLocationNewOverlay myLocationNewOverlay;

    public MapManager(MapView mapView, MainActivity activity, Context context) {
        this.mapView = mapView;
        this.activity = activity;
        this.context = context;

        sharedPreferences = context.getSharedPreferences(SharedPreferencesConstant.PREFS_NAME, Context.MODE_PRIVATE);
        itemInfoWindowMap = new HashMap<>();
        circleManager = new CircleManager(mapView, activity, this, itemInfoWindowMap);
        markerGestureListener = new MarkerGestureListener(mapView, this, itemInfoWindowMap, activity.getResources());
    }

    /**
     * Initialisation de la carte
     */
    public void initMap() {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getZoomController()
                .setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapView.setMultiTouchControls(true);
        mapView.setScrollableAreaLimitLatitude(MapView.getTileSystem().getMaxLatitude(),
                MapView.getTileSystem().getMinLatitude(),0);

        mapView.setVerticalMapRepetitionEnabled(false);
        mapView.setMinZoomLevel(3.0);

        IMapController mapController = mapView.getController();
        mapController.setZoom(SharedPreferencesConstant.DEFAULT_ZOOM);

        initMapOverlays();
    }

    /**
     * Initialisation des couches de la carte
     */
    private void initMapOverlays() {
        myLocationNewOverlay = new MyLocationOverlay(new GpsMyLocationProvider(context), mapView, activity, this);
        myLocationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationNewOverlay);

        CopyrightOverlay mCopyrightOverlay = new CopyrightOverlay(context);
        mapView.getOverlays().add(mCopyrightOverlay);

        CompassOverlay mapNorthCompassOverlay = new MapNorthCompassOverlay(context, mapView);
        mapNorthCompassOverlay.enableCompass();
        mapNorthCompassOverlay.setCompassCenter(40, 55);
        mapView.getOverlays().add(mapNorthCompassOverlay);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(displayMetrics.widthPixels / 2, 10);
        mapView.getOverlays().add(mScaleBarOverlay);

        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(mapView);
        mRotationGestureOverlay.setEnabled(true);
        mapView.getOverlays().add(mRotationGestureOverlay);

        mapView.getOverlays().add(new AddMarkerOverlay(activity));

        overlayItemItemizedOverlay = new ItemizedIconOverlay<>(context, getOverlayItems(), markerGestureListener);
        mapView.getOverlays().add(overlayItemItemizedOverlay);
    }

    public void showAddCircleAroundPoiDialog(OverlayItem item) {
        AddCircleAroundPoiDialogBuilder builder = new AddCircleAroundPoiDialogBuilder(activity, this, item);
        builder.show();
    }

    public void showAddCircleAroundMeDialog() {
        showAddCircleAroundPoiDialog(null);
    }

    public boolean hasSavedCircle() {
        return circleManager.hasSavedCircle();
    }

    public void updateMarkers() {
        if(hasSavedCircle()) { // actualise le cercle avec les sites dedans
            circleManager.restorePreviousCircle();
        } else { // actualise tous les sites de la carte
            overlayItemItemizedOverlay.removeAllItems();
            overlayItemItemizedOverlay.addItems(getOverlayItems());
        }

        mapView.invalidate();
    }

    @NonNull
    private OverlayItem mapPoiToOverlayItem(Poi poi) {
        return new OverlayItem(poi.getName(), poi.getResume(), new GeoPoint(poi.getLatitude(), poi.getLongitude()));
    }

    @NonNull
    public List<OverlayItem> getOverlayItems() {
        List<Poi> pois = ContentResolverHelper.getPois(activity.getContentResolver());

        return pois.stream()
                .map(this::mapPoiToOverlayItem)
                .collect(Collectors.toList());
    }

    @NonNull
    public List<OverlayItem> getOverlayItems(long categoryFilter) {
        List<Poi> pois = ContentResolverHelper.getPois(activity.getContentResolver());
        List<OverlayItem> overlayItems = new ArrayList<>();

        for (Poi poi : pois) {
            if (poi.getCategoryId() == categoryFilter) {
                overlayItems.add(mapPoiToOverlayItem(poi));
                continue;
            }

            String uid = getItemUid(new GeoPoint(poi.getLatitude(), poi.getLongitude()));
            if (itemInfoWindowMap.containsKey(uid)) {
                Objects.requireNonNull(itemInfoWindowMap.get(uid)).close();
            }
        }

        return overlayItems;
    }

    /**
     * Enregistrement des paramètres de la carte avant fermeture
     */
    public void onPause() {
        // Sauvegarde des paramètres actuels de la carte
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(SharedPreferencesConstant.PREFS_TILE_SOURCE, mapView.getTileProvider().getTileSource().name());
        edit.putFloat(SharedPreferencesConstant.PREFS_ORIENTATION, mapView.getMapOrientation());
        edit.putString(SharedPreferencesConstant.PREFS_LATITUDE_STRING, String.valueOf(mapView.getMapCenter().getLatitude()));
        edit.putString(SharedPreferencesConstant.PREFS_LONGITUDE_STRING, String.valueOf(mapView.getMapCenter().getLongitude()));
        edit.putFloat(SharedPreferencesConstant.PREFS_ZOOM_LEVEL_DOUBLE, (float) mapView.getZoomLevelDouble());
        edit.apply();
    }

    /**
     * Restauration de la carte avec les paramètres sauvegardés
     */
    public void restoreMap() {
        if(sharedPreferences == null){
            return;
        }

        float zoomLevel = sharedPreferences.getFloat(SharedPreferencesConstant.PREFS_ZOOM_LEVEL_DOUBLE, SharedPreferencesConstant.DEFAULT_ZOOM);
        mapView.getController().setZoom(zoomLevel);

        float orientation = sharedPreferences.getFloat(SharedPreferencesConstant.PREFS_ORIENTATION, 0);
        mapView.setMapOrientation(orientation, false);

        String latitudeString = sharedPreferences.getString(SharedPreferencesConstant.PREFS_LATITUDE_STRING, SharedPreferencesConstant.DEFAULT_LATITUDE);
        String longitudeString = sharedPreferences.getString(SharedPreferencesConstant.PREFS_LONGITUDE_STRING, SharedPreferencesConstant.DEFAULT_LONGITUDE);
        mapView.setExpectedCenter(new GeoPoint(Double.parseDouble(latitudeString), Double.parseDouble(longitudeString)));


        boolean isAroundMe = sharedPreferences.getBoolean(SharedPreferencesConstant.CIRCLE_IS_AROUND_ME, false);

        if (isAroundMe) {
            activity.updateFilterAction(true);
        }
    }

    public void addMarkerToCurrentLocation() {
        if (myLocationNewOverlay == null || myLocationNewOverlay.getMyLocation() == null) {
            return;
        }

        GeoPoint point = myLocationNewOverlay.getMyLocation();
        activity.poiActivityLauncher.launch(PoisActivity.createIntent(activity, point.getLatitude(), point.getLongitude()));
    }

    public void centerToUserLocation() {
        if (myLocationNewOverlay == null || myLocationNewOverlay.getMyLocation() == null) {
            return;
        }

        myLocationNewOverlay.enableFollowLocation(); // centrage de la carte sur la position actuelle
    }

    public void drawCircle(OverlayItem item, double radiusInMeters, long categoryFilter) {
        if (hasSavedCircle()) {
            removeCircle();
        }

        circleManager.drawCircle(item, radiusInMeters, categoryFilter);
        markerGestureListener.setLastCircleCenterItemUid(item.getPoint());
        mapView.invalidate(); // demande le rafraichissement de la carte si le cercle a été ajouté
    }

    public void drawCircleAroundMe(double radiusInMeters, long categoryFilter) {
        if (hasSavedCircle()) {
            removeCircle();
        }

        circleManager.drawCircleAroundMe(myLocationNewOverlay.getMyLocation(), radiusInMeters, categoryFilter);
        mapView.invalidate(); // demande le rafraichissement de la carte si le cercle a été ajouté
        activity.updateFilterAction(true);
    }

    public void removeCircle() {
        circleManager.removeCircle();
        activity.updateFilterAction(false);
    }

    public boolean isCircleAroundMe() {
        return sharedPreferences.getBoolean(SharedPreferencesConstant.CIRCLE_IS_AROUND_ME, false);
    }

    public static String getItemUid(IGeoPoint point) {
        NumberFormat nf= NumberFormat.getInstance();
        nf.setMaximumFractionDigits(15);
        return MessageFormat.format("{0}:{1}", nf.format(point.getLatitude()), nf.format(point.getLongitude()));
    }

}
