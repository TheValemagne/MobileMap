package com.example.mobilemap.database.interfaces;

import com.example.mobilemap.database.tables.DatabaseItem;

/**
 * Interface pour les controlleurs gérant la création et modification de données
 *
 * @param <T> une classe héritant de DatabaseItem
 * @author J.Houdé
 */
public interface ItemView<T extends DatabaseItem> {
    /**
     * Vérification des champs de données
     *
     * @return vrai si les données sont valides
     */
    boolean isValid();

    /**
     * Récupération des données entrées
     *
     * @return object contenant les données du formulaire
     */
    T getValues();
}
