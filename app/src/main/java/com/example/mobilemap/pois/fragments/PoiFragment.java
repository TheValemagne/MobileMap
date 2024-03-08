package com.example.mobilemap.pois.fragments;

import android.content.res.Resources;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.mobilemap.R;
import com.example.mobilemap.database.interfaces.ItemView;
import com.example.mobilemap.map.manager.MiniMapManager;
import com.example.mobilemap.pois.listeners.SavePoiListener;
import com.example.mobilemap.pois.listeners.GeocodeAddressListener;
import com.example.mobilemap.validators.DoubleRangeValidator;
import com.example.mobilemap.validators.FieldValidator;
import com.example.mobilemap.validators.IsFieldSet;
import com.example.mobilemap.pois.PoiTextWatcher;
import com.example.mobilemap.pois.PoisActivity;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.database.tables.Poi;
import com.example.mobilemap.databinding.FragmentPoiBinding;
import com.example.mobilemap.listeners.CancelAction;
import com.example.mobilemap.listeners.DeleteDatabaseItemListener;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PoiFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author J.Houdé
 */
public class PoiFragment extends Fragment implements ItemView<Poi> {

    public static final String ARG_ITEM_ID = "itemId";
    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LONGITUDE = "longitude";
    public static final String ARG_LAUNCHED_FOR_RESULT = "launchedForResult";

    private long itemId = DatabaseContract.NOT_EXISTING_ID;
    private boolean launchedForResult = false;
    private Poi poi = null;
    private FragmentPoiBinding binding;
    private List<Category> categories;
    private PoisActivity activity;
    private Geocoder geocoder;
    private List<FieldValidator> textFieldValidators;
    private List<FieldValidator> latitudeValidators;
    private List<FieldValidator> longitudeValidators;

    public PoiFragment() {
        // Required empty public constructor
    }

    /**
     * Factory méthod pour afficher le détail du site existant
     *
     * @param itemId            identifiant du site à afficher
     * @param launchedForResult si l'activité a été lancée pour retourner un résultat
     * @return nouvelle instance du fragment
     */
    public static PoiFragment newInstance(long itemId, boolean launchedForResult) {
        PoiFragment fragment = new PoiFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemId);
        args.putBoolean(ARG_LAUNCHED_FOR_RESULT, launchedForResult);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Factory method pour ajouter un site avec localisation
     *
     * @param latitude          latitude du nouveau site
     * @param longitude         longitude du nouveau site
     * @param launchedForResult si l'activité a été lancée pour retourner un résultat
     * @return nouvelle instance du fragment
     */
    public static PoiFragment newInstance(double latitude, double longitude, boolean launchedForResult) {
        PoiFragment fragment = new PoiFragment();

        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putBoolean(ARG_LAUNCHED_FOR_RESULT, launchedForResult);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFromArguments();

        activity = (PoisActivity) requireActivity();
        geocoder = new Geocoder(this.requireContext(), Locale.getDefault());
    }

    private void initFromArguments() {
        Bundle bundle = getArguments();

        if (bundle == null) {
            return;
        }

        if (bundle.containsKey(ARG_ITEM_ID)) {
            itemId = bundle.getLong(ARG_ITEM_ID);
        }

        if (bundle.containsKey(ARG_LAUNCHED_FOR_RESULT)) {
            launchedForResult = bundle.getBoolean(ARG_LAUNCHED_FOR_RESULT, false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPoiBinding.inflate(inflater, container, false);

        Resources resources = requireActivity().getResources();
        binding.title.setText(resources.getText(itemId == DatabaseContract.NOT_EXISTING_ID ? R.string.add_poi : R.string.edit_poi));

        categories = ContentResolverHelper.getCategories(requireActivity().getContentResolver());
        initCategoryDropDown();

        if (itemId == DatabaseContract.NOT_EXISTING_ID) {
            binding.poiDeleteBtn.setVisibility(View.GONE);
            binding.poiBtnSpace.setVisibility(View.GONE);
            updateFieldsFromArguments();
        } else {
            findPoi(itemId).ifPresent(item -> poi = item);
            updateFields();
        }

        MiniMapManager miniMapManager = new MiniMapManager(activity, this, binding.miniMapView,
                Arrays.asList(binding.poiLatitude, binding.poiLongitude));

        miniMapManager.initMap();
        miniMapManager.updateMap();

        PoiTextWatcher poiTextWatcher = new PoiTextWatcher(miniMapManager);
        Arrays.asList(
                binding.poiName,
                binding.poiLatitude,
                binding.poiLongitude,
                binding.poiResume
        ).forEach(editText -> editText.addTextChangedListener(poiTextWatcher));

        bindActionButtons();
        initValidators();

        return binding.getRoot();
    }

    /**
     * Mise à jour des champs de données avec les arguments donnés au lancement du fragment
     */
    private void updateFieldsFromArguments() {
        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey(ARG_LATITUDE) || !bundle.containsKey(ARG_LONGITUDE)) {
            return;
        }

        double latitude = bundle.getDouble(ARG_LATITUDE);
        binding.poiLatitude.setText(String.valueOf(latitude));

        double longitude = bundle.getDouble(ARG_LONGITUDE);
        binding.poiLongitude.setText(String.valueOf(longitude));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // uniquement pour les appareils tournant sous tiramisu
            geocoder.getFromLocation(latitude, longitude, 1, new GeocodeAddressListener(binding.poiPostalAddress));
        } else {
            binding.poiPostalAddress.setText(getAddressFromCoordinates(new GeoPoint(latitude, longitude)));
        }
    }

