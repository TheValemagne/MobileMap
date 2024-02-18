package com.example.mobilemap.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.mobilemap.R;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.databinding.DialogAskCircleRadiusBinding;
import com.example.mobilemap.map.listeners.AddCircleDialogListener;

import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialogue de demande de données pour afficer un cercle
 *
 * @author J.Houdé
 */
public class AddCircleAroundPoiDialogBuilder extends AlertDialog.Builder {
    private final Activity activity;
    private final MapManager mapManager;
    private final OverlayItem item;
    private final Resources resources;
    private final DialogAskCircleRadiusBinding binding;

    /**
     * Dialogue de demande de données pour afficher un cercle
     *
     * @param activity   activité mère
     * @param mapManager gestionnaire de la carte
     * @param item       élément reptrésentant le centre du futur cercle
     */
    public AddCircleAroundPoiDialogBuilder(Activity activity, MapManager mapManager, OverlayItem item) {
        super(activity);
        this.activity = activity;
        this.mapManager = mapManager;
        this.item = item;

        resources = activity.getResources();
        this.setTitle(resources.getString(R.string.ask_circle_perimeter_title));

        binding = DialogAskCircleRadiusBinding.inflate(activity.getLayoutInflater());
        this.setView(binding.getRoot());

        initSpinner(binding.categoryFilter, activity);

        this.setNegativeButton(resources.getString(R.string.dialog_cancel), (dialog, which) -> dialog.cancel());
    }

    @Override
    public AlertDialog show() {
        AlertDialog dialog = super.show();

        Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        buttonPositive.setText(resources.getString(R.string.dialog_show));
        buttonPositive.setVisibility(View.VISIBLE);
        buttonPositive.setOnClickListener(new AddCircleDialogListener(dialog, mapManager, activity, item, binding));

        return dialog;
    }

    /**
     * Initialisation des options de la boîte de sélection
     *
     * @param spinner  boîte de sélection
     * @param activity activité mère
     */
    private void initSpinner(Spinner spinner, Activity activity) {
        List<Category> categories = ContentResolverHelper.getCategories(activity.getContentResolver());

        List<String> categoryNames = categories.stream().map(Category::getName).collect(Collectors.toList());
        spinner.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames));
    }

}
