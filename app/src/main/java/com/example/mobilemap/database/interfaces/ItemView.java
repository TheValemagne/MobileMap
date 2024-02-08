package com.example.mobilemap.database.interfaces;

import com.example.mobilemap.database.tables.DatabaseItem;

/**
 * Interface pour les controlleurs gérant la création et modification de données
 * @param <T>
 */
public interface ItemView<T extends DatabaseItem> {
    boolean check();

    T getValues();
}
