package com.example.mobilemap.fragment;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilemap.adapter.CategoryListRecyclerViewAdapter;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.databinding.FragmentCategoriesListBinding;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesListFragment extends Fragment {
    private CategoryListRecyclerViewAdapter adapter;

    public CategoriesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentCategoriesListBinding binding = FragmentCategoriesListBinding.inflate(inflater, container, false);

        List<Category> categories = getCategories();

        if (!categories.isEmpty()) {
            binding.emptyLabel.setVisibility(View.INVISIBLE);
        }

        RecyclerView recyclerView = binding.categoriesList;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new CategoryListRecyclerViewAdapter(categories, requireActivity().getContentResolver());
        recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    private void updateList() {
        adapter.updateList(getCategories());
    }

    @NonNull
    private List<Category> getCategories() {
        Cursor cursor = requireActivity().getContentResolver()
                .query(DatabaseContract.Category.CONTENT_URI, DatabaseContract.Category.COLUMNS, null, null, DatabaseContract.Category.COLUMN_NAME);
        assert cursor != null;

        return Category.mapFromList(cursor);
    }
}