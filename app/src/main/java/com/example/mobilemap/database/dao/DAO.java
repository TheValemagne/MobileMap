package com.example.mobilemap.database.dao;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.example.mobilemap.database.DatabaseHelper;
import com.example.mobilemap.database.table.DatabaseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DAO<T extends DatabaseItem> {
    private final DatabaseHelper databaseHelper;
    private final String tableName;
    private final String[] columns;

    public DAO(DatabaseHelper databaseHelper, String tableName, String[] columns) {
        this.databaseHelper = databaseHelper;
        this.tableName = tableName;
        this.columns = columns;
    }

    protected abstract Optional<T> mapCursor(Cursor cursor);
    public Optional<T> find(long id) {
        Cursor cursor = this.databaseHelper.getReadableDatabase()
                .query(tableName, columns, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToNext();

        return mapCursor(cursor);
    }

    public List<T> findAll() {
        Cursor cursor = getItems(columns);
        List<T> result = new ArrayList<>();

        if(!cursor.moveToFirst()) {
            return result;
        }

        do{
            Optional<T> item = mapCursor(cursor);
            item.ifPresent(result::add);
        } while(cursor.moveToNext());

        return result;
    }

    private Cursor getItems(String[] columns) {
        return databaseHelper.getReadableDatabase()
                .query(tableName, columns, null, null, null, null, null);
    }

    public void insert(T item) {
        long id = databaseHelper.getWritableDatabase()
                .insert(tableName, null, item.toContentValues());
        item.setId(id);
    }

    public int update(T item){
        return databaseHelper.getWritableDatabase()
                .update(tableName, item.toContentValues(), BaseColumns._ID + " =" + item.getId(), null);
    }

    public void delete(long id) {
        databaseHelper.getWritableDatabase()
                .delete(tableName, BaseColumns._ID + " =" + id, null);
    }
}
