package com.example.mobilemap.database;

import android.content.Context;
import android.database.Cursor;

import com.example.mobilemap.database.dao.CategoryDAO;
import com.example.mobilemap.database.dao.SiteDAO;
import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.database.table.Site;

import java.util.List;

public class DatabaseManager {
    private final DatabaseHelper databaseHelper;
    private final CategoryDAO categoryDAO;
    private final SiteDAO siteDAO;

    public DatabaseManager(Context context) {

        this.databaseHelper = new DatabaseHelper(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        this.categoryDAO = new CategoryDAO(databaseHelper);
        this.siteDAO = new SiteDAO(databaseHelper);
    }

    public void close() {
        databaseHelper.close();
    }

    public void insertSite(Site site) {
        siteDAO.insert(site);
    }

    public void insertCategory(Category category) {
        categoryDAO.insert(category);
    }

    public int updateSite(Site site) {
        return siteDAO.update(site);
    }

    public int updateCategory(Category category) {
        return categoryDAO.update(category);
    }

    public void deleteSite(long id) {
        siteDAO.delete(id);
    }

    public void deleteCategory(long id) {
        categoryDAO.delete(id);
    }

    public List<Site> getSites() {
        return siteDAO.getAll();
    }

    public List<Category> getCategories() {
        return categoryDAO.getAll();
    }

    public Cursor getSitesDetails() {
        String request = "SELECT A.name, P.name, year " +
                "FROM " + DatabaseContract.Site.TABLE_NAME + " AS A " +
                "JOIN " + DatabaseContract.Category.TABLE_NAME + " AS P " +
                "ON A." + DatabaseContract.Site.COLUMN_CATEGORY_ID + " = P." + DatabaseContract.Category.ID  +
                ";";

        return databaseHelper.getReadableDatabase().rawQuery(request, null);
    }
}

