package com.example.mobilemap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.databinding.ActivityPoisBinding;
import com.example.mobilemap.fragment.PoiFragment;
import com.example.mobilemap.fragment.PoiListFragment;
import com.example.mobilemap.listener.NavigationBarItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PoisActivity extends AppCompatActivity {
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPoisBinding binding = ActivityPoisBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationMenuView = binding.poisNavigationBar;
        bottomNavigationMenuView.setSelectedItemId(R.id.navigation_pois);
        bottomNavigationMenuView.setOnItemSelectedListener(new NavigationBarItemSelectedListener(this, R.id.navigation_pois));

        Intent intent = getIntent();
        Fragment fragment;
        if(shouldShowPoiFragment(intent)) {
            fragment = PoiFragment.newInstance(intent.getDoubleExtra(PoiFragment.ARG_LATITUDE, 0.0),
                    intent.getDoubleExtra(PoiFragment.ARG_LONGITUDE, 0.0));
        } else {
            fragment = new PoiListFragment();
        }

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.poisFragmentContainer, fragment)
                .commit();
    }

    private boolean shouldShowPoiFragment(Intent intent) {
        return intent.hasExtra(PoiFragment.ARG_LATITUDE)
                && intent.hasExtra(PoiFragment.ARG_LONGITUDE)
                && !ContentResolverHelper.getCategories(getContentResolver()).isEmpty();
    }

    public DeleteItemContext getDeleteContext() {
        return new DeleteItemContext(DatabaseContract.Poi.CONTENT_URI,
                R.string.dialog_delete_poi_title,
                R.string.confirm_delete_poi_msg);
    }

    public static Intent createIntent(Activity activity, double latitude, double longitude) {
        Intent intent = new Intent(activity.getApplicationContext(), PoisActivity.class);
        intent.putExtra(ARG_LATITUDE, latitude);
        intent.putExtra(ARG_LONGITUDE, longitude);

        return intent;
    }
}