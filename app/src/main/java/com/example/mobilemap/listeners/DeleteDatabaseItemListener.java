package com.example.mobilemap.listeners;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.provider.BaseColumns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemap.database.DeleteItemContext;
import com.example.mobilemap.R;
import com.example.mobilemap.databinding.DialogConfirmDeleteBinding;

import java.text.MessageFormat;

public class DeleteDatabaseItemListener implements View.OnClickListener{
    protected long getItemId() {
        return itemId;
    }

    private final long itemId;
    private final AppCompatActivity activity;
    private final ContentResolver contentResolver;
    private final DeleteItemContext deleteDataContext;

    /**
     * Ecouteur pour supprimer un élément de la base de données
     *
     * @param itemId identifiant de l'élément à supprimer
     * @param activity activité mère
     * @param deleteCategoryContext contexte pour la supression de l'élément
     */
    public DeleteDatabaseItemListener(long itemId, AppCompatActivity activity, DeleteItemContext deleteCategoryContext) {
        this.itemId = itemId;
        this.activity = activity;
        this.contentResolver = activity.getContentResolver();

        this.deleteDataContext = deleteCategoryContext;
    }

    @Override
    public void onClick(View v) {
        // affichage du dialogue de confirmation
        Resources resources = activity.getResources();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(deleteDataContext.getDialogTitle());

        DialogConfirmDeleteBinding binding = DialogConfirmDeleteBinding.inflate(activity.getLayoutInflater());
        binding.dialogMsg.setText(deleteDataContext.getDialogMsg());
        builder.setView(binding.getRoot());

        builder.setPositiveButton(resources.getText(R.string.dialog_confirm), (dialog, which) -> {
            deleteItem();
            afterItemDeleted();
        });
        builder.setNegativeButton(resources.getText(R.string.dialog_cancel), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Exécute la requête de supression
     */
    private void deleteItem() {
        contentResolver
                .delete(deleteDataContext.getDatabaseUri(),
                        MessageFormat.format("{0} = {1}", BaseColumns._ID, itemId), null);
    }

    /**
     * Action après suppression de l'élément dans la base de données
     */
    protected void afterItemDeleted() {
        activity.getSupportFragmentManager().popBackStackImmediate();
    }
}
