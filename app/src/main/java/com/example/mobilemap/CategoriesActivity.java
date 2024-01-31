package com.example.mobilemap;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.databinding.ActivityCategoriesBinding;
import com.example.mobilemap.fragment.CategoryListFragment;
import com.example.mobilemap.listener.NavigationBarItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

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

    public List<Category> getCategories() {
        Cursor cursor = this.getContentResolver()
                .query(DatabaseContract.Category.CONTENT_URI, DatabaseContract.Category.COLUMNS,
                        null, null, DatabaseContract.Category.COLUMN_NAME + " COLLATE LOCALIZED ASC");
        assert cursor != null;

        return Category.mapFromList(cursor);
    }

    public DeleteItemContext getDeleteContext() {
        return new DeleteItemContext(DatabaseContract.Category.CONTENT_URI,
                R.string.dialog_delete_category_title,
                R.string.confirm_delete_category_msg);
    }
}