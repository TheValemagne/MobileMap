package com.example.mobilemap.database.tables;

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
 *
 * @author J.Houdé
 */
public class Category extends DatabaseItem {
    private String name;

    /**
     * Constructeur d'une catégorie existante
     *
     * @param id   identifiant unique de la catégorie
     * @param name nom de la catégorie
     */
    public Category(long id, String name) {
        super(id);
        this.name = name;
    }

    /**
     * Constructeur d'une catégorie à enregister
     *
     * @param name nom de la catégorie
     */
    public Category(String name) {
        this(-1, name);
    }

    @NonNull
    @Override
    public String toString() {
        return MessageFormat.format("Category(_id={0}, name={1})", id, name);
    }

    /**
     * Retourne le nom de la catégorie
     *
     * @return nom de la catégorie
     */
    public String getName() {
        return name;
    }

    /**
     * Modifie le nom de la catégorie
     *
     * @param name nouveau nom de la catégorie
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Convertie un cursor en catégorie
     *
     * @param cursor résultat de la requête à convertir
     * @return la catégorie requêtée
     */
    @SuppressLint("Range")
    public static Category fromCursor(Cursor cursor) {
        return new Category(
                cursor.getLong(cursor.getColumnIndex(DatabaseContract.Category._ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.Category.COLUMN_NAME))
        );
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseContract.Category.COLUMN_NAME, getName());

        return contentValue;
    }

    /**
     * Convertie un cursor en liste de catégories
     *
     * @param cursor résultat de la requête à convertir
     * @return une liste de catégories
     */
    public static List<Category> mapFromList(Cursor cursor) {
        List<Category> result = new ArrayList<>();

        if (!cursor.moveToFirst()) {
            return result;
        }

        do {
            result.add(Category.fromCursor(cursor));
        } while (cursor.moveToNext());

        return result;
    }
}
