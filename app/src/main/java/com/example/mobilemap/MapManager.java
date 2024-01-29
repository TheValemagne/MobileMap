package com.example.mobilemap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;

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
import java.util.Arrays;
import java.util.List;

public class MapManager {
    private final MapView mapView;
    private final Context context;
    private Polygon circleOverlay;
    private ItemizedIconOverlay<OverlayItem> overlayItemItemizedOverlay;
    private final static int MAX_CIRCLE_POINTS = 360;

    public MapManager(MapView mapView, Context context) {
        this.mapView = mapView;
        this.context = context;
    }

    public void initMap() {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getZoomController()
                .setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapView.setMultiTouchControls(true);
        mapView.setMinZoomLevel(12.0);

        mapView.setHorizontalMapRepetitionEnabled(false);
        mapView.setVerticalMapRepetitionEnabled(false);

        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
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

        List<OverlayItem> overlayItems = new ArrayList<>(Arrays.asList(
                new OverlayItem("ISFATES Metz", "ISFATES", new GeoPoint(49.094168, 6.230186)),
                new OverlayItem("UFR MIM Metz", "MIM", new GeoPoint(49.0946557, 6.2297174))
        ));

        Drawable drawable = overlayItems.get(0).getMarker(0);

        overlayItemItemizedOverlay = new ItemizedIconOverlay<>(context, overlayItems, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
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

        mapView.getOverlays().add(overlayItemItemizedOverlay);
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

        for (int angleInDegree = 0; angleInDegree < MAX_CIRCLE_POINTS; angleInDegree += 5) {
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
}
