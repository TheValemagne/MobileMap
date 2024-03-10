package com.example.mobilemap.map.listeners;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.mobilemap.map.manager.MapManager;

import java.util.List;

/**
 * Ecouteur de recherche d'une position sur la carte
 *
 * @author J.Houdé
 */
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class RequestSearchResultListener implements Geocoder.GeocodeListener {
    private final MapManager mapManager;
    private final Handler handler;

    /**
     * Ecouteur de recherche d'une position sur la carte
     *
     * @param mapManager gestionnaire de la carte
     */
    public RequestSearchResultListener(MapManager mapManager) {
        this.mapManager = mapManager;

        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onGeocode(@NonNull List<Address> addresses) {
        Runnable runnable = () -> {
            // L'action d'affichage du cercle est executé dans le thread principal ayant le contrôle de l'interface
            handler.postDelayed(() -> mapManager.showAddCircleAroundSearch(addresses.get(0)), 10);
        };

        new Thread(runnable).start();
    }
}
