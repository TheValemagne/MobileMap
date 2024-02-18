package com.example.mobilemap.pois.listeners;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobilemap.R;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.pois.fragments.PoiFragment;

/**
 * Création de l'écouteur pour afficher un site
 *
 * @author J.Houdé
 */
public class ShowPoiListener implements View.OnClickListener {
    private final long itemId;
    private final AppCompatActivity activity;

    /**
     * Création de l'écouteur pour afficher un site existant
     * @param itemId   identifiant de la catégorie
     * @param activity activité à l'origine du fragment
     */
    public ShowPoiListener(long itemId, AppCompatActivity activity) {
        this.itemId = itemId;
        this.activity = activity;
    }

    /**
     * Création de l'écouteur pour afficher un nouveau site
     *
     * @param activity activité à l'origine du fragment
     */
    public ShowPoiListener(androidx.appcompat.app.AppCompatActivity activity) {
        this(-1, activity);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = itemId > DatabaseContract.NOT_EXISTING_ID ? PoiFragment.newInstance(itemId, false) : new PoiFragment();

        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.poisFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}