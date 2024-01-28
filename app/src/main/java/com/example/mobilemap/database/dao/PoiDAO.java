package com.example.mobilemap.database.dao;

import android.database.Cursor;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.DatabaseHelper;
import com.example.mobilemap.database.table.Poi;

import java.util.Optional;

public class PoiDAO extends DAO<Poi> {

    public PoiDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper, DatabaseContract.Site.TABLE_NAME, DatabaseContract.Site.COLUMNS);
    }

    @Override
    public Optional<Poi> mapCursor(Cursor cursor) {
        if (cursor == null)
            return Optional.empty();

        return Optional.of(Poi.fromCursor(cursor));
    }

}
