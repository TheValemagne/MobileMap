package com.example.mobilemap.validators;

import android.content.res.Resources;
import android.widget.EditText;

import com.example.mobilemap.R;

import java.util.List;

/**
 * Vérification de l'unicité du nom d'une catégorie
 *
 * @author J.Houdé
 */
public class IsUniqueCategoryValidator extends FieldValidator {
    private final List<String> notAvailableValues;

    /**
     * Vérification de l'unicité du nom d'une catégorie
     *
     * @param field              champ à vérifier
     * @param resources          gestionnaire de ressourses
     * @param notAvailableValues liste de catégories déjà définies
     */
    public IsUniqueCategoryValidator(EditText field, Resources resources, List<String> notAvailableValues) {
        super(field, resources.getString(R.string.error_category_not_unique));

        this.notAvailableValues = notAvailableValues;
    }

    @Override
    public boolean isValid() {
        return !notAvailableValues.contains(getValue());
    }
}
