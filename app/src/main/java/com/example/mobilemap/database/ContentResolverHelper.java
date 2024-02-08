package com.example.mobilemap.database;

import android.content.ContentResolver;
import android.database.Cursor;

import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.database.tables.Poi;
import com.example.mobilemap.database.tables.PoiDetail;

import java.util.List;

public class ContentResolverHelper {
    /**
     * Retourne la liste des catégories
     *
     * @param contentResolver gestionnaire de la base de données
     * @return liste des caégories
     */
    public static List<Category> getCategories(ContentResolver contentResolver) {
        Cursor cursor = contentResolver
                .query(DatabaseContract.Category.CONTENT_URI, DatabaseContract.Category.COLUMNS,
                        null, null, DatabaseContract.Category.COLUMN_NAME + " COLLATE LOCALIZED ASC");
        assert cursor != null;

        return Category.mapFromList(cursor);
    }

    /**
     * Retourne la liste des sites
     *
     * @param contentResolver gestionnaire de la base de données
     * @return liste des sites
     */
    public static List<PoiDetail> getPoisDetail(ContentResolver contentResolver) {
        Cursor cursor = contentResolver
                .query(DatabaseContract.PoiDetail.CONTENT_URI, DatabaseContract.PoiDetail.COLUMNS,
                        null, null, DatabaseContract.PoiDetail.COLUMN_SITE_NAME + " COLLATE LOCALIZED ASC");
        assert cursor != null;

        return PoiDetail.mapFromList(cursor);
    }

    /**
     * Retourne la liste des sites détaillés
     *
     * @param contentResolver gestionnaire de la base de données
     * @return liste des sites détaillés
     */
    public static List<Poi> getPois(ContentResolver contentResolver) {
        Cursor cursor = contentResolver
                .query(DatabaseContract.Poi.CONTENT_URI, DatabaseContract.Poi.COLUMNS,
                        null, null, DatabaseContract.Poi.COLUMN_NAME + " COLLATE LOCALIZED ASC");
        assert cursor != null;

        return Poi.mapFromList(cursor);
    }
}
