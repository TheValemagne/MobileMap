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
     * @return retourne vrai si les données sont valides, sinon faux
     */
    boolean isValid();

    /**
     * Récupération des données entrées
     *
     * @return objet contenant les données renseignés du formulaire
     */
    T getValues();
}
