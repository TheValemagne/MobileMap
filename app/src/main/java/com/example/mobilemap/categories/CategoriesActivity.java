package com.example.mobilemap.categories;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mobilemap.R;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.DeleteItemContext;
import com.example.mobilemap.databinding.ActivityCategoriesBinding;
import com.example.mobilemap.categories.fragments.CategoryListFragment;
import com.example.mobilemap.listeners.NavigationBarItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCategoriesBinding binding = ActivityCategoriesBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationMenuView = binding.categoriesNavigationBar;
        bottomNavigationMenuView.setSelectedItemId(R.id.navigation_categories);
        bottomNavigationMenuView.setOnItemSelectedListener(new NavigationBarItemSelectedListener(this, R.id.navigation_categories));

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.categoriesFragmentContainer, new CategoryListFragment())
                .commit();
    }

    public DeleteItemContext getDeleteContext() {
        return new DeleteItemContext(DatabaseContract.Category.CONTENT_URI,
                R.string.dialog_delete_category_title,
                R.string.confirm_delete_category_msg);
    }
}