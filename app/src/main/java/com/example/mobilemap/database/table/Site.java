package com.example.mobilemap.database.table;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.example.mobilemap.database.DatabaseContract;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class Site extends DatabaseItem {
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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private double longitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
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

    public Site(long id, String name, double latitude, double longitude, String postalAddress, long categoryId, String resume) {
        super(id);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postalAddress = postalAddress;
        this.categoryId = categoryId;
        this.resume = resume;
    }

    public Site(String name, double latitude, double longitude, String postalAddress, long categoryId, String resume) {
        this(-1, name, latitude, longitude, postalAddress, categoryId, resume);
    }

    @SuppressLint("Range")
    public static Site fromCursor(Cursor cursor) {
        return new Site(
                cursor.getLong(cursor.getColumnIndex(DatabaseContract.Site.ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_NAME)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_LONGITUDE)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_POSTAL_ADDRESS)),
                cursor.getLong(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_CATEGORY_ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_RESUME))
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
        contentValues.put(DatabaseContract.Site.COLUMN_NAME, getName());
        contentValues.put(DatabaseContract.Site.COLUMN_LATITUDE, getLatitude());
        contentValues.put(DatabaseContract.Site.COLUMN_LONGITUDE, getLongitude());
        contentValues.put(DatabaseContract.Site.COLUMN_POSTAL_ADDRESS, getPostalAddress());
        contentValues.put(DatabaseContract.Site.COLUMN_CATEGORY_ID, getCategoryId());
        contentValues.put(DatabaseContract.Site.COLUMN_RESUME, getResume());

        return contentValues;
    }

    public static List<Site> mapFromList(Cursor cursor) {
        List<Site> result = new ArrayList<>();

        if(!cursor.moveToFirst()) {
            return result;
        }

        do{
            result.add(Site.fromCursor(cursor));
        } while(cursor.moveToNext());

        return result;
    }
}
