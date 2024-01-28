package com.example.mobilemap.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.mobilemap.database.dao.CategoryDAO;
import com.example.mobilemap.database.dao.PoiDAO;
import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.database.table.Poi;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final CategoryDAO categoryDAO;
    private final PoiDAO siteDAO;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);

        this.categoryDAO = new CategoryDAO(this);
        this.siteDAO = new PoiDAO(this);
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

    public void insertPoi(Poi poi) {
        siteDAO.insert(poi);
    }

    public void insertCategory(Category category) {
        categoryDAO.insert(category);
    }

    public int updatePoi(Poi site) {
        return siteDAO.update(site);
    }

    public int updateCategory(Category category) {
        return categoryDAO.update(category);
    }

    public void deletePoi(long id) {
        siteDAO.delete(id);
    }

    public void deleteCategory(long id) {
        categoryDAO.delete(id);
    }

    public List<Poi> getPois() {
        return siteDAO.findAll();
    }

    public List<Category> getCategories() {
        return categoryDAO.findAll();
    }

    public Cursor getPoiDetails() {
        String request = "SELECT A.name, P.name, year " +
                "FROM " + DatabaseContract.Site.TABLE_NAME + " AS A " +
                "JOIN " + DatabaseContract.Category.TABLE_NAME + " AS P " +
                "ON A." + DatabaseContract.Site.COLUMN_CATEGORY_ID + " = P." + DatabaseContract.Category.ID  +
                ";";

        return getReadableDatabase().rawQuery(request, null);
    }
}
