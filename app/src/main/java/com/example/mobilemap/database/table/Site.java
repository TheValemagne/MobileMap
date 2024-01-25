package com.example.mobilemap.database.table;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.example.mobilemap.database.DatabaseContract;

import java.text.MessageFormat;

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

    public static Site fromContentValues(ContentValues contentValues) {
        return new Site(contentValues.getAsString(DatabaseContract.Site.COLUMN_NAME),
                contentValues.getAsDouble(DatabaseContract.Site.COLUMN_LATITUDE),
                contentValues.getAsDouble(DatabaseContract.Site.COLUMN_LONGITUDE),
                contentValues.getAsString(DatabaseContract.Site.COLUMN_POSTAL_ADDRESS),
                contentValues.getAsLong(DatabaseContract.Site.COLUMN_CATEGORY_ID),
                contentValues.getAsString(DatabaseContract.Site.COLUMN_RESUME));
    }

    @NonNull
    @Override
    public String toString() {
        return MessageFormat.format("Site(_id={0}, name={1}, latitude={2}, longitude={3}, postalAddress={4}, categoryId={5}, resume={6})",
                id, name, latitude, longitude, postalAddress, categoryId, resume);
    }
}
