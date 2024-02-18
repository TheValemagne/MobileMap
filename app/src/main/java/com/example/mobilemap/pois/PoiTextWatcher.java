package com.example.mobilemap.pois;

import android.text.Editable;
import android.text.TextWatcher;

import com.example.mobilemap.pois.fragments.PoiFragment;

public class PoiTextWatcher implements TextWatcher {
    private final PoiFragment fragment;

    public PoiTextWatcher(PoiFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // hint vide
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // hint vide
    }

    @Override
    public void afterTextChanged(Editable s) {
        fragment.updateMiniMap();
    }
}
