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
    private double latitude;
    private double longitude;
    private String postalAddress;
    private long categoryId;
    private String resume;

    /**
     * Constructeur d'un site existant
     *
     * @param id            identifiant unique du site
     * @param name          nom du site
     * @param latitude      latitude du site (-90.0 à 90.0)
     * @param longitude     longitude du site (-180.0 à 180.0)
     * @param postalAddress adresse postale du site
     * @param categoryId    catégorie associée au site
     * @param resume        résumé / description du site
     */
    public Poi(long id, String name, double latitude, double longitude, String postalAddress, long categoryId, String resume) {
        super(id);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postalAddress = postalAddress;
        this.categoryId = categoryId;
        this.resume = resume;
    }

    /**
     * Constructeur d'un site à enregister
     *
     * @param name          nom de la catégorie
     * @param latitude      latitude du site (-90.0 à 90.0)
     * @param longitude     longitude du site (-180.0 à 180.0)
     * @param postalAddress adresse postale du site
     * @param categoryId    catégorie associée au site
     * @param resume        résumé / description du site
     */
    public Poi(String name, double latitude, double longitude, String postalAddress, long categoryId, String resume) {
        this(-1, name, latitude, longitude, postalAddress, categoryId, resume);
    }

    @NonNull
    @Override
    public String toString() {
        return MessageFormat.format("Site(_id={0}, name={1}, latitude={2}, longitude={3}, postalAddress={4}, categoryId={5}, resume={6})",
                id, name, latitude, longitude, postalAddress, categoryId, resume);
    }

    /**
     * Retourne le nom du site
     *
     * @return nom du site
     */
    public String getName() {
        return name;
    }

    /**
     * Modifie le nom du site
     *
     * @param name nouveau nom du site
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retourne la latitude du site
     * @return latitude du site
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Modifie la latitude du site
     * @param latitude latitude du site, un chiffre dans [-90.0, 90.0]
     */
    public void setLatitude(@FloatRange(from = -90.0, to = 90.0) double latitude) {
        this.latitude = latitude;
    }

    /**
     * Retourne la longitude du site
     * @return longitude du site
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Modifie la longitude du site
     * @param longitude longitude du site, un chiffre dans [-180.0, 180.0]
     */
    public void setLongitude(@FloatRange(from = -180.0, to = 180.0) double longitude) {
        this.longitude = longitude;
    }

    /**
     * Retourne l'adresse postale du site
     * @return adresse postale du site
     */
    public String getPostalAddress() {
        return postalAddress;
    }

    /**
     * Modifie l'adresse postale du site
     * @param postalAddress nouvelle adresse postale
     */
    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    /**
     * Retourne l'identifiant de la catégorie du site
     * @return identifiant de la catégorie du site
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Modifie l'identifiant de la catégorie du site
     * @param categoryId nouvelle identifiant de la catégorie du site
     */
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Retourne le résumé du site
     * @return résumé du site
     */
    public String getResume() {
        return resume;
    }

    /**
     * Modifie le résumé du site
     * @param resume résumé du site
     */
    public void setResume(String resume) {
        this.resume = resume;
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
