package com.example.mobilemap.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import com.example.mobilemap.CircleRadiusDialog;
import com.example.mobilemap.MainActivity;
import com.example.mobilemap.PoisActivity;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.table.Poi;
import com.example.mobilemap.listener.MarkerGertureListener;

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
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class MapManager {
    private final MapView mapView;
    private final MainActivity activity;
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private Polygon circleOverlay;
    private ItemizedIconOverlay<OverlayItem> overlayItemItemizedOverlay;
    private MarkerGertureListener markerGertureListener;
    private MyLocationNewOverlay myLocationNewOverlay;
    private final static int MAX_ANGLE_IN_DEGREE = 360;
    private InfoWindow infoWindow;

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

    private static final String CIRCLE_LATITUDE_STRING = "circleLatitudeString";
    private static final String CIRCLE_LONGITUDE_STRING = "circleLongitudeString";
    private static final String CIRCLE_ITEM_INDEX = "circleItemIndex";
    private static final String CIRCLE_RADIUS_STRING = "cicleRadiusString";
    private static final String CIRCLE_CATEGORY_FILTER = "cicleCategoryFilter";

    public MapManager(MapView mapView, MainActivity activity, Context context) {
        this.mapView = mapView;
        this.activity = activity;
        this.context = context;

        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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
        markerGertureListener = new MarkerGertureListener(this);
        overlayItemItemizedOverlay = new ItemizedIconOverlay<>(context, getOverlayItems(), markerGertureListener);
        mapView.getOverlays().add(overlayItemItemizedOverlay);
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
    private List<OverlayItem> getOverlayItems() {
        return getOverlayItems(-1);
    }

    @NonNull
    private List<OverlayItem> getOverlayItems(long categoryFilter) {
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
        double latitude = Double.parseDouble(latitudeString);
        double longitude = Double.parseDouble(longitudeString);
        mapView.setExpectedCenter(new GeoPoint(latitude, longitude));

        restorePreviousCircle();
    }

    private void restorePreviousCircle() {
        String circleRadius = sharedPreferences.getString(CIRCLE_RADIUS_STRING, "");
        int itemIndex = sharedPreferences.getInt(CIRCLE_ITEM_INDEX, -1);

        if(circleRadius.isEmpty() || itemIndex == -1) {
            return;
        }
        String circleLatitude = sharedPreferences.getString(CIRCLE_LATITUDE_STRING, "0.0");
        String circleLongitude = sharedPreferences.getString(CIRCLE_LONGITUDE_STRING, "0.0");
        IGeoPoint center = new GeoPoint(Double.parseDouble(circleLatitude), Double.parseDouble(circleLongitude));

        OverlayItem centerItem = overlayItemItemizedOverlay.getItem(itemIndex);

        if(!centerItem.getPoint().equals(center)){
            return;
        }

        long categoryFilter = sharedPreferences.getLong(CIRCLE_CATEGORY_FILTER, -1);

        drawCircle(itemIndex, Double.parseDouble(circleRadius), categoryFilter);
        markerGertureListener.setLastItemUid(center);
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
        if (circleOverlay != null) {
            removeCircle();
        }

        OverlayItem centerItem = overlayItemItemizedOverlay.getItem(index);
        centerItem.setMarker(ResourcesCompat.getDrawable(activity.getResources(), org.osmdroid.library.R.drawable.marker_default, activity.getTheme()));
        drawCenterInfoWindow(centerItem);
        IGeoPoint center = centerItem.getPoint();

        // Générez les points du périmètre du cercle
        List<GeoPoint> circlePoints = generateCirclePerimeterPoints(center, radiusInMeters);
        // Ajoutez le cercle à la carte
        circleOverlay = new Polygon();
        // intérieur du cercle
        circleOverlay.getFillPaint().setColor(Color.TRANSPARENT);
        // bordure du cercle
        circleOverlay.getOutlinePaint().setColor(Color.BLUE);
        circleOverlay.getOutlinePaint().setStrokeWidth(2f);

        circleOverlay.setPoints(circlePoints);
        mapView.getOverlayManager().add(circleOverlay);

        List<OverlayItem> filteredItems = getOverlayItems(categoryFilter);

        if(!filteredItems.contains(centerItem)) { // affiche toujour le point centrale du cercle
            filteredItems.add(centerItem);
        }

        showPoisInsideCircle(filteredItems, center, radiusInMeters);

        // Sauvegarde les données du circle pour retracer le cercle si l'application est mise en pause
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(CIRCLE_LATITUDE_STRING, String.valueOf(center.getLatitude()));
        edit.putString(CIRCLE_LONGITUDE_STRING, String.valueOf(center.getLongitude()));
        edit.putString(CIRCLE_RADIUS_STRING, String.valueOf(radiusInMeters));
        edit.putLong(CIRCLE_CATEGORY_FILTER, categoryFilter);
        edit.putInt(CIRCLE_ITEM_INDEX, index);
        edit.apply();
    }

    private void drawCenterInfoWindow(OverlayItem centerItem) {
        OverlayWithIW overlayWithIW = new CustomOverlayWithIW(centerItem);
        infoWindow = new CustomInfoWindow(org.osmdroid.library.R.layout.bonuspack_bubble, mapView);
        overlayWithIW.setInfoWindow(infoWindow);
        mapView.getOverlays().add(overlayWithIW);
        overlayWithIW.getInfoWindow().open(overlayWithIW, (GeoPoint) centerItem.getPoint(), 0, -25);
    }


    public void removeCircle() {
        if (circleOverlay != null) {
            mapView.getOverlayManager().remove(circleOverlay);
            circleOverlay = null;
        }

        if (infoWindow != null && infoWindow.isOpen()) {
            infoWindow.close();
        }

        // Supprimer les données du cercle
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(CIRCLE_LATITUDE_STRING, "");
        edit.putString(CIRCLE_LONGITUDE_STRING, "");
        edit.putString(CIRCLE_RADIUS_STRING, "");
        edit.apply();

        updateMarkers();
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

    private void showPoisInsideCircle(List<OverlayItem> items, IGeoPoint center, double radiusInMeters) {
        overlayItemItemizedOverlay.removeAllItems(); // supprime tous les marqueurs de sites affichés

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

        overlayItemItemizedOverlay.addItems(overlayItems);
        mapView.invalidate();
    }
}
