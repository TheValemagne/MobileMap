package com.example.mobilemap.database.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.mobilemap.database.DatabaseHelper;
import com.example.mobilemap.database.table.DatabaseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DAO<T extends DatabaseItem> {
    protected DatabaseHelper databaseHelper;
    protected final String tableName;
    protected final String[] columns;
    public DAO(DatabaseHelper databaseHelper, String tableName, String[] columns) {
        this.databaseHelper = databaseHelper;
        this.tableName = tableName;
        this.columns = columns;

    }

    public abstract Optional<T> mapCursor(Cursor cursor);
    public Optional<T> get(long id) {
        Cursor cursor = this.databaseHelper.getReadableDatabase().rawQuery("SELECT * FROM " + tableName + " WHERE _ID = " + id, null);
        cursor.moveToNext();

        return mapCursor(cursor);
    }

    public List<T> getAll() {
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

    protected Cursor getItems(String[] columns) {
        return databaseHelper.getReadableDatabase()
                .query(tableName, columns, null, null, null, null, null);
    }

    public abstract ContentValues mapItem(T item);

    public void insert(T item) {
        long id = databaseHelper.getWritableDatabase()
                .insert(tableName, null, mapItem(item));
        item.setId(id);
    }

    public int update(T item){
        return databaseHelper.getWritableDatabase()
                .update(tableName, mapItem(item), " _id = " + item.getId(), null);
    }

    public void delete(long id) {
        databaseHelper.getWritableDatabase()
                .delete(tableName,  "_id =" + id, null);
    }
}
