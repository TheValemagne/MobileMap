package com.example.mobilemap.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemap.listeners.NavigationBarItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Classe abstraite pour toutes les activités de l'application
 *
 * @author J.Houdé
 */
public abstract class BaseActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationMenuView;
    private int currentPageId;

    /**
     * Initialisation de la bare de navigation
     *
     * @param bottomNavigationMenuView barre de navigation à initialiser
     * @param currentPageId identifiant du bouton de la page actuelle
     */
    protected void initNavigationBar(BottomNavigationView bottomNavigationMenuView, int currentPageId) {
        this.currentPageId = currentPageId;
        this.bottomNavigationMenuView = bottomNavigationMenuView;

        this.bottomNavigationMenuView.setSelectedItemId(this.currentPageId);
        this.bottomNavigationMenuView.setOnItemSelectedListener(new NavigationBarItemSelectedListener(this, this.currentPageId));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // affichage de l'indicateur pour la page actuellement affichée
        bottomNavigationMenuView.setSelectedItemId(currentPageId);
    }
}
