package com.example.mobilemap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mobilemap.databinding.ActivityCategoriesBinding;
import com.example.mobilemap.fragment.CategoriesListFragment;
import com.example.mobilemap.listener.NavigationBarItemSelectedListener;
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
                .replace(R.id.categoriesFragmentContainer, new CategoriesListFragment())
                .commit();
    }
}