package com.example.mobilemap.pois.fragments;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mobilemap.R;
import com.example.mobilemap.database.interfaces.ItemView;
import com.example.mobilemap.map.SharedPreferencesConstant;
import com.example.mobilemap.validators.DoubleRangeValidator;
import com.example.mobilemap.validators.FieldValidator;
import com.example.mobilemap.validators.IsFieldEmpty;
import com.example.mobilemap.pois.PoiTextWatcher;
import com.example.mobilemap.pois.PoisActivity;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.database.tables.Poi;
import com.example.mobilemap.databinding.FragmentPoiBinding;
import com.example.mobilemap.listeners.CancelAction;
import com.example.mobilemap.listeners.DeleteDatabaseItemListener;
import com.example.mobilemap.listeners.SaveDatabaseItemListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PoiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PoiFragment extends Fragment implements ItemView<Poi> {

    private static final String ARG_ITEM_ID = "itemId";
    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LONGITUDE = "longitude";
    private long itemId = DatabaseContract.NOT_EXISTING_ID;
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

    public static PoiFragment newInstance(double latitude, double longitude) {
        PoiFragment fragment = new PoiFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
            itemId = getArguments().getLong(ARG_ITEM_ID);
        }

        activity = (PoisActivity) requireActivity();
    }

    private void updateFromArguments() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }

        if (bundle.containsKey(ARG_LATITUDE)) {
            binding.poiLatitude.setText(String.valueOf(bundle.getDouble(ARG_LATITUDE)));
        }

        if (bundle.containsKey(ARG_LONGITUDE)) {
            binding.poiLongitude.setText(String.valueOf(bundle.getDouble(ARG_LONGITUDE)));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPoiBinding.inflate(inflater, container, false);

        Resources resources = requireActivity().getResources();
        binding.title.setText(resources.getText(itemId == -1 ? R.string.add_poi : R.string.edit_poi));

        categories = ContentResolverHelper.getCategories(requireActivity().getContentResolver());
        initCategoryDropDown();

        if (itemId == -1) {
            binding.poiDeleteBtn.setVisibility(View.GONE);
            binding.poiBtnSpace.setVisibility(View.GONE);
            updateFromArguments();
        } else {
            poi = findPoi(itemId);
            updateFields(binding);
        }

        initMiniMap(binding.miniMapView);
        updateMiniMap();

        PoiTextWatcher poiTextWatcher = new PoiTextWatcher(this);
        binding.poiName.addTextChangedListener(poiTextWatcher);
        binding.poiLatitude.addTextChangedListener(poiTextWatcher);
        binding.poiLongitude.addTextChangedListener(poiTextWatcher);
        binding.poiResume.addTextChangedListener(poiTextWatcher);
        bindActionButtons(binding);

        return binding.getRoot();
    }

    private void updateFields(FragmentPoiBinding binding) {
        binding.poiName.setText(poi.getName());
        binding.poiLatitude.setText(String.valueOf(poi.getLatitude()));
        binding.poiLongitude.setText(String.valueOf(poi.getLongitude()));
        binding.poiPostalAddress.setText(poi.getPostalAddress());
        setSelectedValue(binding.categoryDropDown, poi.getCategoryId());
        binding.poiResume.setText(poi.getResume());
    }

    private void initMiniMap(MapView miniMapView) {
        miniMapView.setTileSource(TileSourceFactory.MAPNIK);
        miniMapView.getZoomController()
                .setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        miniMapView.setMultiTouchControls(true);
        miniMapView.setHorizontalMapRepetitionEnabled(false);
        miniMapView.setVerticalMapRepetitionEnabled(false);
        miniMapView.setMinZoomLevel(5.0);
        miniMapView.setMaxZoomLevel(20.0);

        GeoPoint initialCenter = new GeoPoint(Double.parseDouble(SharedPreferencesConstant.DEFAULT_LATITUDE),
                Double.parseDouble(SharedPreferencesConstant.DEFAULT_LONGITUDE));
        miniMapView.setScrollableAreaLimitDouble(new BoundingBox(initialCenter.getLatitude(), initialCenter.getLongitude(),
                initialCenter.getLatitude(), initialCenter.getLongitude()));

        double zoomLevel = 18.5;
        IMapController mapController = miniMapView.getController();
        mapController.setZoom(zoomLevel);
    }

    public void updateMiniMap() {
        List<TextView> textViews = new ArrayList<>(Arrays.asList(
                binding.poiLatitude,
                binding.poiLongitude
        ));

        if (textViews.stream().anyMatch(textView -> textView.getText().toString().isEmpty())) {
            return;
        }

        MapView miniMapView = binding.miniMapView;
        miniMapView.getOverlays().clear();

        Poi modifiedPoi = getValues();
        GeoPoint point = new GeoPoint(modifiedPoi.getLatitude(), modifiedPoi.getLongitude());
        miniMapView.setScrollableAreaLimitDouble(new BoundingBox(modifiedPoi.getLatitude(), modifiedPoi.getLongitude(),
                modifiedPoi.getLatitude(), modifiedPoi.getLongitude()));
        miniMapView.setExpectedCenter(point);

        Marker marker = new Marker(miniMapView);
        marker.setTitle(modifiedPoi.getName());
        marker.setSnippet(modifiedPoi.getResume());
        marker.setPosition(point);
        miniMapView.getOverlays().add(marker);
        marker.showInfoWindow();
    }

    private void initCategoryDropDown() {
        List<String> categoryNames = categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        binding.categoryDropDown.setAdapter(new ArrayAdapter<>(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames));
    }

    private Poi findPoi(long id) {
        Cursor cursor = requireActivity().getContentResolver()
                .query(DatabaseContract.Poi.CONTENT_URI, DatabaseContract.Poi.COLUMNS, MessageFormat.format("{0} = {1}", DatabaseContract.Poi._ID, id),
                        null, null);
        assert cursor != null;
        cursor.moveToFirst();

        return Poi.fromCursor(cursor);
    }

    private void bindActionButtons(FragmentPoiBinding binding) {
        binding.poiSaveBtn.setOnClickListener(new SaveDatabaseItemListener<>(activity, this, DatabaseContract.Poi.CONTENT_URI, isLaunchedForResult()));
        binding.poiCancelBtn.setOnClickListener(new CancelAction(activity, isLaunchedForResult()));

        if(poi != null) {
            binding.poiDeleteBtn.setOnClickListener(new DeleteDatabaseItemListener(poi.getId(), activity, activity.getDeleteContext()));
        }
    }

    private boolean isLaunchedForResult() {
        Bundle bundle = getArguments();

        if (bundle == null) {
            return false;
        }

        return bundle.containsKey(ARG_LATITUDE) && bundle.containsKey(ARG_LONGITUDE);
    }

    private void setSelectedValue(Spinner spinner, long id) {
        for (int index = 0; index < categories.size(); index++) {
            if (categories.get(index).getId() == id) {
                spinner.setSelection(index, true);
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
        Resources resources = requireActivity().getResources();

        List<FieldValidator> textValidators = new ArrayList<>(Arrays.asList(
                new IsFieldEmpty(binding.poiName, resources),
                new IsFieldEmpty(binding.poiPostalAddress, resources),
                new IsFieldEmpty(binding.poiResume, resources)
        ));

        List<FieldValidator> latitudeValidators = new ArrayList<>(Arrays.asList(
                new IsFieldEmpty(binding.poiLatitude, resources),
                new DoubleRangeValidator(binding.poiLatitude, resources, -90, 90)
        ));

        List<FieldValidator> longitudeValidators = new ArrayList<>(Arrays.asList(
                new IsFieldEmpty(binding.poiLongitude, resources),
                new DoubleRangeValidator(binding.poiLongitude, resources, -180, 180)
        ));

        boolean checkEmptyFields = textValidators.stream().map(FieldValidator::check)
                .reduce(true, (aBoolean, aBoolean2) -> aBoolean && aBoolean2);
        boolean checkLatitude = latitudeValidators.stream().allMatch(FieldValidator::check);
        boolean checkLongitude = longitudeValidators.stream().allMatch(FieldValidator::check);

        return checkEmptyFields && checkLatitude && checkLongitude;
    }

    @Override
    public Poi getValues() {
        String name = binding.poiName.getText().toString();
        double latitude = Double.parseDouble(binding.poiLatitude.getText().toString());
        double longitude = Double.parseDouble(binding.poiLongitude.getText().toString());
        String postalAddress = binding.poiPostalAddress.getText().toString();
        long categoryId = getSelectedValue(binding.categoryDropDown);
        String resume = binding.poiResume.getText().toString();

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