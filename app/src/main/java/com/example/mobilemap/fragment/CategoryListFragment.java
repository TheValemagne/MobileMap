package com.example.mobilemap.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilemap.CategoriesActivity;
import com.example.mobilemap.adapter.CategoryListRecyclerViewAdapter;
import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.databinding.FragmentCategoriesListBinding;
import com.example.mobilemap.listener.ShowCategoryDetailListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryListFragment extends Fragment {

    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentCategoriesListBinding binding = FragmentCategoriesListBinding.inflate(inflater, container, false);

        CategoriesActivity activity = (CategoriesActivity) requireActivity();
        binding.addCategoryButton.setOnClickListener(new ShowCategoryDetailListener(activity));

        initRecyclerView(binding, activity);

        return binding.getRoot();
    }

    private void initRecyclerView(FragmentCategoriesListBinding binding, CategoriesActivity activity) {
        List<Category> categories = activity.getCategories();

        if (!categories.isEmpty()) {
            binding.emptyLabel.setVisibility(View.INVISIBLE);
        }

        RecyclerView recyclerView = binding.categoryList;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        CategoryListRecyclerViewAdapter adapter = new CategoryListRecyclerViewAdapter(categories, activity.getContentResolver(), activity);
        recyclerView.setAdapter(adapter);
    }
}