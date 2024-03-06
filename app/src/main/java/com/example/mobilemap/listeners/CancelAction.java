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
    private final boolean launchedForResult;

    /**
     * Ecouteur pour annuler une action d'un fragment
     *
     * @param activity         activité mère
     * @param launchedForResult si l'activité mère a été lancé par une autre activité pour une tâche
     */
    public CancelAction(AppCompatActivity activity, boolean launchedForResult) {
        this.activity = activity;
        this.launchedForResult = launchedForResult;
    }

    @Override
    public void onClick(View v) {
        if (launchedForResult) { // si lancé par une autre activité pour une tâche
            activity.finish();
        }

        // lancé directement en naviguant
        activity.getSupportFragmentManager().popBackStackImmediate();
    }
}
