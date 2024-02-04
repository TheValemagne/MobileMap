package com.example.mobilemap.listeners;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.database.DeleteItemContext;
import com.example.mobilemap.adapters.BaseAdapter;
import com.example.mobilemap.database.interfaces.HasId;

import java.util.List;

public class DeleteDatabaseListListener<T extends RecyclerView.ViewHolder, U extends HasId> extends DeleteDatabaseItemListener {
    private final BaseAdapter<T, U> adapter;

    public DeleteDatabaseListListener(long itemId, AppCompatActivity activity, DeleteItemContext deleteCategoryContext,
                                       BaseAdapter<T, U> adapter) {
        super(itemId, activity, deleteCategoryContext);
        this.adapter = adapter;
    }

    @Override
    protected void afterItemDeleted() {
        int position = getItemPosition(getItemId());

        if (position != -1) {
            adapter.removeItem(position);
        }
    }

    private int getItemPosition(long itemId) {
        List<U> items = adapter.getValues();

        for (int index = 0; index < items.size(); index++) {
            if(items.get(index).getId() == itemId) {
                return index;
            }
        }

        return -1;
    }
}
