package com.example.mobilemap.categories.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilemap.categories.CategoryListRecyclerViewAdapter;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.databinding.FragmentCategoryListBinding;
import com.example.mobilemap.categories.ShowCategoryListener;

import java.util.List;

/**
 * Fragment permettant la gestion de lal iste des catégories
 * A simple {@link Fragment} subclass.
 */
public class CategoryListFragment extends Fragment {

    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentCategoryListBinding binding = FragmentCategoryListBinding.inflate(inflater, container, false);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        binding.addCategoryButton.setOnClickListener(new ShowCategoryListener(activity));

        initRecyclerView(binding, activity);

        return binding.getRoot();
    }

    /**
     * Initialisation de la liste des catégories
     *
     * @param binding  classe contennat les éléments du fragments
     * @param activity activité à l'origine du fragment
     */
    private void initRecyclerView(FragmentCategoryListBinding binding, AppCompatActivity activity) {
        List<Category> categories = ContentResolverHelper.getCategories(activity.getContentResolver());

        if (!categories.isEmpty()) {
            binding.emptyLabel.setVisibility(View.INVISIBLE);
        }

        RecyclerView recyclerView = binding.categoryList;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        CategoryListRecyclerViewAdapter adapter = new CategoryListRecyclerViewAdapter(categories, activity.getContentResolver(), activity);
        recyclerView.setAdapter(adapter);
    }
}