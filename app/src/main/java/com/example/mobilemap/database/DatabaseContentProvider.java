package com.example.mobilemap.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class DatabaseContentProvider extends ContentProvider {
    private DatabaseHelper databaseHelper;

    private String getTable(Uri uri) {
        return Objects.requireNonNull(uri.getPath()).substring(1);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        databaseHelper = new DatabaseHelper(context);

        return databaseHelper.getWritableDatabase() != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return databaseHelper.getReadableDatabase().query(getTable(uri), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // vnd.android.cursor.dir/site
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = databaseHelper.getReadableDatabase().insert(getTable(uri), null, values);

        if (id == -1) {
            throw new RuntimeException("Failed to insert row into " + uri);
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return databaseHelper.getWritableDatabase().delete(getTable(uri), selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return databaseHelper.getWritableDatabase().update(getTable(uri), values, selection, selectionArgs);
    }
}
