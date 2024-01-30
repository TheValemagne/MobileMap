package com.example.mobilemap.adapter;

import android.content.ContentResolver;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.database.table.DatabaseItem;

import java.util.List;

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder, U extends DatabaseItem> extends RecyclerView.Adapter<T>  {
    protected final List<U> values;
    protected final ContentResolver contentResolver;

    public BaseAdapter(List<U> values, ContentResolver contentResolver) {
        this.values = values;
        this.contentResolver = contentResolver;
    }

    public void updateList(List<U> items) {
        this.values.clear();
        this.values.addAll(items);
        this.notifyItemRangeChanged(0, items.size());
    }

    public void removeItem(int position) {
        values.remove(position);
        this.notifyItemRemoved(position);
    }
}
