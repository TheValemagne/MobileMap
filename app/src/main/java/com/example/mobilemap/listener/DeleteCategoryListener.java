package com.example.mobilemap.listener;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemap.R;
import com.example.mobilemap.database.DatabaseContract;
import com.example.mobilemap.databinding.DialogConfirmDeleteBinding;

import java.text.MessageFormat;

public class DeleteCategoryListener implements View.OnClickListener{
    private final long itemId;
    private final AppCompatActivity activity;
    private final ContentResolver contentResolver;

    public DeleteCategoryListener(long itemId, AppCompatActivity activity) {
        this.itemId = itemId;
        this.activity = activity;
        this.contentResolver = activity.getContentResolver();
    }

    @Override
    public void onClick(View v) {
        // affichage du dialogue de confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        Resources resources = activity.getResources();

        builder.setTitle(resources.getString(R.string.dialog_delete_category_title));

        DialogConfirmDeleteBinding binding = DialogConfirmDeleteBinding.inflate(activity.getLayoutInflater());
        binding.dialogMsg.setText(resources.getText(R.string.confirm_delete_category_msg));
        builder.setView(binding.getRoot());

        builder.setPositiveButton(resources.getText(R.string.dialog_confirm), (dialog, which) -> {
            deleteItem();
            afterItemDeleted();
        });
        builder.setNegativeButton(resources.getText(R.string.dialog_cancel), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteItem() {
        contentResolver
                .delete(DatabaseContract.Category.CONTENT_URI,
                        MessageFormat.format("{0} = {1}", DatabaseContract.Category._ID, itemId), null);
    }

    protected void afterItemDeleted() {
        activity.getSupportFragmentManager().popBackStackImmediate();
    }
}
