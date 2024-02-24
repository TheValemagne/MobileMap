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

import com.example.mobilemap.R;

import java.text.MessageFormat;
import java.util.List;

public class SuggestionAdapter extends CursorAdapter {
    private final SearchView searchView;
    private final List<Address> addresses;
    private TextView suggestionAddress;
    private TextView suggestionLocality;

    public SuggestionAdapter(Context context, Cursor c, SearchView searchView, List<Address> addresses) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        this.searchView = searchView;

        this.addresses = addresses;
    }

    public static MatrixCursor getCursorAdapter(List<Address> addresses) {
        Object[] temp = new Object[]{0, "default"};
        String[] columns = new String[]{"_id", "text"};
        MatrixCursor cursor = new MatrixCursor(columns);

        for (int i = 0; i < addresses.size(); i++) {
            Address address = addresses.get(i);
            temp[0] = i;
            temp[1] = address.getAddressLine(0);
            cursor.addRow(temp);
        }

        return cursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.suggestion_list_item, parent, false);

        suggestionAddress = view.findViewById(R.id.suggestionAddress);
        suggestionLocality = view.findViewById(R.id.suggestionLocality);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Address address = addresses.get(cursor.getPosition());

        view.setOnClickListener(v -> searchView.setQuery(address.getAddressLine(0), true));
        suggestionAddress.setText(address.getAddressLine(0));
        suggestionLocality.setText(MessageFormat.format("{0}, {1}", address.getLocality(), address.getCountryName()));
    }
}
