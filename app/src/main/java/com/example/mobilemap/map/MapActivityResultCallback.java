package com.example.mobilemap.map;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;

import com.example.mobilemap.pois.listeners.SavePoiListener;

import org.osmdroid.util.GeoPoint;

/**
 * Callback pour traiter le résultat de l'activité de gestion des sites
 *
 * @author J.Houdé
 */
public class MapActivityResultCallback implements ActivityResultCallback<ActivityResult> {
    private final MapManager mapManager;

    public MapActivityResultCallback(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    @Override
    public void onActivityResult(ActivityResult result) {
        if (result.getResultCode() != Activity.RESULT_OK) {
            return;
        }

        Intent intent = result.getData();

        if (mapManager.hasSavedCircle()
                && intent != null
                && intent.getExtras() != null
                && shouldRemoveCircle(intent.getExtras())) {
            mapManager.removeCircle(); // supprime le ercle si le nouveau site ne correspond pas au filter actuellement appliqué
        }

        mapManager.updateMap(); // actuaisation de la carte
    }

    /**
     * Vérifie s'il faut supprimer le cercle actuellement affiché
     *
     * @param bundle contenu du dernier site ajouté sur la carte
     * @return vrai si le cercle doit être supprimé, sinon faux
     */
    private boolean shouldRemoveCircle(Bundle bundle) {
        GeoPoint center = mapManager.getCircleManager().getCircleCenter();
        Location centerLocation = convertToLocation("center", center.getLatitude(), center.getLongitude());
        Location point = convertToLocation("point", bundle.getDouble(SavePoiListener.CREATED_POI_LATITUDE), bundle.getDouble(SavePoiListener.CREATED_POI_LONGITUDE));

        return point.distanceTo(centerLocation) > mapManager.getCircleManager().getCircleRadius() ||
                mapManager.getCircleManager().getCategoryFilter() != bundle.getLong(SavePoiListener.CREATED_POI_CATEGORY, -1);
    }

    /**
     * Créé une location
     * @param provider nom du provider
     * @param latitude latitude de la localisation
     * @param longitude longitude de la localisation
     * @return retourne une localisation avec les données fournies
     */
    private Location convertToLocation(String provider, double latitude, double longitude) {
        Location location = new Location(provider);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }
}
