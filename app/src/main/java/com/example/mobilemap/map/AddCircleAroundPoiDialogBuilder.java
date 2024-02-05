package com.example.mobilemap.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mobilemap.R;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.databinding.DialogAskCircleRadiusBinding;

import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;
import java.util.stream.Collectors;

public class AddCircleAroundPoiDialogBuilder extends AlertDialog.Builder {
    private final MapManager mapManager;
    private List<Category> categories;
    public AddCircleAroundPoiDialogBuilder(Activity activity, MapManager mapManager, OverlayItem item) {
        super(activity);
        this.mapManager = mapManager;

        Resources resources = activity.getResources();
        this.setTitle(resources.getString(R.string.ask_circle_perimeter_title));

        DialogAskCircleRadiusBinding binding = DialogAskCircleRadiusBinding.inflate(activity.getLayoutInflater());
        this.setView(binding.getRoot());

        final EditText editCircleRadius = binding.editCircleRadius;
        final Spinner categoryFilter = binding.categoryFilter;
        initSpinner(categoryFilter, activity);

        this.setPositiveButton(resources.getString(R.string.dialog_show), (dialog, which) -> {
            String textRadius = editCircleRadius.getText().toString();

            if (textRadius.isEmpty()) {
                return;
            }

            double circleRadius = Double.parseDouble(textRadius);
            long categoryFilterValue = getSelectedValue(categoryFilter);

            if (item != null){
                this.mapManager.drawCircle(item, circleRadius, categoryFilterValue);
            } else {
                this.mapManager.drawCircleAroundMe(circleRadius, categoryFilterValue);
            }

        });
        this.setNegativeButton(resources.getString(R.string.dialog_cancel), (dialog, which) -> dialog.cancel());
    }

    private void initSpinner(Spinner spinner, Activity activity) {
        categories = ContentResolverHelper.getCategories(activity.getContentResolver());
        List<String> categoryNames = categories.stream().map(Category::getName).collect(Collectors.toList());
        spinner.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames));
    }

    private long getSelectedValue(Spinner spinner) {
        String categoryName = (String) spinner.getSelectedItem();
        Category selectedCategory = categories.stream().filter(category -> category.getName().equals(categoryName)).findFirst().orElse(null);

        return selectedCategory != null ? selectedCategory.getId() : -1;
    }

}
