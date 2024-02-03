package com.example.mobilemap.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.example.mobilemap.CircleRadiusDialog;
import com.example.mobilemap.MainActivity;
import com.example.mobilemap.PoisActivity;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.table.Poi;
import com.example.mobilemap.listener.MarkerGertureListener;

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
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;
import java.util.stream.Collectors;

public final class MapManager {
    private final MapView mapView;
    private final MainActivity activity;
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private CircleManager circleManager;
    private ItemizedIconOverlay<OverlayItem> overlayItemItemizedOverlay;
    private final MarkerGertureListener markerGertureListener;
    private MyLocationNewOverlay myLocationNewOverlay;

    // préférence par défaut
    private final static float DEFAULT_ZOOM = 13.5F;
    private final static String DEFAULT_LATITUDE = "49.109523";
    private final static String DEFAULT_LONGITUDE = "6.1768191";

    // Pour le stockage des préférences
    private static final String PREFS_NAME = "com.exemple.mobileMap";
    private static final String PREFS_TILE_SOURCE = "tilesource";
    private static final String PREFS_LATITUDE_STRING = "latitudeString";
    private static final String PREFS_LONGITUDE_STRING = "longitudeString";
    private static final String PREFS_ORIENTATION = "orientation";
    private static final String PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble";

    public MapManager(MapView mapView, MainActivity activity, Context context) {
        this.mapView = mapView;
        this.activity = activity;
        this.context = context;

        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        markerGertureListener = new MarkerGertureListener(this);
    }

    public void initMap() {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getZoomController()
                .setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapView.setMultiTouchControls(true);

        mapView.setHorizontalMapRepetitionEnabled(false);
        mapView.setVerticalMapRepetitionEnabled(false);

        myLocationNewOverlay = new MyLocationOverlay(new GpsMyLocationProvider(context), mapView, activity);
        myLocationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationNewOverlay);

        CopyrightOverlay mCopyrightOverlay = new CopyrightOverlay(context);
        mapView.getOverlays().add(mCopyrightOverlay);

        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(mScaleBarOverlay);

        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(mapView);
        mRotationGestureOverlay.setEnabled(true);
        mapView.getOverlays().add(mRotationGestureOverlay);

        mapView.getOverlays().add(new AddMarkerOverlay(activity));
        initMarkers();

        IMapController mapController = mapView.getController();
        mapController.setZoom(DEFAULT_ZOOM);
    }

    private void initMarkers() {
        overlayItemItemizedOverlay = new ItemizedIconOverlay<>(context, getOverlayItems(), markerGertureListener);
        mapView.getOverlays().add(overlayItemItemizedOverlay);
        circleManager = new CircleManager(mapView, activity, this, sharedPreferences, overlayItemItemizedOverlay, markerGertureListener);
    }

    public void addOverlayItemCircle(int index) {
        removeCircle();
        CircleRadiusDialog builder = new CircleRadiusDialog(activity, this, index);
        builder.show();
        mapView.invalidate();
    }

    public void updateMarkers() {
        overlayItemItemizedOverlay.removeAllItems();

        List<OverlayItem> items = getOverlayItems();
        overlayItemItemizedOverlay.addItems(items);

        mapView.invalidate();
    }

    @NonNull
    public List<OverlayItem> getOverlayItems() {
        return getOverlayItems(-1);
    }

    @NonNull
    public List<OverlayItem> getOverlayItems(long categoryFilter) {
        List<Poi> pois = ContentResolverHelper.getPois(activity.getContentResolver());

        return pois.stream().filter(poi -> categoryFilter == -1 || poi.getCategoryId() == categoryFilter)
                .map(poi -> new OverlayItem(poi.getName(), poi.getResume(), new GeoPoint(poi.getLatitude(), poi.getLongitude())))
                .collect(Collectors.toList());
    }

    /**
     * Enregistrement des paramètres de la carte avant fermeture
     */
    public void onPause() {
        // Sauvegarde des paramètres actuels de la carte
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(PREFS_TILE_SOURCE, mapView.getTileProvider().getTileSource().name());
        edit.putFloat(PREFS_ORIENTATION, mapView.getMapOrientation());
        edit.putString(PREFS_LATITUDE_STRING, String.valueOf(mapView.getMapCenter().getLatitude()));
        edit.putString(PREFS_LONGITUDE_STRING, String.valueOf(mapView.getMapCenter().getLongitude()));
        edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, (float) mapView.getZoomLevelDouble());
        edit.apply();
    }

    /**
     * Restauration de la carte avec les paramètres sauvegardés
     */
    public void restoreMap() {
        if(sharedPreferences == null){
            return;
        }

        float zoomLevel = sharedPreferences.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, DEFAULT_ZOOM);
        mapView.getController().setZoom(zoomLevel);

        float orientation = sharedPreferences.getFloat(PREFS_ORIENTATION, 0);
        mapView.setMapOrientation(orientation, false);

        String latitudeString = sharedPreferences.getString(PREFS_LATITUDE_STRING, DEFAULT_LATITUDE);
        String longitudeString = sharedPreferences.getString(PREFS_LONGITUDE_STRING, DEFAULT_LONGITUDE);
        mapView.setExpectedCenter(new GeoPoint(Double.parseDouble(latitudeString), Double.parseDouble(longitudeString)));

        circleManager.restorePreviousCircle();
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
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
    }

    public void drawCircle(int index, double radiusInMeters, long categoryFilter) {
        circleManager.drawCircle(index, radiusInMeters, categoryFilter);
    }

    public void removeCircle() {
        circleManager.removeCircle();
    }

}
