package com.example.mobilemap.listener;

import android.content.ContentResolver;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.database.table.Category;
import com.example.mobilemap.fragment.CategoryFragment;

import java.text.MessageFormat;

public class SaveCategoryListener implements View.OnClickListener {
    private final AppCompatActivity activity;
    private final CategoryFragment fragment;
    private final ContentResolver contentResolver;

    public SaveCategoryListener(AppCompatActivity activity, CategoryFragment fragment, ContentResolver contentResolver) {
        this.activity = activity;
        this.fragment = fragment;
        this.contentResolver = contentResolver;
    }

    @Override
    public void onClick(View v) {
        if(!fragment.check()) {
            return;
        }

        Category category = fragment.getValues();

        if (category.getId() == -1) {
            contentResolver.insert(DatabaseContract.Category.CONTENT_URI, category.toContentValues());
        } else {
            contentResolver.update(DatabaseContract.Category.CONTENT_URI, category.toContentValues(),
                    MessageFormat.format("{0} = {1}", DatabaseContract.Category._ID, category.getId()), null);
        }

        activity.getSupportFragmentManager().popBackStackImmediate();
    }
}
