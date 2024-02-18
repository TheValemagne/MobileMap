package com.example.mobilemap.pois;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.example.mobilemap.R;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.DeleteItemContext;
import com.example.mobilemap.databinding.ActivityPoisBinding;
import com.example.mobilemap.pois.fragments.PoiFragment;
import com.example.mobilemap.pois.fragments.PoiListFragment;
import com.example.mobilemap.listeners.NavigationBarItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Activité pour la gestion des sites
 *
 * @author J.Houdé
 */
public class PoisActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationMenuView;
    private static final int currentPageId = R.id.navigation_pois;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPoisBinding binding = ActivityPoisBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        bottomNavigationMenuView = binding.poisNavigationBar;
        bottomNavigationMenuView.setSelectedItemId(currentPageId);
        bottomNavigationMenuView.setOnItemSelectedListener(new NavigationBarItemSelectedListener(this, currentPageId));


        if (this.getSupportFragmentManager().findFragmentById(R.id.poisFragmentContainer) != null) {
            return;
        }

        Intent intent = getIntent();
        Fragment fragment = shouldShowPoiFragment(intent) ?
                getPoiFragmentInstance(intent) : new PoiListFragment();

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.poisFragmentContainer, fragment)
                .commit();
    }

    private Fragment getPoiFragmentInstance(Intent intent) {
        long itemId = intent.getLongExtra(PoiFragment.ARG_ITEM_ID, DatabaseContract.NOT_EXISTING_ID);

        if (itemId == DatabaseContract.NOT_EXISTING_ID) {
            return PoiFragment.newInstance(intent.getDoubleExtra(PoiFragment.ARG_LATITUDE, 0.0),
                    intent.getDoubleExtra(PoiFragment.ARG_LONGITUDE, 0.0), true);
        }

        return PoiFragment.newInstance(itemId, true);
    }

    private boolean shouldShowPoiFragment(Intent intent) {
        return (intent.hasExtra(PoiFragment.ARG_LATITUDE)
                && intent.hasExtra(PoiFragment.ARG_LONGITUDE)
                && !ContentResolverHelper.getCategories(getContentResolver()).isEmpty())
                || intent.hasExtra(PoiFragment.ARG_ITEM_ID);
    }

    public DeleteItemContext getDeleteContext() {
        Resources resources = this.getResources();
        return new DeleteItemContext(DatabaseContract.Poi.CONTENT_URI,
                resources.getString(R.string.dialog_delete_poi_title),
                resources.getString(R.string.confirm_delete_poi_msg));
    }

    public static Intent createIntent(Activity activity, double latitude, double longitude) {
        return new Intent(activity.getApplicationContext(), PoisActivity.class)
                .putExtra(PoiFragment.ARG_LATITUDE, latitude)
                .putExtra(PoiFragment.ARG_LONGITUDE, longitude);
    }

    public static Intent createIntent(Activity activity, long itemId) {
        return new Intent(activity.getApplicationContext(), PoisActivity.class)
                .putExtra(PoiFragment.ARG_ITEM_ID, itemId);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bottomNavigationMenuView.setSelectedItemId(currentPageId);
    }
}