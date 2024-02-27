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

import com.example.mobilemap.adapters.FragmentListView;
import com.example.mobilemap.categories.CategoryListRecyclerViewAdapter;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.databinding.FragmentCategoryListBinding;
import com.example.mobilemap.categories.ShowCategoryListener;

import java.util.List;

/**
 * Fragment permettant la gestion de la liste des catégories
 * A simple {@link Fragment} subclass.
 *
 * @author J.Houdé
 */
public class CategoryListFragment extends Fragment implements FragmentListView {
    private FragmentCategoryListBinding binding;

    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        binding.addCategoryButton.setOnClickListener(new ShowCategoryListener(activity));

        initRecyclerView(activity);

        return binding.getRoot();
    }

    /**
     * Initialisation de la liste des catégories
     *
     * @param activity activité à l'origine du fragment
     */
    private void initRecyclerView(AppCompatActivity activity) {
        updateView();

        RecyclerView recyclerView = binding.categoryList;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        CategoryListRecyclerViewAdapter adapter = new CategoryListRecyclerViewAdapter(ContentResolverHelper.getCategories(activity.getContentResolver()),
                activity.getContentResolver(), activity, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void updateView() {
        List<Category> categories = ContentResolverHelper.getCategories(requireActivity().getContentResolver());

        binding.emptyLabel.setVisibility(categories.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }
}