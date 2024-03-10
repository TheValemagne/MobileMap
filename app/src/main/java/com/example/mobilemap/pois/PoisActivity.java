package com.example.mobilemap.pois;

import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.example.mobilemap.activities.BaseActivity;
import com.example.mobilemap.activities.DatabaseItemManager;
import com.example.mobilemap.R;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.DeleteItemContext;
import com.example.mobilemap.databinding.ActivityPoisBinding;
import com.example.mobilemap.pois.fragments.PoiFragment;
import com.example.mobilemap.pois.fragments.PoiListFragment;

/**
 * Activité pour la gestion des sites
 *
 * @author J.Houdé
 */
public class PoisActivity extends BaseActivity implements DatabaseItemManager {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPoisBinding binding = ActivityPoisBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initNavigationBar(binding.poisNavigationBar, R.id.navigation_pois);

        if (this.getSupportFragmentManager().findFragmentById(R.id.poisFragmentContainer) != null) {
            return;
        }

        Intent intent = getIntent();

        // Affichage du fragment avec le détail d'un site ou la liste des sites en fonction des paramètres stockés dans l'intent
        Fragment fragment = shouldShowPoiFragment(intent) ?
                getPoiFragmentInstance(intent) : new PoiListFragment();

        // affichage par défaux de la page avec la liste de sites
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.poisFragmentContainer, fragment)
                .commit();
    }

    /**
     * Créé une instance du Fragment de détail d'un site
     *
     * @param intent données à transmettre au fragment
     * @return le fragment instancié avec les données
     */
    private Fragment getPoiFragmentInstance(Intent intent) {
        long itemId = intent.getLongExtra(PoiFragment.ARG_ITEM_ID, DatabaseContract.NOT_EXISTING_ID);

        if (itemId == DatabaseContract.NOT_EXISTING_ID) {
            return PoiFragment.newInstance(intent.getDoubleExtra(PoiFragment.ARG_LATITUDE, 0.0),
                    intent.getDoubleExtra(PoiFragment.ARG_LONGITUDE, 0.0), true);
        }

        return PoiFragment.newInstance(itemId, true);
    }

    /**
     * Vérifie si le détail d'un site doit être affiché
     *
     * @param intent données à transmettre au fragment
     * @return retourne vrai si le détail d'un site doit être affiché, sinon faux
     */
    private boolean shouldShowPoiFragment(Intent intent) {
        return (intent.hasExtra(PoiFragment.ARG_LATITUDE)
                && intent.hasExtra(PoiFragment.ARG_LONGITUDE)
                && !ContentResolverHelper.getCategories(getContentResolver()).isEmpty())
                || intent.hasExtra(PoiFragment.ARG_ITEM_ID);
    }

    /**
     * Retourne les informations de suppression d'un site
     *
     * @return informations pour suppression d'un site
     */
    @Override
    public DeleteItemContext getDeleteContext() {
        Resources resources = this.getResources();
        return new DeleteItemContext(DatabaseContract.Poi.CONTENT_URI,
                resources.getString(R.string.dialog_delete_poi_title),
                resources.getString(R.string.confirm_delete_poi_msg));
    }

    /**
     * Création d'un intent pour l'affichage du détail d'un nouveau site avec la localisation préremplie
     *
     * @param activity  activité à l'origine de la demande
     * @param latitude  latitude du futur site
     * @param longitude longitude du futur site
     * @return intent avec les informations données
     */
    public static Intent createIntent(Activity activity, double latitude, double longitude) {
        return new Intent(activity.getApplicationContext(), PoisActivity.class)
                .putExtra(PoiFragment.ARG_LATITUDE, latitude)
                .putExtra(PoiFragment.ARG_LONGITUDE, longitude);
    }

    /**
     * Création d'un intent pour l'affichage du détail d'un site
     *
     * @param activity activité à l'origine de la demande
     * @param itemId   identifiant du site à afficher
     * @return intent avec les informations données
     */
    public static Intent createIntent(Activity activity, long itemId) {
        return new Intent(activity.getApplicationContext(), PoisActivity.class)
                .putExtra(PoiFragment.ARG_ITEM_ID, itemId);
    }

}