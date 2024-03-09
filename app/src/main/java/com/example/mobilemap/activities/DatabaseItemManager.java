package com.example.mobilemap.activities;

import com.example.mobilemap.database.DeleteItemContext;

/**
 * Contract pour une activité de gestion de données
 */
public interface DatabaseItemManager {
    /**
     * Retourne les informations de suppression d'un élément de la base de donnée
     *
     * @return informations pour suppression d'un élément de la base de donnée
     */
    DeleteItemContext getDeleteContext();
}
