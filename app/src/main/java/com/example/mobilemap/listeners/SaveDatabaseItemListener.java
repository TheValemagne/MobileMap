package com.example.mobilemap.listeners;

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
 * @param <T> élément de la base de données qui étend DatabaseItem
 * @author J.Houdé
 */
public class SaveDatabaseItemListener<T extends DatabaseItem> implements View.OnClickListener {
    protected final AppCompatActivity activity;
    protected final ItemView<T> fragment;
    private final ContentResolver contentResolver;
    private final Uri databaseUri;

    /**
     * Ecouteur pour enregistrer un élément dans la base de données
     *
     * @param activity    activité gérant les données
     * @param fragment    fragement gérant les données
     * @param databaseUri uri de la table
     */
    public SaveDatabaseItemListener(AppCompatActivity activity, ItemView<T> fragment, Uri databaseUri) {
        this.activity = activity;
        this.fragment = fragment;
        this.contentResolver = activity.getContentResolver();
        this.databaseUri = databaseUri;
    }

    @Override
    public void onClick(View v) {
        if (!fragment.isValid()) {
            return;
        }

        saveItem();
        afterSave();
    }

    /**
     * enregistre l'élément dans la base de données en créant ou modifiant l'élément
     */
    private void saveItem() {
        T databaseItem = fragment.getValues();

        if (databaseItem.getId() == DatabaseContract.NOT_EXISTING_ID) { // ajout d'un nouveau contenu dans la base de données
            contentResolver.insert(databaseUri, databaseItem.toContentValues());
            return;
        }

        // mise à jour d'un contneu existant dans la base de données
        contentResolver.update(databaseUri, databaseItem.toContentValues(),
                MessageFormat.format("{0} = {1}", BaseColumns._ID, databaseItem.getId()), null);
    }

    /**
     * Action réalisé après l'enregistrement
     */
    protected void afterSave() {
        activity.getSupportFragmentManager().popBackStackImmediate();
    }
}
