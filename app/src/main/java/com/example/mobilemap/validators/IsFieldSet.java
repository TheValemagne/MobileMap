package com.example.mobilemap.validators;

import android.content.res.Resources;
import android.widget.EditText;

import com.example.mobilemap.R;

/**
 * Vérification de champ obligatoire
 *
 * @author J.Houdé
 */
public class IsFieldSet extends FieldValidator {
    /**
     * Vérification de champ obligatoire
     *
     * @param field champ à vérifier
     * @param resources gestionnaire de ressourses
     */
    public IsFieldSet(EditText field, Resources resources) {
        super(field, resources.getString(R.string.error_field_is_empty));
    }

    @Override
    public boolean isValid() {
        return !this.field.getText().toString().trim().isEmpty();
    }
}
