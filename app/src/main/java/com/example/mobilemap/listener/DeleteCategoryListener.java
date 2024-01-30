package com.example.mobilemap.listener;

import android.content.ContentResolver;
import android.view.View;

import com.example.mobilemap.database.DatabaseContract;

public class DeleteCategoryListener implements View.OnClickListener{
    private final long itemId;
    private final ContentResolver contentResolver;

    public DeleteCategoryListener(long itemId, ContentResolver contentResolver) {
        this.itemId = itemId;
        this.contentResolver = contentResolver;
    }


    @Override
    public void onClick(View v) {
        contentResolver
                .delete(DatabaseContract.Category.CONTENT_URI, DatabaseContract.Category._ID + " =" + itemId, null);
    }
}
