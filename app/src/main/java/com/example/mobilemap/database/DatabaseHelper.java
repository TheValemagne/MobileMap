package com.example.mobilemap.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + DatabaseContract.Site.TABLE_NAME + " (" +
                DatabaseContract.Site.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseContract.Site.COLUMN_NAME + " text NOT NULL," +
                DatabaseContract.Site.COLUMN_LATITUDE + " REAL NOT NULL," +
                DatabaseContract.Site.COLUMN_LONGITUDE + " REAL NOT NULL," +
                DatabaseContract.Site.COLUMN_POSTAL_ADDRESS + " text NOT NULL," +
                DatabaseContract.Site.COLUMN_CATEGORY_ID + " INTEGER NOT NULL," +
                DatabaseContract.Site.COLUMN_RESUME + " text NOT NULL," +
                "FOREIGN KEY(" + DatabaseContract.Site.COLUMN_CATEGORY_ID + ") REFERENCES "
                + DatabaseContract.Category.TABLE_NAME + "(" + DatabaseContract.Category.ID + ") ON DELETE CASCADE);");

        sqLiteDatabase.execSQL("CREATE TABLE " + DatabaseContract.Category.TABLE_NAME + " (" +
                DatabaseContract.Category.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseContract.Category.COLUMN_NAME + " text NOT NULL UNIQUE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        migrateDatabase(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        migrateDatabase(sqLiteDatabase);
    }

    private void migrateDatabase(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Site.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Category.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
