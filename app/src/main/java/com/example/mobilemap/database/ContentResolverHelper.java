package com.example.mobilemap.database;

import android.content.ContentResolver;
import android.database.Cursor;

import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.database.table.PoiDetail;

import java.util.List;

public class ContentResolverHelper {
    public static List<Category> getCategories(ContentResolver contentResolver) {
        Cursor cursor = contentResolver
                .query(DatabaseContract.Category.CONTENT_URI, DatabaseContract.Category.COLUMNS,
                        null, null, DatabaseContract.Category.COLUMN_NAME + " COLLATE LOCALIZED ASC");
        assert cursor != null;

        return Category.mapFromList(cursor);
    }

    public static List<PoiDetail> getPois(ContentResolver contentResolver) {
        Cursor cursor = contentResolver
                .query(DatabaseContract.PoiDetail.CONTENT_URI, DatabaseContract.PoiDetail.COLUMNS,
                        null, null, DatabaseContract.PoiDetail.COLUMN_SITE_NAME + " COLLATE LOCALIZED ASC");
        assert cursor != null;

        return PoiDetail.mapFromList(cursor);
    }
}
