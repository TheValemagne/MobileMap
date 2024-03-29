package com.example.mobilemap.map;

import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.example.mobilemap.activities.BaseActivity;
import com.example.mobilemap.R;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.databinding.ActivityMainBinding;
import com.example.mobilemap.map.listeners.SearchViewListener;
import com.example.mobilemap.map.manager.MapManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Activité principale gérant la carte
 *
 * @author J.Houdé
 */
public class MainActivity extends BaseActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private MapManager mapManager;
    private LinearLayout floatingButtonsLayout;
    private LinearLayout filterFloatingLayout;
    private FloatingActionButton showCircleAroundMe;
    private FloatingActionButton removeCircleAroundMe;
    private SearchView searchView;

    public ActivityResultLauncher<Intent> getPoiActivityLauncher() {
        return poiActivityLauncher;
    }

    private ActivityResultLauncher<Intent> poiActivityLauncher;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this); // permet d'étendre l'affichage de l'application à tout l'écran

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        ConstraintLayout constraintLayout = binding.getRoot();
        setContentView(constraintLayout);

        // ajustement de la bar de navigation pour éviter tout dépassement ou superposition avec la bar de navigation de l'application
        ViewCompat.setOnApplyWindowInsetsListener(constraintLayout, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures());
            constraintLayout.setPadding(0, 0, 0, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        // demande de permission pour la localisation
        requestPermissionsIfNecessary(Collections.singletonList(
                Manifest.permission.ACCESS_FINE_LOCATION
        ));

        bindUI(binding);

        if (ContentResolverHelper.getPois(this.getContentResolver()).isEmpty()) { // s'il y aucun site enregistré
            disableFilterButtons();
        }

        poiActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new MapActivityResultCallback(mapManager));
    }

    /**
     * Initialisation des éléments graphiques
     *
     * @param binding lien avec la vue graphique
     */
    private void bindUI(ActivityMainBinding binding) {
        mapManager = new MapManager(binding.mapView, this);
        mapManager.initMap();

        // barre de recherche
        searchView = binding.searchView;
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchViewListener(this, mapManager, binding.searchView));

        // boutons d'actions en lien avec la localisation de l'utilisateur
        floatingButtonsLayout = binding.floatingButtonsLayout;
        floatingButtonsLayout.setVisibility(View.GONE);

        filterFloatingLayout = binding.filterFloatingLayout;
        filterFloatingLayout.setVisibility(View.GONE);

        initButtons(binding);

        // barre de navigation
        initNavigationBar(binding.mainNavigationBar, R.id.navigation_map);
    }

    /**
     * Initialisation des boutons de l'activité
     *
     * @param binding lien avec la vue graphique
     */
    private void initButtons(ActivityMainBinding binding) {
        showCircleAroundMe = binding.showCircleAroundMe;
        showCircleAroundMe.setOnClickListener(v -> mapManager.showAddCircleAroundMeDialog());

        removeCircleAroundMe = binding.removeCircleAroundMe;
        removeCircleAroundMe.setOnClickListener(v -> mapManager.removeCircle());

        binding.locateMeBtn.setOnClickListener(v -> mapManager.centerToUserLocation());
        binding.addMarkerAtLocation.setOnClickListener(v -> mapManager.addMarkerToCurrentLocation());
    }

    /**
     * Déactivation des éléments de filtrage des marqueurs
     */
    private void disableFilterButtons() {
        searchView.setVisibility(View.GONE);
        searchView.setFocusable(false);

        filterFloatingLayout.setVisibility(View.GONE);
    }

    /**
     * Gestion de l'affichage des boutons en lien avec la localisation actuelle
     *
     * @param isVisible affiche les boutons si vrai, sinon les caches
     */
    public void shouldShowLocationBtn(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        boolean hasStoredPoi = !ContentResolverHelper.getPois(this.getContentResolver()).isEmpty();
        int filterButtonsVisibility = hasStoredPoi ? View.VISIBLE : View.GONE;

        floatingButtonsLayout.setVisibility(visibility);
        filterFloatingLayout.setVisibility(isVisible && hasStoredPoi ? View.VISIBLE : View.GONE);
        searchView.setVisibility(filterButtonsVisibility);
        searchView.setFocusable(hasStoredPoi);
    }

    /**
     * Mise à jour du bouton d'action pour le cercle autour de la position actuelle
     *
     * @param hasCircleAroundMe si vrai affichage de l'option de la croix, sinon de la loupe de recherche
     */
    public void updateFilterAction(boolean hasCircleAroundMe) {
        if (hasCircleAroundMe) {
            filterFloatingLayout.setVisibility(View.VISIBLE);
        } else if (floatingButtonsLayout.getVisibility() == View.GONE) {
            filterFloatingLayout.setVisibility(View.GONE);
        }

        int showCircleAroundMeVisibility = hasCircleAroundMe ? View.GONE : View.VISIBLE;
        int removeCircleAroundMeVisibility = hasCircleAroundMe ? View.VISIBLE : View.GONE;

        showCircleAroundMe.setVisibility(showCircleAroundMeVisibility);
        removeCircleAroundMe.setVisibility(removeCircleAroundMeVisibility);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mapManager != null) { // actualise la carte après navigation dans les autres pages
            mapManager.updateMap();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mapManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mapManager.onResume();

        if (searchView != null) { // enlever le focus de la barre de recherche
            searchView.clearFocus();
        }

        if (searchView != null
                && filterFloatingLayout != null
                && ContentResolverHelper.getPois(this.getContentResolver()).isEmpty()) {
            disableFilterButtons(); // déactiver les boutons de filtres s'il y a aucun site enregistré
        }
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

        if (permissionsToRequest.isEmpty()) {
            return;
        }

        ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toArray(new String[0]),
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Demande les permissions nécessaires à la carte
     *
     * @param permissions liste de permissions à demander
     */
    private void requestPermissionsIfNecessary(List<String> permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }

        if (permissionsToRequest.isEmpty()) {
            return;
        }

        ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toArray(new String[0]),
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }
}