package com.example.mobilemap.adapter;

import android.content.ContentResolver;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.database.table.Poi;
import com.example.mobilemap.databinding.PoiListItemBinding;

import java.util.List;

public class PoisListRecyclerViewAdapter extends BaseAdapter<PoisListRecyclerViewAdapter.ViewHolder, Poi>{
    public PoisListRecyclerViewAdapter(List<Poi> values, ContentResolver contentResolver) {
        super(values, contentResolver);
    }

    @NonNull
    @Override
    public PoisListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PoisListRecyclerViewAdapter.ViewHolder(PoiListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PoisListRecyclerViewAdapter.ViewHolder holder, int position) {
        Poi poi = values.get(position);

        holder.contentView.setText(poi.getName());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView contentView;
        public final TextView categoryName;
        public final Button editBtn;
        public final Button deleteBtn;

        public ViewHolder(PoiListItemBinding binding) {
            super(binding.getRoot());
            contentView = binding.content;
            categoryName = binding.categoryName;
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
