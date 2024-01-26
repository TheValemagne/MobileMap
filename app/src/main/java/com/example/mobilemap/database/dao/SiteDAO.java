package com.example.mobilemap.database.dao;

import android.database.Cursor;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.DatabaseHelper;
import com.example.mobilemap.database.table.Site;

import java.util.Optional;

public class SiteDAO extends DAO<Site> {

    public SiteDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper, DatabaseContract.Site.TABLE_NAME, DatabaseContract.Site.COLUMNS);
    }

    @Override
    public Optional<Site> mapCursor(Cursor cursor) {
        if (cursor == null)
            return Optional.empty();

        return Optional.of(Site.fromCursor(cursor));
    }

}
