package com.example.mobilemap.listener;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemap.R;
import com.example.mobilemap.fragment.CategoryFragment;

public class EditCategoryListener implements View.OnClickListener {
    private final long itemId;
    private final AppCompatActivity activity;

    public EditCategoryListener(long itemId, AppCompatActivity activity) {
        this.itemId = itemId;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.categoriesFragmentContainer, CategoryFragment.newInstance(itemId))
                .addToBackStack(null)
                .commit();
    }
}
