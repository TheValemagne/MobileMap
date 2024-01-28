package com.example.mobilemap.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.mobilemap.CategoriesActivity;
import com.example.mobilemap.MainActivity;
import com.example.mobilemap.R;
import com.google.android.material.navigation.NavigationBarView;

public class NavigationBarItemSelectedListener implements NavigationBarView.OnItemSelectedListener {
    private final Activity activity;
    private final int currentActivityId;

    public NavigationBarItemSelectedListener(Activity activity, int currentActivityId) {
        this.activity = activity;
        this.currentActivityId = currentActivityId;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int menuId = item.getItemId();

        System.out.println(activity + " " + item);
        if (menuId == R.id.navigation_map) {
            if(currentActivityId == R.id.navigation_map) {
                return true;
            }

            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);

            return true;
        } else if (menuId == R.id.navigation_categories) {
            if(currentActivityId == R.id.navigation_categories) {
                return true;
            }

            Intent intent = new Intent(activity, CategoriesActivity.class);
            activity.startActivity(intent);

            return true;
        } else {
            return false;
        }
    }
}
