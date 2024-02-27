package com.example.mobilemap.map.listeners;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.mobilemap.map.manager.MapManager;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

/**
 * Ecouteur de recherche d'une position sur la carte
 *
 * @author J.Houdé
 */
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class RequestSearResultListener implements Geocoder.GeocodeListener {
    private final MapManager mapManager;
    private final Handler handler;

    /**
     * Ecouteur de recherche d'une position sur la carte
     *
     * @param mapManager gestionnaire de la carte
     */
    public RequestSearResultListener(MapManager mapManager) {
        this.mapManager = mapManager;

        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onGeocode(@NonNull List<Address> addresses) {
        Runnable runnable = () -> {
            // L'action d'affichage du cercle est executé dans le thread principal ayant le contrôle de l'interface
            handler.postDelayed(() -> {
                Address address = addresses.get(0);
                mapManager.showAddCircleAroundPoiDialog(new OverlayItem(
                        "0",
                        address.getAddressLine(0),
                        address.getLocality(),
                        new GeoPoint(address.getLatitude(), address.getLongitude())));
            }, 10);
        };

        new Thread(runnable).start();
    }
}
