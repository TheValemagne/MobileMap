package com.example.mobilemap.validators;

import android.content.res.Resources;
import android.widget.EditText;

import com.example.mobilemap.R;

import java.util.List;

/**
 * Vérification de l'unicité du nom d'une catégorie
 */
public class IsUniqueCategoryValidator extends FieldValidator{
    private final List<String> notAvailableValues;

    public IsUniqueCategoryValidator(EditText field, Resources resources, List<String> notAvailableValues) {
        super(field, resources.getString(R.string.error_category_not_unique));

        this.notAvailableValues = notAvailableValues;
    }

    @Override
    public boolean isValid() {
        return !notAvailableValues.contains(this.field.getText().toString());
    }
}
