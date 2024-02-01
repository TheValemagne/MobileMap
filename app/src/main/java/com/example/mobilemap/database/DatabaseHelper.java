package com.example.mobilemap.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.database.table.Poi;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(@Nullable Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + DatabaseContract.Poi.TABLE_NAME + " (" +
                DatabaseContract.Poi._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseContract.Poi.COLUMN_NAME + " text NOT NULL," +
                DatabaseContract.Poi.COLUMN_LATITUDE + " REAL NOT NULL," +
                DatabaseContract.Poi.COLUMN_LONGITUDE + " REAL NOT NULL," +
                DatabaseContract.Poi.COLUMN_POSTAL_ADDRESS + " text NOT NULL," +
                DatabaseContract.Poi.COLUMN_CATEGORY_ID + " INTEGER NOT NULL," +
                DatabaseContract.Poi.COLUMN_RESUME + " text NOT NULL," +
                "FOREIGN KEY(" + DatabaseContract.Poi.COLUMN_CATEGORY_ID + ") REFERENCES "
                + DatabaseContract.Category.TABLE_NAME + "(" + DatabaseContract.Category._ID + ") ON DELETE CASCADE);");

        sqLiteDatabase.execSQL("CREATE TABLE " + DatabaseContract.Category.TABLE_NAME + " (" +
                DatabaseContract.Category._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseContract.Category.COLUMN_NAME + " text NOT NULL UNIQUE);");

        sqLiteDatabase.execSQL("CREATE VIEW "+ DatabaseContract.PoiDetail.TABLE_NAME + " \n" +
                "AS SELECT S." + DatabaseContract.Poi._ID + " AS " + DatabaseContract.PoiDetail._ID +
                ", S." + DatabaseContract.Poi.COLUMN_NAME + " AS " + DatabaseContract.PoiDetail.COLUMN_SITE_NAME + ", " +
                "C." + DatabaseContract.Category.COLUMN_NAME + " AS " + DatabaseContract.PoiDetail.COLUMN_CATEGORY_NAME +
                " FROM " + DatabaseContract.Poi.TABLE_NAME + " AS S INNER JOIN "
                + DatabaseContract.Category.TABLE_NAME + " AS C ON S." + DatabaseContract.Poi.COLUMN_CATEGORY_ID + " = C." + DatabaseContract.Category._ID + ";");

        sqLiteDatabase.execSQL("INSERT INTO " + DatabaseContract.Category.TABLE_NAME + "(" + DatabaseContract.Category.COLUMN_NAME + ")" +
                "VALUES ('Auberge'), ('Bar'), ('Château'), ('Eglise'), ('Hôtel'), ('Jardin'), ('Musée'), ('Restaurant')");
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Poi.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Category.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP VIEW IF EXISTS " + DatabaseContract.PoiDetail.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
