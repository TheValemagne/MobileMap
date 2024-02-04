package com.example.mobilemap.database.table;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.interfaces.HasId;

import java.util.ArrayList;
import java.util.List;

public class PoiDetail implements HasId {
    private final long id;
    private final String name;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    private final String categoryName;

    public PoiDetail(long id, String name, String categoryName) {
        this.id = id;
        this.name = name;
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
