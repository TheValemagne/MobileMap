package com.example.mobilemap.database;

import android.net.Uri;

/**
 * Contexte pour supprimer une entrée de la base de données
 *
 * @author J.Houdé
 */
public class DeleteItemContext {
    public Uri getDatabaseUri() {
        return databaseUri;
    }

    /**
     * Retourne le titre du dialogue de suppression
     *
     * @return titre du dialogue de suppression
     */
    public String getDialogTitle() {
        return dialogTitle;
    }

    /**
     * Retourne le message du dialogue de suppression
     *
     * @return message du dialogue de suppression
     */
    public String getDialogMsg() {
        return dialogMsg;
    }

    private final Uri databaseUri;
    private final String dialogTitle;
    private final String dialogMsg;

    /**
     * @param databaseUri uri de la table
     * @param dialogTitle titre du dialogue de suppression
     * @param dialogMsg   message du dialogue de suppression
     */
    public DeleteItemContext(Uri databaseUri, String dialogTitle, String dialogMsg) {
        this.databaseUri = databaseUri;
        this.dialogTitle = dialogTitle;
        this.dialogMsg = dialogMsg;
    }
}
