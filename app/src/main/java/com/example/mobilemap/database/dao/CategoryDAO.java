package com.example.mobilemap.database.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.DatabaseHelper;
import com.example.mobilemap.database.table.Category;

import java.util.Optional;

public class CategoryDAO extends DAO<Category> {
    public CategoryDAO(DatabaseHelper databaseHelper) {
        super(databaseHelper, DatabaseContract.Category.TABLE_NAME, DatabaseContract.Category.COLUMNS);
    }

    @SuppressLint("Range")
    @Override
    public Optional<Category> mapCursor(Cursor cursor) {
        if (cursor == null)
            return Optional.empty();

        return Optional.of(new Category(
                cursor.getLong(cursor.getColumnIndex(DatabaseContract.Site.ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_NAME))
        ));
    }

    @Override
    public ContentValues mapItem(Category item) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseContract.Site.COLUMN_NAME, item.getName());

        return contentValue;
    }
}
