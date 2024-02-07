package com.example.mobilemap.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.mobilemap.NavigationHandler;
import com.example.mobilemap.categories.CategoriesActivity;
import com.example.mobilemap.map.MainActivity;
import com.example.mobilemap.pois.PoisActivity;
import com.example.mobilemap.R;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavigationBarItemSelectedListener implements NavigationBarView.OnItemSelectedListener {

    private final List<NavigationHandler> handlers;

    /**
     * @param activity activité mère
     * @param currentActivityId identifiant de la page actuelle
     */
    public NavigationBarItemSelectedListener(Activity activity, int currentActivityId) {
        this.handlers = new ArrayList<>(Arrays.asList(
                new NavigationHandler(R.id.navigation_map, new Intent(activity, MainActivity.class), activity, currentActivityId),
                new NavigationHandler(R.id.navigation_categories, new Intent(activity, CategoriesActivity.class), activity, currentActivityId),
                new NavigationHandler(R.id.navigation_pois, new Intent(activity, PoisActivity.class), activity, currentActivityId)
        ));

        for (int index = 0; index < handlers.size() - 1; index++) {
            handlers.get(index).setNextNavigationHandler(handlers.get(index + 1));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return handlers.get(0).handle(item.getItemId());
    }
}
