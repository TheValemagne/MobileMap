package com.example.mobilemap.database.tables;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.interfaces.HasId;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant la jointure entre la le nom d'un site et le nom de la catégorie
 */
public class PoiDetail implements HasId {
    private final long id;
    private final String siteName;

    public long getId() {
        return id;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    private final String categoryName;

    public PoiDetail(long id, String name, String categoryName) {
        this.id = id;
        this.siteName = name;
        this.categoryName = categoryName;
    }

    @SuppressLint("Range")
    public static PoiDetail fromCursor(Cursor cursor) {
        return new PoiDetail(
                cursor.getLong(cursor.getColumnIndex(DatabaseContract.PoiDetail._ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.PoiDetail.COLUMN_SITE_NAME)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.PoiDetail.COLUMN_CATEGORY_NAME))
        );
    }

    /**
     * Convertie le cursor en liste de sites détaillées
     *
     * @param cursor résultat de la requête à convertir
     * @return une liste de sites détaillées
     */
    public static List<PoiDetail> mapFromList(Cursor cursor) {
        List<PoiDetail> result = new ArrayList<>();

        if(!cursor.moveToFirst()) {
            return result;
        }

        do{
            result.add(PoiDetail.fromCursor(cursor));
        } while(cursor.moveToNext());

        return result;
    }
}
