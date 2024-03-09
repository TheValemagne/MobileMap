package com.example.mobilemap.validators;

import android.widget.EditText;

/**
 * Classe abstraite pour la vérification d'une condition d'un champ de données
 *
 * @author J.Houdé
 */
public abstract class FieldValidator {
    protected final EditText field;
    protected final String errorMsg;

    /**
     * Classe abstraite pour la vérification d'une condition d'un champ de données
     *
     * @param field    champ à vérifier
     * @param errorMsg message d'erreur à afficher en cas d'invalidité
     */
    public FieldValidator(EditText field, String errorMsg) {
        this.field = field;
        this.errorMsg = errorMsg;
    }

    /**
     * Vérification si le contenu est valide
     *
     * @return vrai si le contenu est valide, sinon faux
     */
    public abstract boolean isValid();

    /**
     * Vérification du champ et affichage en cas de non validité d'un message d'erreur
     *
     * @return vrai si le champ est valide, sinon faux
     */
    public boolean check() {
        if (this.isValid()) {
            return true;
        }

        this.field.setError(this.errorMsg); // affichage du message d'erreur
        return false;
    }
}
