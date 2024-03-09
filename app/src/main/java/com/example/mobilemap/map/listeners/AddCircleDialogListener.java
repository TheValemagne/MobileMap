package com.example.mobilemap.map.listeners;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.databinding.DialogAskCircleRadiusBinding;
import com.example.mobilemap.map.manager.MapManager;
import com.example.mobilemap.validators.IsFieldSet;

import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

/**
 * Ecouteur de validation du dialogue de création de cercle
 *
 * @author J.Houdé
 */
public class AddCircleDialogListener implements View.OnClickListener {
    private final AlertDialog dialog;
    private final MapManager mapManager;
    private final List<Category> categories;
    private final OverlayItem item;
    private final Resources resources;
    private final EditText editCircleRadius;
    private final Spinner categoryFilterSpinner;

    /**
     * Ecouteur de validation du dialogue de création de cercle
     *
     * @param dialog     le dialog à associer
     * @param mapManager gestionnaire de la carte
     * @param item       élément centrale du cercle à afficher
     * @param binding    lien à la vue du dialogue
     */
    public AddCircleDialogListener(AlertDialog dialog,
                                   MapManager mapManager,
                                   Activity activity,
                                   OverlayItem item,
                                   DialogAskCircleRadiusBinding binding) {
        this.dialog = dialog;
        this.mapManager = mapManager;
        this.categories = ContentResolverHelper.getCategories(activity.getContentResolver());
        this.resources = activity.getResources();

        this.item = item;
        this.editCircleRadius = binding.editCircleRadius;
        this.categoryFilterSpinner = binding.categoryFilter;
    }

    @Override
    public void onClick(View v) {
        if (!new IsFieldSet(editCircleRadius, resources).check()) {
            return;
        }

        drawCircle(dialog);
    }

    /**
     * Génération du cercle sur la carte
     *
     * @param dialog dialog d'interaction
     */
    private void drawCircle(AlertDialog dialog) {
        double circleRadius = Double.parseDouble(editCircleRadius.getText().toString());
        long categoryFilterValue = getSelectedCategoryId();

        if (item != null) { // dessiner un cercle autour d'un lieu indiqué sur la carte
            this.mapManager.drawCircle(item, circleRadius, categoryFilterValue);
        } else { // dessiner un cercle autour de la position actuelle de l'utilisateur
            mapManager.getMyLocationPoint()
                    .ifPresent(location -> this.mapManager.drawCircleAroundMe(location, circleRadius, categoryFilterValue));
        }

        this.mapManager.centerToCircleCenter(); // centrage de la carte sur le centre du cercle tracé

        dialog.dismiss();
    }

    /**
     * Récupère l'identifiant de la catégorie sélectionnée
     *
     * @return identifiant de la catégorie sélectionnée
     */
    private long getSelectedCategoryId() {
        String categoryName = (String) categoryFilterSpinner.getSelectedItem();
        Category selectedCategory = categories.stream().filter(category -> category.getName().equals(categoryName)).findFirst().orElse(null);

        return selectedCategory != null ? selectedCategory.getId() : DatabaseContract.NOT_EXISTING_ID;
    }
}
