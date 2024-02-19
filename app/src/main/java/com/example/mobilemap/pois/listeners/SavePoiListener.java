package com.example.mobilemap.pois.listeners;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemap.database.interfaces.ItemView;
import com.example.mobilemap.database.tables.Poi;
import com.example.mobilemap.listeners.SaveDatabaseItemListener;

/**
 * Ecoutouer pour enregistrer un site dans la base de données
 */
public class SavePoiListener extends SaveDatabaseItemListener<Poi> {
    public static final String CREATED_POI_LATITUDE = "createdPoiLatitude";
    public static final String CREATED_POI_LONGITUDE = "createdPoiLongitude";
    public static final String CREATED_POI_CATEGORY = "createdPoiCategory";

    private final boolean launchedForResult;
    /**
     * Ecoutouer pour enregistrer un site dans la base de données
     *
     * @param activity           activité mère
     * @param fragment           fragement mère
     * @param databaseUri        uri de la table
     * @param launchedForResult  si l'activité mère a été lancé par une autre activité pour une tâche
     */
    public SavePoiListener(AppCompatActivity activity, ItemView<Poi> fragment, Uri databaseUri, boolean launchedForResult) {
        super(activity, fragment, databaseUri);

        this.launchedForResult = launchedForResult;
    }

    @Override
    protected void afterSave() {
        if (launchedForResult) { // activité lancée pour un résultat
            activity.setResult(Activity.RESULT_OK, createIntent(fragment.getValues()));
            activity.finish();
            return;
        }

        activity.getSupportFragmentManager().popBackStackImmediate();
    }

    /**
     * Création de l'intent avec les données du site créé
     * @param poi site créé
     * @return intent avec les coordonnées et la catégorie du site
     */
    private Intent createIntent(Poi poi) {
        Intent intent = new Intent();
        intent.putExtra(CREATED_POI_LATITUDE, poi.getLatitude());
        intent.putExtra(CREATED_POI_LONGITUDE, poi.getLongitude());
        System.out.println(poi.getCategoryId());
        intent.putExtra(CREATED_POI_CATEGORY, poi.getCategoryId());

        return intent;
    }
}
