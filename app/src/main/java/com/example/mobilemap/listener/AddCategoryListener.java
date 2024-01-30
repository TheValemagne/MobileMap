package com.example.mobilemap.listener;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemap.R;
import com.example.mobilemap.fragment.CategoryFragment;

public class AddCategoryListener implements View.OnClickListener {
    private final AppCompatActivity activity;

    public AddCategoryListener(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.categoriesFragmentContainer, new CategoryFragment())
                .addToBackStack(null)
                .commit();
    }
}
