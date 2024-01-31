package com.example.mobilemap.fragment;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mobilemap.CategoriesActivity;
import com.example.mobilemap.R;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.databinding.FragmentCategoryBinding;
import com.example.mobilemap.listener.DeleteCategoryListener;
import com.example.mobilemap.listener.SaveCategoryListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {
    private static final String ARG_ITEM_ID = "itemId";
    private long itemId = -1;
    private Category category = null;
    private TextView categoryName;
    private List<String> categoryNames;

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

        CategoriesActivity activity = (CategoriesActivity) requireActivity();
        categoryNames = activity.getCategories().stream()
                .map(Category::getName).collect(Collectors.toList());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentCategoryBinding binding = FragmentCategoryBinding.inflate(inflater, container, false);

        categoryName = binding.categoryName;

        binding.categorySaveBtn.setOnClickListener(new SaveCategoryListener((AppCompatActivity) requireActivity(), this, requireActivity().getContentResolver()));
        binding.categoryCancelBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        if (itemId == -1) {
            binding.categoryDeleteBtn.setVisibility(View.GONE);
            binding.deleteBtnSpace.setVisibility(View.GONE);
        } else {
            category = findCategory(itemId);
            categoryName.setText(category.getName());
            binding.categoryDeleteBtn.setOnClickListener(new DeleteCategoryListener(category.getId(), (CategoriesActivity) requireActivity()));
        }

        return binding.getRoot();
    }

    private Category findCategory(long id) {
        Cursor cursor = requireActivity().getContentResolver()
                .query(DatabaseContract.Category.CONTENT_URI, DatabaseContract.Category.COLUMNS, DatabaseContract.Category._ID + " = " + id,
                        null, DatabaseContract.Category.COLUMN_NAME);
        assert cursor != null;
        cursor.moveToFirst();

        return Category.fromCursor(cursor);
    }

    public boolean check() {
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

    public Category getValues() {
        String name = categoryName.getText().toString();

        if (category == null) {
            return new Category(name);
        }

        category.setName(name);
        return category;
    }
}