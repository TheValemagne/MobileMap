package com.example.mobilemap.map.listeners;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.mobilemap.map.SuggestionAdapter;

import java.util.List;

/**
 * Ecouteur de réception des suggestions de recherches d'adresses
 *
 * @author J.Houdé
 */
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class RequestSearchSuggestionsListener implements Geocoder.GeocodeListener {
    private final Activity activity;
    private final SearchView searchView;
    private final Handler handler;

    /**
     * Ecouteur de réception des suggestions d'adresses
     *
     * @param activity   activité principale
     * @param searchView barre de recherche
     */
    public RequestSearchSuggestionsListener(Activity activity, SearchView searchView) {
        this.activity = activity;
        this.searchView = searchView;

        handler = new Handler(Looper.getMainLooper());
    }


    @Override
    public void onGeocode(@NonNull List<Address> addresses) {
        Runnable runnable = () -> {
            // L'action doit être executé dans le thread principal ayant le contrôle de l'interface
            handler.postDelayed(() -> {
                searchView.setSuggestionsAdapter(new SuggestionAdapter(activity, searchView, addresses));
                searchView.getSuggestionsAdapter().notifyDataSetChanged();
            }, 10);
        };
        new Thread(runnable).start();
    }

}
