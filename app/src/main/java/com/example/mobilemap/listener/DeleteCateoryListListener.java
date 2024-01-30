package com.example.mobilemap.listener;

import android.content.ContentResolver;
import android.view.View;

import com.example.mobilemap.adapter.CategoryListRecyclerViewAdapter;

public class DeleteCateoryListListener extends DeleteCategoryListener{
    private final int position;
    private final CategoryListRecyclerViewAdapter adapter;

    public DeleteCateoryListListener(long itemId, ContentResolver contentResolver, int position, CategoryListRecyclerViewAdapter adapter) {
        super(itemId, contentResolver);
        this.position = position;
        this.adapter = adapter;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        adapter.removeItem(position);
    }
}
