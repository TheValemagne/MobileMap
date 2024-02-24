package com.example.mobilemap.map.listeners;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.widget.SearchView;

import com.example.mobilemap.map.MapManager;
import com.example.mobilemap.map.SuggestionAdapter;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.util.List;

public class SearchViewListener implements SearchView.OnQueryTextListener {
    private final Activity activity;
    private final MapManager mapManager;
    private final SearchView searchView;
    private final Geocoder geocoder;

    public SearchViewListener(Activity activity, MapManager mapManager, SearchView searchView) {
        this.activity = activity;
        this.mapManager = mapManager;
        this.searchView = searchView;
        geocoder = new Geocoder(activity);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        List<Address> addresses;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(query, 1, new RequestSearResultListener(mapManager));
        } else {
            try {
                addresses = geocoder.getFromLocationName(query, 1);
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(newText, 5, new RequestSuggestionsListener(activity, searchView));
        } else {
            try {
                List<Address> addresses = new Geocoder(activity).getFromLocationName(newText, 5);

                if (addresses == null || addresses.isEmpty()) {
                    return true;
                }

                searchView.setSuggestionsAdapter(new SuggestionAdapter(activity, SuggestionAdapter.getCursorAdapter(addresses), searchView, addresses));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

}
