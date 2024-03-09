package com.example.mobilemap.categories;

import android.content.res.Resources;
import android.os.Bundle;

import com.example.mobilemap.activities.BaseActivity;
import com.example.mobilemap.activities.DatabaseItemManager;
import com.example.mobilemap.R;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.DeleteItemContext;
import com.example.mobilemap.databinding.ActivityCategoriesBinding;
import com.example.mobilemap.categories.fragments.CategoryListFragment;

/**
 * Activité de gestion des catégories
 *
 * @author J.Houdé
 */
public class CategoriesActivity extends BaseActivity implements DatabaseItemManager {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCategoriesBinding binding = ActivityCategoriesBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initNavigationBar(binding.categoriesNavigationBar, R.id.navigation_categories);

        if (this.getSupportFragmentManager().findFragmentById(R.id.categoriesFragmentContainer) != null) {
            return;
        }

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.categoriesFragmentContainer, new CategoryListFragment())
                .commit();
    }

    /**
     * Retourne le contexte pour supprimer une catégorie
     *
     * @return informations pour supprimer la catégorie
     */
    @Override
    public DeleteItemContext getDeleteContext() {
        Resources resources = this.getResources();

        return new DeleteItemContext(DatabaseContract.Category.CONTENT_URI,
                resources.getString(R.string.dialog_delete_category_title),
                resources.getString(R.string.confirm_delete_category_msg));
    }

}