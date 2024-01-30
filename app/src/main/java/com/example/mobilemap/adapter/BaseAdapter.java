package com.example.mobilemap.adapter;

import android.content.ContentResolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.database.table.DatabaseItem;

import java.util.List;

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder, U extends DatabaseItem> extends RecyclerView.Adapter<T>  {
    protected final List<U> values;
    protected final ContentResolver contentResolver;
    protected final AppCompatActivity activity;

    public BaseAdapter(List<U> values, ContentResolver contentResolver, AppCompatActivity activity) {
        this.values = values;
        this.contentResolver = contentResolver;
        this.activity = activity;
    }

    public void removeItem(int position) {
        values.remove(position);
        this.notifyItemRemoved(position);
    }
}
