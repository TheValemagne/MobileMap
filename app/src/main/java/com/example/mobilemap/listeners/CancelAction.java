package com.example.mobilemap.listeners;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Ecouteur pour annuler une action d'un fragment
 *
 * @author J.Houdé
 */
public class CancelAction implements View.OnClickListener {
    private final AppCompatActivity activity;
    private final boolean lauchedForResult;

    /**
     * Ecouteur pour annuler une action d'un fragment
     *
     * @param activity         activité mère
     * @param lauchedForResult si l'activité mère a été lancé par une autre activité pour une tâche
     */
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
