package com.example.mobilemap.listener;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CancelAction implements View.OnClickListener {
    private final AppCompatActivity activity;
    private final boolean lauchedForResult;

    public CancelAction(AppCompatActivity activity, boolean lauchedForResult) {
        this.activity = activity;
        this.lauchedForResult = lauchedForResult;
    }

    @Override
    public void onClick(View v) {
        if (lauchedForResult) { // lancé par une autre activité pour une tâche
            activity.finish();
        }

        // lancé directement en naviguant
        activity.getSupportFragmentManager().popBackStackImmediate();
    }
}
