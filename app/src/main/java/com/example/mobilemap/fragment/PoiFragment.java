package com.example.mobilemap.fragment;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mobilemap.PoisActivity;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.database.table.Poi;
import com.example.mobilemap.databinding.FragmentPoiBinding;
import com.example.mobilemap.listener.DeleteDatabaseItemListener;
import com.example.mobilemap.listener.SaveDatabaseItemListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PoiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PoiFragment extends Fragment implements ItemView<Poi> {

    private static final String ARG_ITEM_ID = "itemId";
    private long itemId = -1;
    private Poi poi = null;
    private FragmentPoiBinding binding;
    private List<Category> categories;

    private PoisActivity activity;

    public PoiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param itemId Parameter 1.
     * @return A new instance of fragment PoiFragment.
     */
    public static PoiFragment newInstance(long itemId) {
        PoiFragment fragment = new PoiFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemId = getArguments().getLong(ARG_ITEM_ID);
        }

        activity = (PoisActivity) requireActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPoiBinding.inflate(inflater, container, false);

        categories = ContentResolverHelper.getCategories(requireActivity().getContentResolver());
        initCategoryDropDown();

        if (itemId == -1) {
            binding.poiDeleteBtn.setVisibility(View.GONE);
            binding.poiBtnSpace.setVisibility(View.GONE);
        } else {
            poi = findPoi(itemId);
            updateFields(binding);
        }

        bindActionButtons(binding);

        return binding.getRoot();
    }

    private void initCategoryDropDown() {
        List<String> categoryNames = categories.stream().map(Category::getName).collect(Collectors.toList());
        binding.categoryDropDown.setAdapter(new ArrayAdapter<>(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames));
    }

    private Poi findPoi(long id) {
        Cursor cursor = requireActivity().getContentResolver()
                .query(DatabaseContract.Poi.CONTENT_URI, DatabaseContract.Poi.COLUMNS, DatabaseContract.Poi._ID + " = " + id,
                        null, null);
        assert cursor != null;
        cursor.moveToFirst();

        return Poi.fromCursor(cursor);
    }

    private void bindActionButtons(FragmentPoiBinding binding) {
        binding.poiSaveBtn.setOnClickListener(new SaveDatabaseItemListener<>(activity, this, DatabaseContract.Poi.CONTENT_URI));
        binding.poiCancelBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());

        if(poi != null) {
            binding.poiDeleteBtn.setOnClickListener(new DeleteDatabaseItemListener(poi.getId(), activity, activity.getDeleteContext()));
        }
    }

    private void updateFields(FragmentPoiBinding binding) {
        binding.poiName.setText(poi.getName());
        binding.poiLatitude.setText(String.valueOf(poi.getLatitude()));
        binding.poiLongitude.setText(String.valueOf(poi.getLongitude()));
        binding.poiPostalAddress.setText(poi.getPostalAddress());
        setSelectedValue(binding.categoryDropDown, poi.getCategoryId());
        binding.poiResume.setText(poi.getResume());
    }

    private void setSelectedValue(Spinner spinner, long id) {
        for (int index = 0; index < categories.size(); index++) {
            if (categories.get(index).getId() == id) {
                spinner.setSelection(index);
                return;
            }
        }
    }

    private long getSelectedValue(Spinner spinner) {
        String categoryName = (String) spinner.getSelectedItem();
        Category selectedCategory = categories.stream().filter(category -> category.getName().equals(categoryName)).findFirst().orElse(null);

        return selectedCategory != null ? selectedCategory.getId() : -1;
    }


    @Override
    public boolean check() {
        return true;
    }

    @Override
    public Poi getValues() {
        String name = binding.poiName.getText().toString();
        float latitude = Float.parseFloat(binding.poiLatitude.getText().toString());
        float longitude = Float.parseFloat(binding.poiLongitude.getText().toString());
        String postalAddress = binding.poiPostalAddress.getText().toString();
        long categoryId = getSelectedValue(binding.categoryDropDown);
        String resume = binding.poiResume.getText().toString();

        System.out.println(categoryId);

        if (poi == null) {
            return new Poi(name, latitude, longitude, postalAddress, categoryId, resume);
        }

        poi.setName(name);
        poi.setLatitude(latitude);
        poi.setLongitude(longitude);
        poi.setPostalAddress(postalAddress);
        poi.setCategoryId(categoryId);
        poi.setResume(resume);

        return poi;
    }
}