    /**
     * Initialisation des validateurs de champs textes
     */
    private void initValidators() {
        Resources resources = requireActivity().getResources();

        textFieldValidators = new ArrayList<>(Arrays.asList(
                new IsFieldSet(binding.poiName, resources), //vérification si le nom est défini
                new IsFieldSet(binding.poiPostalAddress, resources), //vérification si l'adresse est défini
                new IsFieldSet(binding.poiResume, resources) //vérification si le résumé est défini
        ));

        latitudeValidators = new ArrayList<>(Arrays.asList(
                new IsFieldSet(binding.poiLatitude, resources), //vérification si la latitude est définie
                new DoubleRangeValidator(binding.poiLatitude, resources, -90, 90) // la latitude doit être dans [-90, 90]
        ));

        longitudeValidators = new ArrayList<>(Arrays.asList(
                new IsFieldSet(binding.poiLongitude, resources), //vérification si la longitude est définie
                new DoubleRangeValidator(binding.poiLongitude, resources, -180, 180) // la longitude doit être dans [-180, 180]
        ));
    }

    /**
     * Retourne l'adresse postale accossiée aux coorconnées. Cette méthode est bloquante
     *
     * @param point coordonnées de l'adresse recherchée
     * @return l'adresse contenenant la rue, la ville et le pays
     */
    private String getAddressFromCoordinates(GeoPoint point) {
        if (!Geocoder.isPresent()) {
            return "";
        }

        Geocoder geocoder = new Geocoder(this.requireContext(), Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
        } catch (IOException e) {
            return "";
        }

        if (addresses == null || addresses.isEmpty()) {
            return "";
        }

        return addresses.get(0).getAddressLine(0);
    }

    /**
     * Mise à jour des champts de données avec le site
     */
    private void updateFields() {
        binding.poiName.setText(poi.getName());
        binding.poiLatitude.setText(String.valueOf(poi.getLatitude()));
        binding.poiLongitude.setText(String.valueOf(poi.getLongitude()));
        binding.poiPostalAddress.setText(poi.getPostalAddress());
        setSelectedCategory(poi.getCategoryId());
        binding.poiResume.setText(poi.getResume());
    }

    private void initCategoryDropDown() {
        List<String> categoryNames = categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        binding.categoryDropDown.setAdapter(new ArrayAdapter<>(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames));
    }

    private Optional<Poi> findPoi(long id) {
        Cursor cursor = requireActivity().getContentResolver()
                .query(DatabaseContract.Poi.CONTENT_URI, DatabaseContract.Poi.COLUMNS, MessageFormat.format("{0} = {1}", DatabaseContract.Poi._ID, id),
                        null, null);

        if (cursor == null) {
            return Optional.empty();
        }

        cursor.moveToFirst();

        return Optional.of(Poi.fromCursor(cursor));
    }

    /**
     * Initialisation des écouteurs des boutons d'actions : annuler, supprimer et enregistrer
     */
    private void bindActionButtons() {
        binding.poiSaveBtn.setOnClickListener(new SavePoiListener(activity, this, DatabaseContract.Poi.CONTENT_URI, launchedForResult));
        binding.poiCancelBtn.setOnClickListener(new CancelAction(activity, launchedForResult));

        if (poi != null) {
            binding.poiDeleteBtn.setOnClickListener(new DeleteDatabaseItemListener(poi.getId(), activity, activity.getDeleteContext(), launchedForResult));
        }
    }

    private void setSelectedCategory(long id) {
        for (int index = 0; index < categories.size(); index++) {
            if (categories.get(index).getId() == id) {
                binding.categoryDropDown.setSelection(index, true);
                return;
            }
        }
    }

    private long getSelectedCategory() {
        String categoryName = (String) binding.categoryDropDown.getSelectedItem();
        Category selectedCategory = categories.stream()
                .filter(category -> category.getName().equals(categoryName))
                .findFirst().orElse(null);

        return selectedCategory != null ? selectedCategory.getId() : -1;
    }

    @Override
    public boolean isValid() {
        boolean AreTextFieldsSet = textFieldValidators.stream()
                .map(FieldValidator::check)
                .reduce(true, (aBoolean, aBoolean2) -> aBoolean && aBoolean2);
        boolean isLatitudeValid = latitudeValidators.stream()
                .allMatch(FieldValidator::check);
        boolean isLongitudeValid = longitudeValidators.stream()
                .allMatch(FieldValidator::check);

        return AreTextFieldsSet && isLatitudeValid && isLongitudeValid;
    }

    @Override
    public Poi getValues() {
        String name = binding.poiName.getText().toString().trim();
        double latitude = Double.parseDouble(binding.poiLatitude.getText().toString().trim());
        double longitude = Double.parseDouble(binding.poiLongitude.getText().toString().trim());
        String postalAddress = binding.poiPostalAddress.getText().toString().trim();
        long categoryId = getSelectedCategory();
        String resume = binding.poiResume.getText().toString().trim();

        if (poi == null) { // création d'un nouveau site
            return new Poi(name, latitude, longitude, postalAddress, categoryId, resume);
        }

        // modification du site existant
        poi.setName(name);
        poi.setLatitude(latitude);
        poi.setLongitude(longitude);
        poi.setPostalAddress(postalAddress);
        poi.setCategoryId(categoryId);
        poi.setResume(resume);

        return poi;
    }
}