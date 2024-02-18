package com.example.mobilemap.database.tables;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import com.example.mobilemap.database.DatabaseContract;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe conteneur représentant la table SQL Point of Interest (POI) ou point d'intérêt sur la carte
 *
 * @author J.Houdé
 */
public class Poi extends DatabaseItem {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private double latitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(@FloatRange(from = -90.0, to = 90.0) double latitude) {
        this.latitude = latitude;
    }

    private double longitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(@FloatRange(from = -180.0, to = 180.0) double longitude) {
        this.longitude = longitude;
    }

    private String postalAddress;

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    private long categoryId;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    private String resume;

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Poi(long id, String name, double latitude, double longitude, String postalAddress, long categoryId, String resume) {
        super(id);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postalAddress = postalAddress;
        this.categoryId = categoryId;
        this.resume = resume;
    }

    public Poi(String name, double latitude, double longitude, String postalAddress, long categoryId, String resume) {
        this(-1, name, latitude, longitude, postalAddress, categoryId, resume);
    }

    /**
     * Convertie un cursor en site
     *
     * @param cursor résultat de la requête à convertir
     * @return le site  requêté
     */
    @SuppressLint("Range")
    public static Poi fromCursor(Cursor cursor) {
        return new Poi(
                cursor.getLong(cursor.getColumnIndex(DatabaseContract.Poi._ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Poi.COLUMN_NAME)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Poi.COLUMN_LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Poi.COLUMN_LONGITUDE)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Poi.COLUMN_POSTAL_ADDRESS)),
                cursor.getLong(cursor.getColumnIndex(DatabaseContract.Poi.COLUMN_CATEGORY_ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Poi.COLUMN_RESUME))
        );
    }

    @NonNull
    @Override
    public String toString() {
        return MessageFormat.format("Site(_id={0}, name={1}, latitude={2}, longitude={3}, postalAddress={4}, categoryId={5}, resume={6})",
                id, name, latitude, longitude, postalAddress, categoryId, resume);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.Poi.COLUMN_NAME, getName());
        contentValues.put(DatabaseContract.Poi.COLUMN_LATITUDE, getLatitude());
        contentValues.put(DatabaseContract.Poi.COLUMN_LONGITUDE, getLongitude());
        contentValues.put(DatabaseContract.Poi.COLUMN_POSTAL_ADDRESS, getPostalAddress());
        contentValues.put(DatabaseContract.Poi.COLUMN_CATEGORY_ID, getCategoryId());
        contentValues.put(DatabaseContract.Poi.COLUMN_RESUME, getResume());

        return contentValues;
    }

    /**
     * Convertie le cursor en liste de sites
     *
     * @param cursor résultat de la requête à convertir
     * @return une liste de catégories
     */
    public static List<Poi> mapFromList(Cursor cursor) {
        List<Poi> result = new ArrayList<>();

        if (!cursor.moveToFirst()) {
            return result;
        }

        do {
            result.add(Poi.fromCursor(cursor));
        } while (cursor.moveToNext());

        return result;
    }
}
