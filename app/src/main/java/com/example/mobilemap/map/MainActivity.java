package com.example.mobilemap.map;

import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.mobilemap.R;
import com.example.mobilemap.databinding.ActivityMainBinding;
import com.example.mobilemap.listeners.NavigationBarItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private MapManager mapManager;
    private BottomNavigationView bottomNavigationMenuView;
    private LinearLayout floatingButtonsLayout;
    private LinearLayout filterFloatingLayout;

    private FloatingActionButton showCircleAroundMe;

    private FloatingActionButton removeCircleAroundMe;
    private static final int currentPageId = R.id.navigation_map;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();
        EdgeToEdge.enable(this); // permet d'étendre l'affichage de l'application à tout l'écran

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initBottomNavigationView(binding);

        mapManager = new MapManager(binding.mapView, this, context);
        mapManager.initMap();

        floatingButtonsLayout = binding.floatingButtonsLayout;
        floatingButtonsLayout.setVisibility(View.GONE);

        filterFloatingLayout = binding.filterFloatingLayout;
        filterFloatingLayout.setVisibility(View.GONE);

        showCircleAroundMe = binding.showCircleAroundMe;
        showCircleAroundMe.setOnClickListener(v -> mapManager.showAddCircleAroundMeDialog());

        removeCircleAroundMe = binding.removeCircleAroundMe;
        removeCircleAroundMe.setOnClickListener(v -> mapManager.removeCircle());

        binding.locateMeBtn.setOnClickListener(v -> mapManager.centerToUserLocation());
        binding.addMarkerAtLocation.setOnClickListener(v -> mapManager.addMarkerToCurrentLocation());

        requestPermissionsIfNecessary(Collections.singletonList(
                Manifest.permission.ACCESS_FINE_LOCATION
        ));
    }

    private void initBottomNavigationView(ActivityMainBinding binding) {
        ConstraintLayout view = binding.getRoot();
        // ajustement de la bar de navigation pour éviter tout dépassement ou superposition avec la bar aec les 3 boutons
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures());
            view.setPadding(0, 0, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        bottomNavigationMenuView = binding.mainNavigationBar;
        bottomNavigationMenuView.setSelectedItemId(currentPageId);
        bottomNavigationMenuView.setOnItemSelectedListener(new NavigationBarItemSelectedListener(this, currentPageId));
    }

    public void shouldShowLocationBtn(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;

        floatingButtonsLayout.setVisibility(visibility);
        filterFloatingLayout.setVisibility(visibility);
    }

    public void updateFilterAction(boolean hasCircleAroundMe) {
        filterFloatingLayout.setVisibility(View.VISIBLE);

        int showCircleAroundMeVisibility = hasCircleAroundMe ? View.GONE : View.VISIBLE;
        int removeCircleAroundMeVisibility = hasCircleAroundMe ? View.VISIBLE : View.GONE;

        showCircleAroundMe.setVisibility(showCircleAroundMeVisibility);
        removeCircleAroundMe.setVisibility(removeCircleAroundMeVisibility);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mapManager != null) { // actualise les marques et le circle après navigation dans les autres pages
            mapManager.updateMarkers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapManager.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bottomNavigationMenuView.setSelectedItemId(currentPageId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapManager.onDetach();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>(Arrays.asList(permissions).subList(0, grantResults.length));

        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(List<String> permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public ActivityResultLauncher<Intent> getPoiActivityLauncher() {
        return poiActivityLauncher;
    }

    private final ActivityResultLauncher<Intent> poiActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    mapManager.updateMarkers();
                }
            });
}