package com.example.mobilemap.map.listeners;

import android.app.AlertDialog;
import android.view.View;

import com.example.mobilemap.map.AddCircleAroundPoiDialogBuilder;

public class AddCircleDialogListener implements View.OnClickListener {
    private final AlertDialog dialog;
    private final AddCircleAroundPoiDialogBuilder dialogBuilder;

    public AddCircleDialogListener(AlertDialog dialog, AddCircleAroundPoiDialogBuilder dialogBuilder) {
        this.dialog = dialog;
        this.dialogBuilder = dialogBuilder;
    }

    @Override
    public void onClick(View v) {
        if (!dialogBuilder.check()) {
            return;
        }

        dialogBuilder.showCircle(dialog);
    }
}
