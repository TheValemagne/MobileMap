package com.example.mobilemap.database.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.DatabaseHelper;
import com.example.mobilemap.database.table.Site;

import java.util.Optional;

public class SiteDAO extends DAO<Site> {

    public SiteDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper, DatabaseContract.Site.TABLE_NAME, DatabaseContract.Site.COLUMNS);
    }

    @SuppressLint("Range")
    @Override
    public Optional<Site> mapCursor(Cursor cursor) {
        if (cursor == null)
            return Optional.empty();

        return Optional.of(new Site(
                cursor.getLong(cursor.getColumnIndex(DatabaseContract.Site.ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_NAME)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_LONGITUDE)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_POSTAL_ADDRESS)),
                cursor.getLong(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_CATEGORY_ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_RESUME))
        ));
    }

    @Override
    public ContentValues mapItem(Site item) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseContract.Site.COLUMN_NAME, item.getName());
        contentValue.put(DatabaseContract.Site.COLUMN_LATITUDE, item.getLatitude());
        contentValue.put(DatabaseContract.Site.COLUMN_LONGITUDE, item.getLongitude());
        contentValue.put(DatabaseContract.Site.COLUMN_POSTAL_ADDRESS, item.getPostalAddress());
        contentValue.put(DatabaseContract.Site.COLUMN_CATEGORY_ID, item.getCategoryId());
        contentValue.put(DatabaseContract.Site.COLUMN_RESUME, item.getResume());

        return contentValue;
    }

}
