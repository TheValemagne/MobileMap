package com.example.mobilemap.database.dao;

import android.database.Cursor;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.DatabaseHelper;
import com.example.mobilemap.database.table.Poi;

import java.util.Optional;

public class PoiDAO extends DAO<Poi> {

    public PoiDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper, DatabaseContract.Poi.TABLE_NAME, DatabaseContract.Poi.COLUMNS);
    }

    @Override
    public Optional<Poi> mapCursor(Cursor cursor) {
        if (cursor == null)
            return Optional.empty();

        return Optional.of(com.example.mobilemap.database.table.Poi.fromCursor(cursor));
    }

}
