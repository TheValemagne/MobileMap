package com.example.mobilemap.categories.fragments;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mobilemap.categories.CategoriesActivity;
import com.example.mobilemap.R;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.databinding.FragmentCategoryBinding;
import com.example.mobilemap.database.interfaces.ItemView;
import com.example.mobilemap.listeners.CancelAction;
import com.example.mobilemap.listeners.DeleteDatabaseItemListener;
import com.example.mobilemap.listeners.SaveDatabaseItemListener;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Fragment permettant la gestion d'une catégorie
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment implements ItemView<Category> {
    private static final String ARG_ITEM_ID = "itemId";
    private long itemId = -1;
    private Category category = null;
    private FragmentCategoryBinding binding;
    private List<String> categoryNames;

    private CategoriesActivity activity;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param itemId Parameter 1.
     * @return A new instance of fragment CategoryFragment.
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
        if (getArguments() != null) {
            itemId = getArguments().getLong(ARG_ITEM_ID);
        }

        activity = (CategoriesActivity) requireActivity();
        categoryNames = ContentResolverHelper.getCategories(activity.getContentResolver())
                .stream().map(Category::getName)
                .collect(Collectors.toList()); // récupérer la lsite des noms uniques des catégories existantes
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);

        if (itemId == -1) { // mode ajout d'une catégorie
            binding.categoryDeleteBtn.setVisibility(View.GONE);
            binding.deleteBtnSpace.setVisibility(View.GONE);
        } else { // mode modification d'une catégorie
            Optional<Category> foundCategory = findCategory(itemId);

            if (foundCategory.isPresent()) {
                category = foundCategory.get();
                binding.categoryName.setText(category.getName());
            }
        }

        bindActionButtons();

        return binding.getRoot();
    }

    /**
     * Initialisation des écouteurs des boutons d'actions : annuler, supprimer, enregistrer
     */
    private void bindActionButtons() {
        binding.categorySaveBtn.setOnClickListener(new SaveDatabaseItemListener<>(activity, this, DatabaseContract.Category.CONTENT_URI, false));
        binding.categoryCancelBtn.setOnClickListener(new CancelAction(activity, false));
        if(category != null) {
            binding.categoryDeleteBtn.setOnClickListener(new DeleteDatabaseItemListener(category.getId(), activity, activity.getDeleteContext()));
        }
    }

    /**
     * Retourne la catégorie avec l'identifiant indiqué
     * @param id indentifiant de la catégorie à chercher
     * @return la catégorie recherchée
     */
    private Optional<Category> findCategory(long id) {
        Cursor cursor = requireActivity().getContentResolver()
                .query(DatabaseContract.Category.CONTENT_URI, DatabaseContract.Category.COLUMNS, DatabaseContract.Category._ID + " = " + id,
                        null, null);

        if (cursor == null) {
            return Optional.empty();
        }
        cursor.moveToFirst();

        return Optional.of(Category.fromCursor(cursor));
    }

    /**
     * Vérifie que toutes les données entrées sont valides.
     *
     * @return vrai si les données sont valides
     */
    public boolean check() {
        TextView categoryName = binding.categoryName;

        String name = categoryName.getText().toString().trim();
        Resources resources = requireActivity().getResources();

        if(name.length() == 0) {
            categoryName.setError(resources.getString(R.string.error_field_is_empty));
        }

        if(categoryNames.contains(name)) {
            categoryName.setError(resources.getString(R.string.error_category_not_unique));
        }

        return name.length() != 0 && !categoryNames.contains(name);
    }

    /**
     * Récupère la catégorie en fonction des données entrées
     *
     * @return la catégorie avec le contenu entré dans la page
     */
    public Category getValues() {
        String name = binding.categoryName.getText().toString();

        if (category == null) {
            return new Category(name);
        }

        category.setName(name);
        return category;
    }
}