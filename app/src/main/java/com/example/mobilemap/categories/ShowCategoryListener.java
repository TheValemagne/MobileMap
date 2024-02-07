package com.example.mobilemap.categories;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobilemap.R;
import com.example.mobilemap.categories.fragments.CategoryFragment;

public class ShowCategoryListener implements View.OnClickListener {
    private final long itemId;
    private final AppCompatActivity activity;

    /**
     * Création de l'écouteur pour afficher une catéogorie existante
     *
     * @param itemId identifiant de la catégorie
     * @param activity activité à l'origine du fragment
     */
    public ShowCategoryListener(long itemId, AppCompatActivity activity) {
        this.itemId = itemId;
        this.activity = activity;
    }

    /**
     * Création de l'écouteur pour afficher une nouvelle catéogorie
     *
     * @param activity activité à l'origine du fragment
     */
    public ShowCategoryListener(AppCompatActivity activity) {
        this(-1,activity);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = itemId > -1 ? CategoryFragment.newInstance(itemId) : new CategoryFragment();

        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.categoriesFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
