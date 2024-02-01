package com.example.mobilemap.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.example.mobilemap.MainActivity;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.table.Poi;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
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
    private MyLocationNewOverlay myLocationNewOverlay;
    private final static int MAX_ANGLE_IN_DEGREE = 360;

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
        myLocationNewOverlay.enableFollowLocation();
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

        initMarkers();
        mapView.getOverlays().add(new AddMarkerOverlay(activity));

        IMapController mapController = mapView.getController();
        mapController.setZoom(13.5);
    }

    private void initMarkers() {
        overlayItemItemizedOverlay = new ItemizedIconOverlay<>(context, new ArrayList<>(), new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                Log.d("MainActivity", item.getTitle());
                return true;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                return false;
            }
        });

        updateMarkers();
        mapView.getOverlays().add(overlayItemItemizedOverlay);
    }

    public void updateMarkers() {
        List<Poi> pois = ContentResolverHelper.getPois(activity.getContentResolver());

        List<OverlayItem> items = pois.stream()
                .map(poi -> new OverlayItem(poi.getName(), poi.getResume(), new GeoPoint(poi.getLatitude(), poi.getLongitude())))
                .collect(Collectors.toList());

        overlayItemItemizedOverlay.removeAllItems();
        overlayItemItemizedOverlay.addItems(items);
    }

    /**
     * Enregistrement des paramètres de la carte avant fermeture
     */
    public void onPause() {
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
    }

    private void drawCircle(OverlayItem overlayItem, double radiusInMeters) {
        GeoPoint itemPosition = (GeoPoint) overlayItem.getPoint();
        // Générez les points du périmètre du cercle
        List<GeoPoint> circlePoints = generateCirclePerimeterPoints(itemPosition, radiusInMeters);
        // Ajoutez le cercle à la carte
        circleOverlay = new Polygon();
        // intérieur du cercle
        circleOverlay.getFillPaint().setColor(Color.TRANSPARENT);
        // bordure du cercle
        circleOverlay.getOutlinePaint().setColor(Color.BLUE);
        circleOverlay.getOutlinePaint().setStrokeWidth(2f);

        circleOverlay.setPoints(circlePoints);
        mapView.getOverlayManager().add(circleOverlay);
    }

    public void removeCircle() {
        if (circleOverlay != null) {
            mapView.getOverlayManager().remove(circleOverlay);
            circleOverlay = null;
        }
    }

    private List<GeoPoint> generateCirclePerimeterPoints(GeoPoint center, double radiusInMeters) {
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

    private void drawCircleWithPOIs(ArrayList<OverlayItem> items, OverlayItem center, double radiusInMeters) {
        overlayItemItemizedOverlay.removeAllItems(); // supprime tous les marqueurs de sites

        drawCircle(center, radiusInMeters); // Add the circle to the map

        GeoPoint centerPoint = (GeoPoint) center.getPoint();
        ArrayList<OverlayItem> overlayItems = new ArrayList<>();

        Location centerLocation = new Location("Center");
        centerLocation.setLatitude(centerPoint.getLatitude());
        centerLocation.setLongitude(centerPoint.getLongitude());

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

    public void centerToUserLocation() {
        if (myLocationNewOverlay == null || myLocationNewOverlay.getMyLocation() == null) {
            return;
        }

        myLocationNewOverlay.enableFollowLocation();
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
    }
}