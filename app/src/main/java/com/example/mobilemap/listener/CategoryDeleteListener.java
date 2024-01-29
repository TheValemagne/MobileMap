package com.example.mobilemap.listener;

import android.content.ContentResolver;
import android.view.View;

import com.example.mobilemap.adapter.CategoryListRecyclerViewAdapter;
import com.example.mobilemap.database.DatabaseContract;

public class CategoryDeleteListener implements View.OnClickListener{
    private final long itemId;
    private final int position;
    private final ContentResolver contentResolver;
    private final CategoryListRecyclerViewAdapter adapter;

    public CategoryDeleteListener(long itemId, int position, ContentResolver contentResolver, CategoryListRecyclerViewAdapter adapter) {
        this.itemId = itemId;
        this.position = position;
        this.contentResolver = contentResolver;
        this.adapter = adapter;
    }


    @Override
    public void onClick(View v) {
        contentResolver
                .delete(DatabaseContract.Category.CONTENT_URI, DatabaseContract.Category._ID + " =" + itemId, null);

        adapter.removeItem(position);
    }
}
