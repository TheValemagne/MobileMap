package com.example.mobilemap.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.example.mobilemap.map.overlays.AddMarkerOverlay;
import com.example.mobilemap.map.overlays.CustomOverlayWithIW;
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
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class MapManager {
    private final MapView mapView;
    private final MainActivity activity;
    private final Context context;

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private final SharedPreferences sharedPreferences;
    private final CircleManager circleManager;

    public ItemizedIconOverlay<OverlayItem> getOverlayItemItemizedOverlay() {
        return overlayItemItemizedOverlay;
    }

    private ItemizedIconOverlay<OverlayItem> overlayItemItemizedOverlay;

    public MarkerGestureListener getMarkerGestureListener() {
        return markerGestureListener;
    }

    private final MarkerGestureListener markerGestureListener;
    private MyLocationNewOverlay myLocationNewOverlay;

    public MapManager(MapView mapView, MainActivity activity, Context context) {
        this.mapView = mapView;
        this.activity = activity;
        this.context = context;

        sharedPreferences = context.getSharedPreferences(SharedPreferencesConstant.PREFS_NAME, Context.MODE_PRIVATE);
        Map<String, InfoWindow> integerInfoWindowMap = new HashMap<>();
        markerGestureListener = new MarkerGestureListener(mapView, this, integerInfoWindowMap);
        circleManager = new CircleManager(mapView, this, integerInfoWindowMap);
    }

    public void initMap() {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getZoomController()
                .setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapView.setMultiTouchControls(true);

        mapView.setHorizontalMapRepetitionEnabled(false);
        mapView.setVerticalMapRepetitionEnabled(false);

        IMapController mapController = mapView.getController();
        mapController.setZoom(SharedPreferencesConstant.DEFAULT_ZOOM);

        initMapOverlays();
    }

    private void initMapOverlays() {
        myLocationNewOverlay = new MyLocationOverlay(new GpsMyLocationProvider(context), mapView, activity, this);
        myLocationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationNewOverlay);

        CopyrightOverlay mCopyrightOverlay = new CopyrightOverlay(context);
        mapView.getOverlays().add(mCopyrightOverlay);

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
        AddCircleAroundPoiDialog builder = new AddCircleAroundPoiDialog(activity, this, item);
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
            activity.getShowCircleAroundMe().setVisibility(View.GONE);
            activity.getRemoveCircleAroundMe().setVisibility(View.VISIBLE);
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
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
    }

    public void drawCircle(OverlayItem item, double radiusInMeters, long categoryFilter) {
        markerGestureListener.setLastCircleCenterItemUid(item.getPoint());

        if (hasSavedCircle()) {
            removeCircle();
        }

        circleManager.drawCircle(item, radiusInMeters, categoryFilter);
        mapView.invalidate(); // demande le rafraichissement de la carte si le cercle a été ajouté
    }

    public void drawCircleAroundMe(double radiusInMeters, long categoryFilter) {
        if (hasSavedCircle()) {
            removeCircle();
        }

        circleManager.drawCircleAroundMe(myLocationNewOverlay.getMyLocation(), radiusInMeters, categoryFilter);
        mapView.invalidate(); // demande le rafraichissement de la carte si le cercle a été ajouté
        activity.getShowCircleAroundMe().setVisibility(View.GONE);
        activity.getRemoveCircleAroundMe().setVisibility(View.VISIBLE);
    }

    public void removeCircle() {
        circleManager.removeCircle();
        activity.getShowCircleAroundMe().setVisibility(View.VISIBLE);
        activity.getRemoveCircleAroundMe().setVisibility(View.GONE);
    }

    public void showInfoWindow(OverlayItem item, InfoWindow infoWindow) {
        OverlayWithIW overlayWithIW = new CustomOverlayWithIW(item);

        overlayWithIW.setInfoWindow(infoWindow);
        overlayWithIW.getInfoWindow().open(overlayWithIW, (GeoPoint) item.getPoint(),
                CustomInfoWindow.OFFSET_X, CustomInfoWindow.OFFSET_Y);

        mapView.getOverlays().add(overlayWithIW);
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
