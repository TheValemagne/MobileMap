package com.example.mobilemap.map.listeners;

import android.app.Activity;
import android.database.MatrixCursor;
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

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class RequestSuggestionsListener implements Geocoder.GeocodeListener {
    private final Activity activity;
    private final SearchView searchView;
    private final Handler handler;

    public RequestSuggestionsListener(Activity activity, SearchView searchView) {
        this.activity = activity;
        this.searchView = searchView;
        handler = new Handler(Looper.getMainLooper());
    }


    @Override
    public void onGeocode(@NonNull List<Address> addresses) {
        MatrixCursor cursor = SuggestionAdapter.getCursorAdapter(addresses);

        Runnable runnable = () -> {
            // This thread runs in the UI
            handler.postDelayed(() -> {
                searchView.setSuggestionsAdapter(new SuggestionAdapter(activity, cursor, searchView, addresses));
                searchView.getSuggestionsAdapter().notifyDataSetChanged();
            }, 10);
        };
        new Thread(runnable).start();
    }

}
