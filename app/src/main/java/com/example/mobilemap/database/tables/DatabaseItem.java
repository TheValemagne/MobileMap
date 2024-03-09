package com.example.mobilemap.database.tables;

import android.content.ContentValues;

import com.example.mobilemap.database.interfaces.HasId;

/**
 * Classe conteneur représentant une table SQL sous Android
 *
 * @author J.Houdé
 */
public abstract class DatabaseItem implements HasId {
    protected long id;

    /**
     * Classe conteneur représentant une table SQL sous Android
     *
     * @param id identifiant unique de la donnée
     */
    public DatabaseItem(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    /**
     * Convertie les données dans le format ContentValues
     *
     * @return ContentValues avec toutes les données de la classe
     */
    public abstract ContentValues toContentValues();
}
