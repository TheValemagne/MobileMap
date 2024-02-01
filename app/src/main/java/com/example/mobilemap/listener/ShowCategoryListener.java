package com.example.mobilemap.listener;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobilemap.R;
import com.example.mobilemap.fragment.CategoryFragment;

public class ShowCategoryListener implements View.OnClickListener {
    private final long itemId;
    private final AppCompatActivity activity;

    public ShowCategoryListener(long itemId, AppCompatActivity activity) {
        this.itemId = itemId;
        this.activity = activity;
    }

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
