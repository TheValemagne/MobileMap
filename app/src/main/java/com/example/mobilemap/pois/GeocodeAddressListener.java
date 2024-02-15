package com.example.mobilemap.pois;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class GeocodeAddressListener implements Geocoder.GeocodeListener {
    private final TextView addressField;

    public GeocodeAddressListener(TextView addressField) {
        this.addressField = addressField;
    }

    @Override
    public void onGeocode(@NonNull List<Address> addresses) {
        if (addresses.isEmpty()) {
            addressField.setText("");
        }

        addressField.setText(addresses.get(0).getAddressLine(0));
    }
}
