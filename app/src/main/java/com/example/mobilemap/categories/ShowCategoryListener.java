package com.example.mobilemap.categories;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobilemap.R;
import com.example.mobilemap.categories.fragments.CategoryFragment;
import com.example.mobilemap.database.DatabaseContract;

/**
 * Création de l'écouteur pour afficher une catéogorie
 *
 * @author J.Houdé
 */
public class ShowCategoryListener implements View.OnClickListener {
    private final long itemId;
    private final AppCompatActivity activity;

    /**
     * Création de l'écouteur pour afficher une catéogorie existante
     *
     * @param itemId   identifiant de la catégorie
     * @param activity activité à l'origine du fragment
     */
    public ShowCategoryListener(long itemId, AppCompatActivity activity) {
        this.itemId = itemId;
        this.activity = activity;
    }

    /**
     * Création de l'écouteur pour afficher une nouvelle catégorie
     *
     * @param activity activité à l'origine du fragment
     */
    public ShowCategoryListener(AppCompatActivity activity) {
        this(-1, activity);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = itemId > DatabaseContract.NOT_EXISTING_ID ? CategoryFragment.newInstance(itemId) : new CategoryFragment();

        // affichage du détail de la catégorie sélectionnée ou d'une page vide pour la création de contenu
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.categoriesFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
