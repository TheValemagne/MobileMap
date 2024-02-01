package com.example.mobilemap.listener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.DeleteItemContext;
import com.example.mobilemap.adapter.BaseAdapter;

public class DeleteDatabaseListListener<T extends RecyclerView.ViewHolder, U> extends DeleteDatabaseItemListener {
    private final int position;
    private final BaseAdapter<T, U> adapter;

    public DeleteDatabaseListListener(long itemId, AppCompatActivity activity, DeleteItemContext deleteCategoryContext,
                                      int position, BaseAdapter<T, U> adapter) {
        super(itemId, activity, deleteCategoryContext);
        this.position = position;
        this.adapter = adapter;
    }

    @Override
    protected void afterItemDeleted() {
        adapter.removeItem(position);
    }
}
