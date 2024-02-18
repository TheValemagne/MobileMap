package com.example.mobilemap.validators;

import android.content.res.Resources;
import android.icu.text.MessageFormat;
import android.widget.EditText;

import com.example.mobilemap.R;

/**
 * Vérification d'un intervalle de valeur pour double
 *
 * @author J.Houdé
 */
public class DoubleRangeValidator extends FieldValidator {
    private final double minBound;
    private final double maxBound;

    public DoubleRangeValidator(EditText field, Resources resources, double minBound, double maxBound) {
        super(field, MessageFormat.format(resources.getString(R.string.coordinate_out_of_bound), minBound, maxBound));

        this.minBound = minBound;
        this.maxBound = maxBound;
    }

    @Override
    public boolean isValid() {
        double value = Double.parseDouble(field.getText().toString());

        return value >= minBound && value <= maxBound;
    }
}
