package com.example.mobilemap.validators;

import android.widget.EditText;

/**
 * Classe abstraite pour la vérification d'une condition d'un champ de données
 */
public abstract class FieldValidator {
    protected final EditText field;
    protected final String errorMsg;

    public FieldValidator(EditText field, String errorMsg) {
        this.field = field;
        this.errorMsg = errorMsg;
    }

    protected abstract boolean isValid();

    /**
     * Vérification du champ
     *
     * @return vrai si le champ est valide
     */
    public boolean check() {
        if (this.isValid()) {
            return true;
        }

        this.field.setError(this.errorMsg); // affichage du message d'erreur
        return false;
    }
}
