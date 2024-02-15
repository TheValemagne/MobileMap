package com.example.mobilemap.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mobilemap.R;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.databinding.DialogAskCircleRadiusBinding;
import com.example.mobilemap.map.listeners.AddCircleDialogListener;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Dialogue de demmande de données pour afficer un cercle
 */
public class AddCircleAroundPoiDialogBuilder extends AlertDialog.Builder {
    private final MapManager mapManager;
    private List<Category> categories;
    private final OverlayItem item;
    private final Resources resources;
    private final EditText editCircleRadius;
    private final Spinner categoryFilterSpinner;

    /**
     * Dialogue de demmande de données pour afficher un cercle
     *
     * @param activity   activité mère
     * @param mapManager gestionnaire de la carte
     * @param item       élément reptrésentant le centre du futur cercle
     */
    public AddCircleAroundPoiDialogBuilder(Activity activity, MapManager mapManager, OverlayItem item) {
        super(activity);
        this.mapManager = mapManager;
        this.item = item;

        resources = activity.getResources();
        this.setTitle(resources.getString(R.string.ask_circle_perimeter_title));
        this.setIcon(org.osmdroid.library.R.drawable.marker_default);

        DialogAskCircleRadiusBinding binding = DialogAskCircleRadiusBinding.inflate(activity.getLayoutInflater());
        this.setView(binding.getRoot());

        editCircleRadius = binding.editCircleRadius;
        categoryFilterSpinner = binding.categoryFilter;
        initSpinner(categoryFilterSpinner, activity);

        this.setNegativeButton(resources.getString(R.string.dialog_cancel), (dialog, which) -> dialog.cancel());
    }

    @Override
    public AlertDialog show() {
        AlertDialog dialog = super.show();

        Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        buttonPositive.setText(resources.getString(R.string.dialog_show));
        buttonPositive.setVisibility(View.VISIBLE);
        buttonPositive.setOnClickListener(new AddCircleDialogListener(dialog, this));

        return dialog;
    }

    public boolean check() {
        String textRadius = editCircleRadius.getText().toString();

        if (textRadius.isEmpty()) {
            editCircleRadius.setError(resources.getString(R.string.error_field_is_empty));
            return false;
        }

        return true;
    }

    /**
     * Génération du cercle sur la carte
     *
     * @param dialog dialog d'interaction
     */
    public void showCircle(AlertDialog dialog) {
        double circleRadius = Double.parseDouble(editCircleRadius.getText().toString());
        long categoryFilterValue = getSelectedCategoryId(categoryFilterSpinner);

        if (item != null) {
            this.mapManager.drawCircle(item, circleRadius, categoryFilterValue);
        } else {
            Optional<GeoPoint> point = mapManager.getLocationPoint();

            point.ifPresent(location -> this.mapManager.drawCircleAroundMe(location, circleRadius, categoryFilterValue));
        }

        this.mapManager.centerToCircleCenter();

        dialog.dismiss();
    }

    /**
     * Initialisation des options de la boîte de sélection
     *
     * @param spinner  boîte de sélection
     * @param activity activité mère
     */
    private void initSpinner(Spinner spinner, Activity activity) {
        categories = ContentResolverHelper.getCategories(activity.getContentResolver());

        List<String> categoryNames = categories.stream().map(Category::getName).collect(Collectors.toList());
        spinner.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames));
    }

    /**
     * Récupère l'identifiant de la catégorie sélectionnée
     *
     * @param spinner boîte de sélection
     * @return identifiant de la catégorie sélectionnée
     */
    private long getSelectedCategoryId(Spinner spinner) {
        String categoryName = (String) spinner.getSelectedItem();
        Category selectedCategory = categories.stream().filter(category -> category.getName().equals(categoryName)).findFirst().orElse(null);

        return selectedCategory != null ? selectedCategory.getId() : DatabaseContract.NOT_EXISTING_ID;
    }

}
