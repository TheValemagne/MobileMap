package com.example.mobilemap.adapter;

import android.content.ContentResolver;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.PoisActivity;
import com.example.mobilemap.database.table.PoiDetail;
import com.example.mobilemap.databinding.PoiListItemBinding;
import com.example.mobilemap.listener.DeleteDatabaseListListener;

import java.util.List;

public class PoisListRecyclerViewAdapter extends BaseAdapter<PoisListRecyclerViewAdapter.ViewHolder, PoiDetail>{
    public PoisListRecyclerViewAdapter(List<PoiDetail> values, ContentResolver contentResolver, AppCompatActivity activity) {
        super(values, contentResolver, activity);
    }

    @NonNull
    @Override
    public PoisListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PoisListRecyclerViewAdapter.ViewHolder(PoiListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PoisListRecyclerViewAdapter.ViewHolder holder, int position) {
        PoiDetail poiDetail = values.get(position);

        holder.contentView.setText(poiDetail.getName());
        holder.categoryName.setText(poiDetail.getCategoryName());
        holder.deleteBtn.setOnClickListener(new DeleteDatabaseListListener<>(poiDetail.getId(), activity,
                ((PoisActivity) activity).getDeleteContext(), position, this));
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
