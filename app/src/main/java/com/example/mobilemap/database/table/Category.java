package com.example.mobilemap.database.table;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.example.mobilemap.database.DatabaseContract;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe conteneur représentant la table SQL category
 */
public class Category extends DatabaseItem {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category(long id, String name) {
        super(id);
        this.name = name;
    }

    public Category(String name) {
        this(-1, name);
    }

    @SuppressLint("Range")
    public static Category fromCursor(Cursor cursor) {
        return new Category(
                cursor.getLong(cursor.getColumnIndex(DatabaseContract.Site.ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Site.COLUMN_NAME))
        );
    }

    @NonNull
    @Override
    public String toString() {
        return MessageFormat.format("Category(_id={0}, name={1})", id, name);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseContract.Site.COLUMN_NAME, getName());

        return contentValue;
    }

    public static List<Category> mapFromList(Cursor cursor) {
        List<Category> result = new ArrayList<>();

        if(!cursor.moveToFirst()) {
            return result;
        }

        do{
            result.add(Category.fromCursor(cursor));
        } while(cursor.moveToNext());

        return result;
    }
}
