package com.example.mobilemap.adapters;

import android.content.ContentResolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.database.interfaces.HasId;

import java.util.List;

/**
 * Classe abstraite d'un adaptater de liste recycable
 *
 * @param <T> le type du support de vue pour une ligne de la liste
 * @param <U> type de l'élément à afficher
 * @author J.Houdé
 */
public abstract class BaseAdapter<T extends RecyclerView.ViewHolder, U extends HasId> extends RecyclerView.Adapter<T> {
    /**
     * Récupération de la liste des éléments stockés
     *
     * @return liste d'éléments de la liste
     */
    public List<U> getValues() {
        return values;
    }

    protected final List<U> values;
    protected final ContentResolver contentResolver;
    protected final AppCompatActivity activity;
    protected final FragmentListView fragmentListView;

    /**
     * @param values           liste initiale de données
     * @param contentResolver  gestionnaire de la base de données
     * @param activity         activité gérant les données
     * @param fragmentListView fragment gérant l'affichage de la liste
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
     * @param position position de l'élément à supprimer de la liste
     */
    public void removeItem(int position) {
        values.remove(position); // suppression de l'élément de la liste

        this.notifyItemRemoved(position); // notification de la suppression à la vue grapphique de la liste
        this.fragmentListView.updateView(); // mise à jour des textes et boutons d'actions en lien avec la liste
    }
}
