package com.example.mobilemap.adapters;

import android.content.ContentResolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.database.interfaces.HasId;

import java.util.List;

/**
 * Classe abstraite d'une liste
 *
 * @param <T> le type du support de vue
 * @param <U> type de l'élément à afficher
 */
public abstract class BaseAdapter<T extends RecyclerView.ViewHolder, U extends HasId> extends RecyclerView.Adapter<T> {
    public List<U> getValues() {
        return values;
    }

    protected final List<U> values;
    protected final ContentResolver contentResolver;
    protected final AppCompatActivity activity;
    protected final FragmentListView fragmentListView;

    /**
     * @param values           liste initiale de la liste
     * @param contentResolver  gestionnaire de la base de données
     * @param activity         activité à l'origine du fragment
     * @param fragmentListView framgent gérant la liste
     */
    public BaseAdapter(List<U> values, ContentResolver contentResolver, AppCompatActivity activity, FragmentListView fragmentListView) {
        super();

        this.values = values;
        this.contentResolver = contentResolver;
        this.activity = activity;
        this.fragmentListView = fragmentListView;
    }

    /**
     * Supprimer l'élément de la liste à la position indiquée
     *
     * @param position position de l'élément à supprimer
     */
    public void removeItem(int position) {
        values.remove(position);
        this.notifyItemRemoved(position);
        this.fragmentListView.updateView();
    }
}
