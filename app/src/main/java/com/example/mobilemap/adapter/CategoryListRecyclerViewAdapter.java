package com.example.mobilemap.adapter;

import android.content.ContentResolver;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.databinding.CategoryListItemBinding;
import com.example.mobilemap.listener.CategoryDeleteListener;

import java.util.List;

public class CategoryListRecyclerViewAdapter extends BaseAdapter<CategoryListRecyclerViewAdapter.ViewHolder, Category> {

    public CategoryListRecyclerViewAdapter(List<Category> values, ContentResolver contentResolver) {
        super(values, contentResolver);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(CategoryListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = values.get(position);

        holder.contentView.setText(category.getName());
        holder.deleteBtn.setOnClickListener(new CategoryDeleteListener(category.getId(), position, contentResolver, this));
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView contentView;
        public final Button editBtn;
        public final Button deleteBtn;

        public ViewHolder(CategoryListItemBinding binding) {
            super(binding.getRoot());
            contentView = binding.content;
            editBtn = binding.editBtn;
            deleteBtn = binding.deleteBtn;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + contentView.getText() + "'";
        }
    }
}
