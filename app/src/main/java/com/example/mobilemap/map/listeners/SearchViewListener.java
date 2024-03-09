package com.example.mobilemap.map.listeners;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.widget.SearchView;

import com.example.mobilemap.map.manager.MapManager;
import com.example.mobilemap.map.SuggestionAdapter;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.util.List;

/**
 * Ecouteur pour la gestion de la recherche et des suggestions d'une searchView
 *
 * @author J.Houdé
 */
public class SearchViewListener implements SearchView.OnQueryTextListener {
    private final Activity activity;
    private final MapManager mapManager;
    private final SearchView searchView;
    private final Geocoder geocoder;

    private final static int SUGGESTION_LIMIT = 5;
    private final static int QUERY_SEARCH_LIMIT = 1;

    /**
     * Ecouteur pour la gestion de la recherche et des suggestions d'une searchView
     *
     * @param activity   activité principale
     * @param mapManager gestionnaire de la carte
     * @param searchView bare de recherche à manipuler
     */
    public SearchViewListener(Activity activity, MapManager mapManager, SearchView searchView) {
        this.activity = activity;
        this.mapManager = mapManager;
        this.searchView = searchView;

        geocoder = new Geocoder(activity);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // recherche des coordonnées de l'adresse sélectionnée
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) { // uniquement pour les appareils tournant sous tiramisu
            geocoder.getFromLocationName(query, QUERY_SEARCH_LIMIT, new RequestSearchResultListener(mapManager));
        } else {
            List<Address> addresses;

            try {
                addresses = geocoder.getFromLocationName(query, QUERY_SEARCH_LIMIT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (addresses == null || addresses.isEmpty()) {
                return false;
            }

            Address address = addresses.get(0);
            mapManager.showAddCircleAroundPoiDialog(new OverlayItem(
                    "0",
                    address.getAddressLine(0),
                    address.getLocality(),
                    new GeoPoint(address.getLatitude(), address.getLongitude())));
        }

        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.trim().isEmpty()) {
            return false;
        }

        // recherche des suggestions de localisation
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) { // uniquement pour les appareils tournant sous tiramisu
            geocoder.getFromLocationName(newText, SUGGESTION_LIMIT, new RequestSearchSuggestionsListener(activity, searchView));
        } else {
            try {
                List<Address> addresses = new Geocoder(activity).getFromLocationName(newText, SUGGESTION_LIMIT);

                if (addresses == null || addresses.isEmpty()) {
                    return true;
                }

                searchView.setSuggestionsAdapter(new SuggestionAdapter(activity, searchView, addresses));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

}
