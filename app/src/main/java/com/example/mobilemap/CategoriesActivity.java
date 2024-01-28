package com.example.mobilemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.example.mobilemap.adapter.CategoryListRecyclerViewAdapter;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.databinding.ActivityCategoriesBinding;
import com.example.mobilemap.listener.NavigationBarItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private CategoryListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCategoriesBinding binding = ActivityCategoriesBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationMenuView = binding.categoriesNavigationBar;
        bottomNavigationMenuView.setSelectedItemId(R.id.navigation_categories);
        bottomNavigationMenuView.setOnItemSelectedListener(new NavigationBarItemSelectedListener(this, R.id.navigation_categories));

        List<Category> categories = getCategories();

        if (!categories.isEmpty()) {
            binding.emptyLabel.setVisibility(View.INVISIBLE);
        }

        System.out.println(categories);
        RecyclerView recyclerView = binding.categoriesList;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryListRecyclerViewAdapter(categories);
        recyclerView.setAdapter(adapter);
    }

    private void updateList() {
        adapter.updateList(getCategories());
    }

    @NonNull
    private List<Category> getCategories() {
        Cursor cursor = getContentResolver()
                .query(DatabaseContract.Category.CONTENT_URI, DatabaseContract.Category.COLUMNS, null, null, DatabaseContract.Category.COLUMN_NAME);
        assert cursor != null;

        return Category.mapFromList(cursor);
    }
}