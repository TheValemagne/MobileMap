package com.example.mobilemap.map;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.mobilemap.databinding.SuggestionListItemBinding;

import java.text.MessageFormat;
import java.util.List;

/**
 * Adapter de suggestions
 */
public class SuggestionAdapter extends CursorAdapter {
    private final SearchView searchView;
    private final List<Address> addresses;
    private TextView suggestionAddress;
    private TextView suggestionLocality;

    /**
     * Adapter de suggestions
     *
     * @param context contexte de l'application
     * @param searchView bare de recherche
     * @param addresses listes d'addresses de suggestion
     */
    public SuggestionAdapter(Context context, SearchView searchView, List<Address> addresses) {
        super(context, SuggestionAdapter.getCursorAdapter(addresses), FLAG_REGISTER_CONTENT_OBSERVER);

        this.searchView = searchView;
        this.addresses = addresses;
    }

    /**
     * Retourne un cursor avec les addresses données
     *
     * @param addresses liste de suggestions d'addresses
     * @return cursor avec les addresses données
     */
    private static MatrixCursor getCursorAdapter(List<Address> addresses) {
        String[] columns = new String[]{"_id", "text"};
        MatrixCursor cursor = new MatrixCursor(columns);

        for (int index = 0; index < addresses.size(); index++) {
            Address address = addresses.get(index);

            // récupération de l'adresse avec la rue, la ville et le pays
            cursor.addRow(new Object[]{index, address.getAddressLine(0)});
        }

        return cursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SuggestionListItemBinding binding = SuggestionListItemBinding.inflate(inflater);

        suggestionAddress = binding.suggestionAddress;
        suggestionLocality = binding.suggestionLocality;

        return binding.getRoot();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Address address = addresses.get(cursor.getPosition());

        view.setOnClickListener(v -> searchView.setQuery(address.getAddressLine(0), true));
        suggestionAddress.setText(address.getAddressLine(0));
        suggestionLocality.setText(MessageFormat.format("{0}, {1}", address.getLocality(), address.getCountryName()));
    }
}
