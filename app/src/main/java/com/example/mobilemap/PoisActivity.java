package com.example.mobilemap;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.PoiDetail;
import com.example.mobilemap.databinding.ActivityPoisBinding;
import com.example.mobilemap.fragment.PoiListFragment;
import com.example.mobilemap.listener.NavigationBarItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class PoisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPoisBinding binding = ActivityPoisBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationMenuView = binding.poisNavigationBar;
        bottomNavigationMenuView.setSelectedItemId(R.id.navigation_pois);
        bottomNavigationMenuView.setOnItemSelectedListener(new NavigationBarItemSelectedListener(this, R.id.navigation_pois));

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.poisFragmentContainer, new PoiListFragment())
                .commit();
    }

    public List<PoiDetail> getPois() {
        Cursor cursor = this.getContentResolver()
                .query(DatabaseContract.PoiDetail.CONTENT_URI, DatabaseContract.PoiDetail.COLUMNS,
                null, null, DatabaseContract.PoiDetail.COLUMN_SITE_NAME + " COLLATE LOCALIZED ASC");
        assert cursor != null;
        System.out.println(Arrays.toString(cursor.getColumnNames()));

        return PoiDetail.mapFromList(cursor);
    }

    public DeleteItemContext getDeleteContext() {
        return new DeleteItemContext(DatabaseContract.Site.CONTENT_URI,
                R.string.dialog_delete_poi_title,
                R.string.confirm_delete_poi_msg);
    }
}