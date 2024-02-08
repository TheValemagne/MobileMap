package com.example.mobilemap.database.tables;

import android.content.ContentValues;

import com.example.mobilemap.database.interfaces.HasId;

/**
 * Classe conteneur représentant une table SQL sous Android
 */
public abstract class DatabaseItem implements HasId {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    protected long id;

    public DatabaseItem(long _ID) {
        this.id = _ID;
    }

    /**
     * Convertie les données dans le format ContentValues
     *
     * @return ContentValues avec toutes les données de la classe
     */
    public abstract ContentValues toContentValues();
}
