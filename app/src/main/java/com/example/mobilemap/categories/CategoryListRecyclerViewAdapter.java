package com.example.mobilemap.categories;

import android.content.ContentResolver;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.adapters.BaseAdapter;
import com.example.mobilemap.categories.fragments.CategoryListFragment;
import com.example.mobilemap.database.tables.Category;
import com.example.mobilemap.databinding.CategoryListItemBinding;
import com.example.mobilemap.listeners.DeleteDatabaseListListener;

import java.util.List;

/**
 * Adapteur pour la liste de catégories
 *
 * @author J.Houdé
 */
public class CategoryListRecyclerViewAdapter extends BaseAdapter<CategoryListRecyclerViewAdapter.ViewHolder, Category> {

    /**
     * Adapteur pour la liste de catégories
     *
     * @param values          liste de données à afficher
     * @param contentResolver Résolveur de contenu
     * @param activity        activité gérant les données
     * @param fragment        fragment affichhant la liste
     */
    public CategoryListRecyclerViewAdapter(List<Category> values, ContentResolver contentResolver, AppCompatActivity activity, CategoryListFragment fragment) {
        super(values, contentResolver, activity, fragment);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(CategoryListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = values.get(position);

        // initialisation de la ligne avec les données correspondantes
        holder.content.setText(category.getName());
        holder.editButton.setOnClickListener(new ShowCategoryListener(category.getId(), activity));
        holder.deleteButton.setOnClickListener(new DeleteDatabaseListListener<>(category.getId(), activity, ((CategoriesActivity) activity).getDeleteContext(), this));
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    /**
     * Vue d'une catégorie dans la recyclerView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView content;
        public final Button editButton;
        public final Button deleteButton;

        public ViewHolder(CategoryListItemBinding binding) {
            super(binding.getRoot());

            content = binding.content;
            editButton = binding.editButton;
            deleteButton = binding.deleteButton;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + content.getText() + "'";
        }
    }
}
