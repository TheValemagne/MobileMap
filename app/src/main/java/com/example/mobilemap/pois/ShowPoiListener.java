package com.example.mobilemap.pois;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobilemap.R;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.pois.fragments.PoiFragment;

public class ShowPoiListener implements View.OnClickListener {
    private final long itemId;
    private final AppCompatActivity activity;

    public ShowPoiListener(long itemId, AppCompatActivity activity) {
        this.itemId = itemId;
        this.activity = activity;
    }

    public ShowPoiListener(androidx.appcompat.app.AppCompatActivity activity) {
        this(-1, activity);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = itemId > DatabaseContract.NOT_EXISTING_ID ? PoiFragment.newInstance(itemId) : new PoiFragment();

        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.poisFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}