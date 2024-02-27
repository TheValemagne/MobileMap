package com.example.mobilemap.listeners;

import android.app.Activity;
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

/**
 * Ecouteur pour supprimer un élément de la base de données
 *
 * @author J.Houdé
 */
public class DeleteDatabaseItemListener implements View.OnClickListener {
    protected long getItemId() {
        return itemId;
    }

    private final long itemId;
    private final AppCompatActivity activity;
    private final ContentResolver contentResolver;
    private final DeleteItemContext deleteDataContext;
    private final boolean launchedForResult;

    /**
     * Ecouteur pour supprimer un élément de la base de données
     *
     * @param itemId                identifiant de l'élément à supprimer
     * @param activity              activité mère
     * @param deleteCategoryContext contexte pour la supression de l'élément
     * @param launchedForResult     si l'activité mère a été lancé par une autre activité pour une tâche
     */
    public DeleteDatabaseItemListener(long itemId, AppCompatActivity activity, DeleteItemContext deleteCategoryContext, boolean launchedForResult) {
        this.itemId = itemId;
        this.activity = activity;
        this.contentResolver = activity.getContentResolver();

        this.deleteDataContext = deleteCategoryContext;
        this.launchedForResult = launchedForResult;
    }

    /**
     * Ecouteur pour supprimer un élément de la base de données
     *
     * @param itemId                identifiant de l'élément à supprimer
     * @param activity              activité mère
     * @param deleteCategoryContext contexte pour la supression de l'élément
     */
    public DeleteDatabaseItemListener(long itemId, AppCompatActivity activity, DeleteItemContext deleteCategoryContext) {
        this(itemId, activity, deleteCategoryContext, false);
    }

    @Override
    public void onClick(View v) {
        Resources resources = activity.getResources();

        // création du dialogue de confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(deleteDataContext.getDialogTitle());

        DialogConfirmDeleteBinding binding = DialogConfirmDeleteBinding.inflate(activity.getLayoutInflater());
        binding.dialogMsg.setText(deleteDataContext.getDialogMsg());
        builder.setView(binding.getRoot());

        builder.setPositiveButton(resources.getText(R.string.dialog_confirm), (dialog, which) -> deleteItem());
        builder.setNegativeButton(resources.getText(R.string.dialog_cancel), (dialog, which) -> dialog.cancel());

        builder.show(); // affichage du dialogue de confirmation
    }

    /**
     * Exécute la requête de suppression
     */
    private void deleteItem() {
        contentResolver.delete(deleteDataContext.getDatabaseUri(),
                MessageFormat.format("{0} = {1}", BaseColumns._ID, itemId), null);

        afterDeleteItem();
    }

    /**
     * Action après suppression de l'élément dans la base de données
     */
    protected void afterDeleteItem() {
        if (launchedForResult) { // activité lancée pour un résultat
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
            return;
        }

        activity.getSupportFragmentManager().popBackStackImmediate();
    }
}
