package com.example.mobilemap.validators;

import android.content.res.Resources;
import android.widget.EditText;

import com.example.mobilemap.R;

/**
 * Vérification de champ non vide
 *
 * @author J.Houdé
 */
public class IsFieldEmpty extends FieldValidator {
    public IsFieldEmpty(EditText field, Resources resources) {
        super(field, resources.getString(R.string.error_field_is_empty));
    }

    @Override
    public boolean isValid() {
        return !this.field.getText().toString().isEmpty();
    }
}
