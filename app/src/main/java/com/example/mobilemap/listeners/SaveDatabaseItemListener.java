package com.example.mobilemap.listeners;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.tables.DatabaseItem;
import com.example.mobilemap.database.interfaces.ItemView;

import java.text.MessageFormat;

/**
 * Ecouteur pour enregistrer un élément dans la base de données
 *
 * @author J.Houdé
 * @param <T> élément de la base de données qui étend DatabaseItem
 */
public class SaveDatabaseItemListener<T extends DatabaseItem> implements View.OnClickListener {
    private final AppCompatActivity activity;
    private final ItemView<T> fragment;
    private final ContentResolver contentResolver;
    private final Uri databaseUri;
    private final boolean launchedForResult;

    /**
     * Ecouteur pour enregistrer un élément dans la base de données
     *
     * @param activity          activité mère
     * @param fragment          fragement mère
     * @param databaseUri       uri de la table
     * @param launchedForResult si l'activité mère a été lancé par une autre activité pour une tâche
     */
    public SaveDatabaseItemListener(AppCompatActivity activity, ItemView<T> fragment, Uri databaseUri, boolean launchedForResult) {
        this.activity = activity;
        this.fragment = fragment;
        this.contentResolver = activity.getContentResolver();
        this.databaseUri = databaseUri;
        this.launchedForResult = launchedForResult;
    }

    @Override
    public void onClick(View v) {
        if (!fragment.check()) {
            return;
        }

        saveItem();

        if (launchedForResult) { // activité lancée pour un résultat
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
            return;
        }

        activity.getSupportFragmentManager().popBackStackImmediate();
    }

    /**
     * enregistre l'élément dans la base de données en créant ou modifiant l'élément
     */
    private void saveItem() {
        T databaseItem = fragment.getValues();

        if (databaseItem.getId() == DatabaseContract.NOT_EXISTING_ID) {
            contentResolver.insert(databaseUri, databaseItem.toContentValues());
            return;
        }

        contentResolver.update(databaseUri, databaseItem.toContentValues(),
                MessageFormat.format("{0} = {1}", BaseColumns._ID, databaseItem.getId()), null);
    }
}
