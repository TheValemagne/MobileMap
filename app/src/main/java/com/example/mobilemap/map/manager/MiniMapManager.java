package com.example.mobilemap.map.manager;

import androidx.core.content.res.ResourcesCompat;

import com.example.mobilemap.R;
import com.example.mobilemap.database.tables.Poi;
import com.example.mobilemap.map.PoiInfoWindow;
import com.example.mobilemap.map.SharedPreferencesConstant;
import com.example.mobilemap.pois.PoisActivity;
import com.example.mobilemap.pois.fragments.PoiFragment;
import com.example.mobilemap.validators.FieldValidator;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;
import java.util.Objects;

/**
 * Gestionnaire de mini carte d'illustration d'un marqueur
 *
 * @author J.Houdé
 */
public class MiniMapManager {
    private static final double MIN_ZOOM = 9.0;
    private static final double MAX_ZOOM = 20.0;
    private static final double DEFAULT_ZOOM = 18.5;
    private final PoisActivity activity;
    private final PoiFragment fragment;
    private final MapView miniMapView;
    private final List<FieldValidator> coordinatesFieldValidators;
    private Marker marker;

    /**
     * Gestionnaire de mini carte d'illustration d'un marqueur
     *
     * @param activity                   activité gérant les sites
     * @param fragment                   fragment gérent le détail d'un site
     * @param miniMapView                vue de la carte à manipuler
     * @param coordinatesFieldValidators liste des validateurs des champs de coordonnées
     */
    public MiniMapManager(PoisActivity activity, PoiFragment fragment, MapView miniMapView, List<FieldValidator> coordinatesFieldValidators) {
        this.activity = activity;
        this.fragment = fragment;
        this.miniMapView = miniMapView;
        this.coordinatesFieldValidators = coordinatesFieldValidators;
    }

    /**
     * Initialisation de la mini carte
     */
    public void initMap() {
        MapManager.initMapDefaultSettings(miniMapView);

        miniMapView.setHorizontalMapRepetitionEnabled(false);
        miniMapView.setMinZoomLevel(MIN_ZOOM);
        miniMapView.setMaxZoomLevel(MAX_ZOOM);
        miniMapView.getController().setZoom(DEFAULT_ZOOM);

        GeoPoint initialCenter = new GeoPoint(Double.parseDouble(SharedPreferencesConstant.DEFAULT_LATITUDE),
                Double.parseDouble(SharedPreferencesConstant.DEFAULT_LONGITUDE));
        miniMapView.setScrollableAreaLimitDouble(new BoundingBox(initialCenter.getLatitude(), initialCenter.getLongitude(),
                initialCenter.getLatitude(), initialCenter.getLongitude()));

        // intialisation du marqueur d'illustration
        marker = new Marker(miniMapView);
        marker.setIcon(Objects.requireNonNull(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.small_marker, activity.getTheme())));
        marker.setInfoWindow(new PoiInfoWindow(R.layout.poi_info_window, miniMapView));
        miniMapView.getOverlays().add(marker);
    }

    /**
     * Actualisation de la mini carte
     */
    public void updateMap() {
        if (coordinatesFieldValidators.stream().anyMatch(fieldValidator -> !fieldValidator.isValid())) {
            return; // l'une des coordonnées n'est pas valide
        }

        // uniquement si les deux champs sont valides
        Poi modifiedPoi = fragment.getValues();
        GeoPoint point = new GeoPoint(modifiedPoi.getLatitude(), modifiedPoi.getLongitude());

        miniMapView.setScrollableAreaLimitDouble(new BoundingBox(modifiedPoi.getLatitude(), modifiedPoi.getLongitude(),
                modifiedPoi.getLatitude(), modifiedPoi.getLongitude()));
        miniMapView.setExpectedCenter(point);

        // actualisation du marqueur d'illustration
        updateMarker(modifiedPoi, point);

        miniMapView.invalidate();
    }

    /**
     * actualisation du marqueur d'illustration
     *
     * @param modifiedPoi site avec contenu actualiser
     * @param point       nouvelle position du marqueur
     */
    private void updateMarker(Poi modifiedPoi, GeoPoint point) {
        marker.setTitle(modifiedPoi.getName());
        marker.setSnippet(modifiedPoi.getResume());
        marker.setPosition(point);
        marker.showInfoWindow();
    }

}
