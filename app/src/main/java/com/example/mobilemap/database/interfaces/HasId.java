package com.example.mobilemap.database.interfaces;

/**
 * Interface pour les objects de la base de données possédant un identifiant
 *
 * @author J.Houdé
 */
public interface HasId {
    /**
     * Retourne l'identifiant de la donnée
     * @return identifiant unique
     */
    long getId();
}
