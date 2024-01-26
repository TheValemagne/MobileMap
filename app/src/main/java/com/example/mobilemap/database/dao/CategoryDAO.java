package com.example.mobilemap.database.dao;

import android.database.Cursor;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.DatabaseHelper;
import com.example.mobilemap.database.table.Category;

import java.util.Optional;

public class CategoryDAO extends DAO<Category> {
    public CategoryDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper, DatabaseContract.Category.TABLE_NAME, DatabaseContract.Category.COLUMNS);
    }

    @Override
    public Optional<Category> mapCursor(Cursor cursor) {
        if (cursor == null)
            return Optional.empty();

        return Optional.of(Category.fromCursor(cursor));
    }

}
