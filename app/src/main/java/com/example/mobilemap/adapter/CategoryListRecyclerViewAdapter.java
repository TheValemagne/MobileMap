package com.example.mobilemap.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.databinding.CategoryListItemBinding;

import java.util.List;

public class CategoryListRecyclerViewAdapter extends RecyclerView.Adapter<CategoryListRecyclerViewAdapter.ViewHolder> {

    private final List<Category> values;

    public CategoryListRecyclerViewAdapter(List<Category> values) {
        this.values = values;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(CategoryListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void updateList(List<Category> items) {
        this.values.clear();
        this.values.addAll(items);
        this.notifyItemRangeChanged(0, items.size());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.contentView.setText(values.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView contentView;

        public ViewHolder(CategoryListItemBinding binding) {
            super(binding.getRoot());
            contentView = binding.content;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + contentView.getText() + "'";
        }
    }
}
