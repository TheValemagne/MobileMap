package com.example.mobilemap.pois;

import android.text.Editable;
import android.text.TextWatcher;

import com.example.mobilemap.map.manager.MiniMapManager;

/**
 * Ecouteur de modification d'un champ de texte pour l'actualisation de la mini carte
 *
 * @author J.Houdé
 */
public class PoiTextWatcher implements TextWatcher {
    private final MiniMapManager miniMapManager;

    /**
     * Ecouteur de modification d'un champ de texte pour l'actualisation de la mini carte
     *
     * @param miniMapManager gestionnaire de la mini carte
     */
    public PoiTextWatcher(MiniMapManager miniMapManager) {
        this.miniMapManager = miniMapManager;
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
        miniMapManager.updateMap();
    }
}
