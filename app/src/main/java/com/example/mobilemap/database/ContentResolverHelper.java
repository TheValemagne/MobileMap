package com.example.mobilemap.database;

import android.content.ContentResolver;
import android.database.Cursor;

import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.database.tables.Poi;
import com.example.mobilemap.database.tables.PoiDetail;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'aide pour ContentResolver
 *
 * @author J.Houdé
 */
public class ContentResolverHelper {
    private static final String SORT_LOCALIZED_ASC = " COLLATE LOCALIZED ASC";
    private static final String SORT_PATTERN = "{0} {1}";

    /**
     * Retourne l'argument poyr le trie alphanumérique par rapport à une colonne en ignorant les accents
     *
     * @param columnName nom de la colonne pour le trie
     * @return argument de triage SQLite
     */
    private static String getSortBy(String columnName) {
        return MessageFormat.format(SORT_PATTERN, columnName, SORT_LOCALIZED_ASC);
    }

    /**
     * Retourne la liste des catégories
     *
     * @param contentResolver gestionnaire de la base de données
     * @return liste des caégories
     */
    public static List<Category> getCategories(ContentResolver contentResolver) {
        Cursor cursor = contentResolver
                .query(DatabaseContract.Category.CONTENT_URI, DatabaseContract.Category.COLUMNS, null, null,
                        getSortBy(DatabaseContract.Category.COLUMN_NAME)); // triée par nom

        if(cursor == null) {
            return new ArrayList<>();
        }

        return Category.mapFromList(cursor);
    }

    /**
     * Retourne la liste des sites détaillés
     *
     * @param contentResolver gestionnaire de la base de données
     * @return liste des sites détaillés
     */
    public static List<PoiDetail> getPoisDetail(ContentResolver contentResolver) {
        Cursor cursor = contentResolver
                .query(DatabaseContract.PoiDetail.CONTENT_URI, DatabaseContract.PoiDetail.COLUMNS, null, null,
                        getSortBy(DatabaseContract.PoiDetail.COLUMN_SITE_NAME));  // triée par nom

        if(cursor == null) {
            return new ArrayList<>();
        }

        return PoiDetail.mapFromList(cursor);
    }

    /**
     * Retourne la liste des sites
     *
     * @param contentResolver gestionnaire de la base de données
     * @return liste des sites
     */
    public static List<Poi> getPois(ContentResolver contentResolver) {
        Cursor cursor = contentResolver
                .query(DatabaseContract.Poi.CONTENT_URI, DatabaseContract.Poi.COLUMNS, null, null,
                        getSortBy(DatabaseContract.Poi.COLUMN_NAME));  // triée par nom

        if(cursor == null) {
            return new ArrayList<>();
        }

        return Poi.mapFromList(cursor);
    }
}
