package com.example.mobilemap.database.tables;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.interfaces.HasId;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant la jointure entre la le nom d'un site et le nom de la catégorie
 *
 * @author J.Houdé
 */
public class PoiDetail implements HasId {
    private final long id;
    private final String siteName;
    private final String categoryName;

    /**
     * @param id           identifiant unique du site
     * @param name         nom du site
     * @param categoryName nom de la catégorie
     */
    public PoiDetail(long id, String name, String categoryName) {
        this.id = id;
        this.siteName = name;
        this.categoryName = categoryName;
    }

    public long getId() {
        return id;
    }

    /**
     * Retourne le nom du site
     * @return nom du site
     */
    public String getSiteName() {
        return siteName;
    }

    /**
     * Retourne le nom de la catégorie du site
     * @return nom de la catégorie du site
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Convertie le cursor en site détaillée
     *
     * @param cursor résultat de la requête à convertir
     * @return site détaillé
     */
    @SuppressLint("Range")
    public static PoiDetail fromCursor(Cursor cursor) {
        return new PoiDetail(
                cursor.getLong(cursor.getColumnIndex(DatabaseContract.PoiDetail._ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.PoiDetail.COLUMN_SITE_NAME)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.PoiDetail.COLUMN_CATEGORY_NAME))
        );
    }

    /**
     * Convertie le cursor en liste de sites détaillés
     *
     * @param cursor résultat de la requête à convertir
     * @return une liste de sites détaillés
     */
    public static List<PoiDetail> mapFromList(Cursor cursor) {
        List<PoiDetail> result = new ArrayList<>();

        if (!cursor.moveToFirst()) {
            return result;
        }

        do {
            result.add(PoiDetail.fromCursor(cursor));
        } while (cursor.moveToNext());

        return result;
    }
}
