package com.example.mobilemap.categories.fragments;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilemap.R;
import com.example.mobilemap.categories.CategoriesActivity;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.databinding.FragmentCategoryBinding;
import com.example.mobilemap.database.interfaces.ItemView;
import com.example.mobilemap.listeners.CancelAction;
import com.example.mobilemap.listeners.DeleteDatabaseItemListener;
import com.example.mobilemap.listeners.SaveDatabaseItemListener;
import com.example.mobilemap.validators.FieldValidator;
import com.example.mobilemap.validators.IsFieldSet;
import com.example.mobilemap.validators.IsUniqueCategoryValidator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Fragment permettant la gestion d'une catégorie
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author J.Houdé
 */
public class CategoryFragment extends Fragment implements ItemView<Category> {
    private static final String ARG_ITEM_ID = "itemId";
    private long itemId = DatabaseContract.NOT_EXISTING_ID;
    private Category category = null;
    private FragmentCategoryBinding binding;
    private List<String> categoryNames;

    private CategoriesActivity activity;
    private List<FieldValidator> fieldValidators;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Méthode factory pour créer une nouvelle instance avec un identifiant de catégorie
     *
     * @param itemId identifiant de la catégorie à afficher.
     * @return Nouvelle instance du fragment CategoryFragment.
     */
    public static CategoryFragment newInstance(long itemId) {
        CategoryFragment fragment = new CategoryFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) { // initilisation de l'identifiant de la catégorie si existant
            itemId = getArguments().getLong(ARG_ITEM_ID);
        }

        activity = (CategoriesActivity) requireActivity();
        categoryNames = ContentResolverHelper.getCategories(activity.getContentResolver())
                .stream().map(Category::getName)
                .collect(Collectors.toList()); // récupérer la liste des noms uniques des catégories existantes
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);

        Resources resources = requireActivity().getResources();
        binding.title.setText(resources.getText(itemId == DatabaseContract.NOT_EXISTING_ID ? R.string.add_category : R.string.edit_category));

        if (itemId == DatabaseContract.NOT_EXISTING_ID) { // ajout d'une nouvelle catégorie
            binding.categoryDeleteBtn.setVisibility(View.GONE);
            binding.deleteBtnSpace.setVisibility(View.GONE);
        } else { // modification d'une catégorie
            Optional<Category> foundCategory = findCategory(itemId);

            if (foundCategory.isPresent()) {
                category = foundCategory.get();
                binding.categoryName.setText(foundCategory.get().getName());

                categoryNames.remove(category.getName()); // enlève le nom de la catégorie actuelle pour permettre une modification des données
            }
        }

        bindActionButtons();
        initValidators();

        return binding.getRoot();
    }

    /**
     * Initialisation des écouteurs des boutons d'actions : annuler, supprimer et enregistrer
     */
    private void bindActionButtons() {
        binding.categorySaveBtn.setOnClickListener(new SaveDatabaseItemListener<>(activity, this, DatabaseContract.Category.CONTENT_URI));
        binding.categoryCancelBtn.setOnClickListener(new CancelAction(activity, false));

        if (category != null) {
            binding.categoryDeleteBtn.setOnClickListener(new DeleteDatabaseItemListener(category.getId(), activity, activity.getDeleteContext()));
        }
    }

    /**
     * Initialisation des validateurs de champs textes
     */
    private void initValidators() {
        Resources resources = requireActivity().getResources();

        fieldValidators = new ArrayList<>(Arrays.asList(
                new IsFieldSet(binding.categoryName, resources), // vérification si un onm a été défini
                new IsUniqueCategoryValidator(binding.categoryName, resources, categoryNames) // vérification si le nom de catégorie est unique (catégorie à modifier exclue)
        ));
    }

    /**
     * Retourne la catégorie avec l'identifiant indiqué
     *
     * @param id indentifiant de la catégorie à chercher
     * @return la catégorie recherchée
     */
    private Optional<Category> findCategory(long id) {
        Cursor cursor = requireActivity().getContentResolver()
                .query(DatabaseContract.Category.CONTENT_URI, DatabaseContract.Category.COLUMNS, MessageFormat.format("{0} = {1}", DatabaseContract.Category._ID, id),
                        null, null);

        if (cursor == null) {
            return Optional.empty();
        }
        cursor.moveToFirst();

        return Optional.of(Category.fromCursor(cursor));
    }
    
    @Override
    public boolean isValid() {
        return fieldValidators.stream().allMatch(FieldValidator::check);
    }

    /**
     * Récupère la catégorie en fonction des données entrées
     *
     * @return la catégorie avec le contenu entré dans la page
     */
    @Override
    public Category getValues() {
        String name = binding.categoryName.getText().toString().trim();

        if (category == null) { // création d'une nouvelle catégorie
            return new Category(name);
        }

        // modification de la catégorie existante
        category.setName(name);
        return category;
    }
}