package com.example.mobilemap.validators;

import android.widget.EditText;

public abstract class FieldValidator {
    protected final EditText field;
    protected final String errorMsg;

    public FieldValidator(EditText field, String errorMsg) {
        this.field = field;
        this.errorMsg = errorMsg;
    }

    protected abstract boolean isValid();

    public boolean check() {
        if (this.isValid()) {
            return true;
        }

        this.field.setError(this.errorMsg);
        return false;
    }
}
