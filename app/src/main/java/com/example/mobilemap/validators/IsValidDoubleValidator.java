package com.example.mobilemap.validators;

import android.content.res.Resources;
import android.widget.EditText;

import com.example.mobilemap.R;

import java.util.regex.Pattern;

/**
 * Vérification du format si un champ de texte représente un double valide
 */
public class IsValidDoubleValidator extends FieldValidator {
    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    /**
     * Vérification du format si un champ de texte représente un double valide
     *
     * @param field     champ à vérifier
     * @param resources gestionnaire de ressourses
     */
    public IsValidDoubleValidator(EditText field, Resources resources) {
        super(field, resources.getString(R.string.invalid_number));
    }

    @Override
    public boolean isValid() {
        String value = field.getText().toString().trim();

        return pattern.matcher(value).matches();
    }
}
