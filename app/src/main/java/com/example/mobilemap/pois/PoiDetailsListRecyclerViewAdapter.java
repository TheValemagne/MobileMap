package com.example.mobilemap.pois;

import android.content.ContentResolver;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemap.adapters.BaseAdapter;
import com.example.mobilemap.database.tables.PoiDetail;
import com.example.mobilemap.databinding.PoiListItemBinding;
import com.example.mobilemap.listeners.DeleteDatabaseListListener;
import com.example.mobilemap.pois.fragments.PoiListFragment;
import com.example.mobilemap.pois.listeners.ShowPoiListener;

import java.util.List;

/**
 * Adapteur pour la liste de sites détaillés
 *
 * @author J.Houdé
 */
public class PoiDetailsListRecyclerViewAdapter extends BaseAdapter<PoiDetailsListRecyclerViewAdapter.ViewHolder, PoiDetail> {
    /**
     * Adapteur pour la liste de sites détaillés
     *
     * @param values          liste initiale de données
     * @param contentResolver Résolveur de contenu
     * @param activity        activité gérant les données
     * @param fragment        fragment gérant l'affichage de la liste
     */
    public PoiDetailsListRecyclerViewAdapter(List<PoiDetail> values, ContentResolver contentResolver,
                                             AppCompatActivity activity, PoiListFragment fragment) {
        super(values, contentResolver, activity, fragment);
    }

    @NonNull
    @Override
    public PoiDetailsListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PoiDetailsListRecyclerViewAdapter.ViewHolder(PoiListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PoiDetailsListRecyclerViewAdapter.ViewHolder holder, int position) {
        PoiDetail poiDetail = values.get(position);

        // initialisation de la ligne avec les données correspondantes
        holder.content.setText(poiDetail.getSiteName());
        holder.categoryName.setText(poiDetail.getCategoryName());
        holder.editButton.setOnClickListener(new ShowPoiListener(poiDetail.getId(), activity));
        holder.deleteButton.setOnClickListener(new DeleteDatabaseListListener<>(poiDetail.getId(), activity,
                ((PoisActivity) activity).getDeleteContext(), this));
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    /**
     * Vue d'un site dans la recyclerView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView content;
        public final TextView categoryName;
        public final Button editButton;
        public final Button deleteButton;

        public ViewHolder(PoiListItemBinding binding) {
            super(binding.getRoot());
            content = binding.content;
            categoryName = binding.categoryName;
            editButton = binding.editButton;
            deleteButton = binding.deleteButton;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + content.getText() + "'";
        }
    }
}
