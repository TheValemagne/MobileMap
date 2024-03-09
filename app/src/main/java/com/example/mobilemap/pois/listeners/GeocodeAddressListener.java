package com.example.mobilemap.pois.listeners;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.List;

/**
 * Ecouteur pour la recherche asynchrone de résultat Geocode
 *
 * @author J.Houdé
 */
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class GeocodeAddressListener implements Geocoder.GeocodeListener {
    private final TextView addressField;

    /**
     * Ecouteur pour la recherche asynchrone de résultat Geocode
     *
     * @param addressField champ texte à actualiser
     */
    public GeocodeAddressListener(TextView addressField) {
        this.addressField = addressField;
    }

    @Override
    public void onGeocode(@NonNull List<Address> addresses) {
        if (addresses.isEmpty()) { // pas de résultat ou pas d'iternet
            addressField.setText("");
        }

        addressField.setText(addresses.get(0).getAddressLine(0));
    }
}
