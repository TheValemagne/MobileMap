package com.example.mobilemap.categories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;

import com.example.mobilemap.R;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.DeleteItemContext;
import com.example.mobilemap.databinding.ActivityCategoriesBinding;
import com.example.mobilemap.categories.fragments.CategoryListFragment;
import com.example.mobilemap.listeners.NavigationBarItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CategoriesActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationMenuView;
    private static final int currentPageId = R.id.navigation_categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCategoriesBinding binding = ActivityCategoriesBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        bottomNavigationMenuView = binding.categoriesNavigationBar;
        bottomNavigationMenuView.setSelectedItemId(currentPageId);
        bottomNavigationMenuView.setOnItemSelectedListener(new NavigationBarItemSelectedListener(this, currentPageId));

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
    public DeleteItemContext getDeleteContext() {
        Resources resources = this.getResources();

        return new DeleteItemContext(DatabaseContract.Category.CONTENT_URI,
                resources.getString(R.string.dialog_delete_category_title),
                resources.getString(R.string.confirm_delete_category_msg));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bottomNavigationMenuView.setSelectedItemId(currentPageId);
    }
}