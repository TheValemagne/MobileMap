package com.example.mobilemap.listener;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemap.database.table.DatabaseItem;
import com.example.mobilemap.fragment.ItemView;

import java.text.MessageFormat;

public class SaveDatabaseItemListener<T extends DatabaseItem> implements View.OnClickListener {
    private final AppCompatActivity activity;
    private final ItemView<T> fragment;
    private final ContentResolver contentResolver;
    private final Uri databaseUri;

    public SaveDatabaseItemListener(AppCompatActivity activity, ItemView<T> fragment, Uri databaseUri) {
        this.activity = activity;
        this.fragment = fragment;
        this.contentResolver = activity.getContentResolver();
        this.databaseUri = databaseUri;
    }

    @Override
    public void onClick(View v) {
        if(!fragment.check()) {
            return;
        }

        T databaseItem = fragment.getValues();

        if (databaseItem.getId() == -1) {
            contentResolver.insert(databaseUri, databaseItem.toContentValues());
        } else {
            contentResolver.update(databaseUri, databaseItem.toContentValues(),
                    MessageFormat.format("{0} = {1}", BaseColumns._ID, databaseItem.getId()), null);
        }

        activity.getSupportFragmentManager().popBackStackImmediate();
    }
}
