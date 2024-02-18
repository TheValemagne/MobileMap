package com.example.mobilemap.pois.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilemap.adapters.FragmentListView;
import com.example.mobilemap.R;
import com.example.mobilemap.pois.PoiDetailsListRecyclerViewAdapter;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.tables.PoiDetail;
import com.example.mobilemap.databinding.FragmentPoiListBinding;
import com.example.mobilemap.pois.listeners.ShowPoiListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author J.Houdé
 */
public class PoiListFragment extends Fragment implements FragmentListView {
    private FragmentPoiListBinding binding;

    public PoiListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPoiListBinding.inflate(inflater, container, false);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        initRecyclerView(activity);
        binding.addPoiButton.setOnClickListener(new ShowPoiListener(activity));

        return binding.getRoot();
    }

    private boolean shouldEnableList() {
        return !ContentResolverHelper.getCategories(requireActivity().getContentResolver()).isEmpty();
    }

    private void initRecyclerView(AppCompatActivity activity) {
        updateView();

        RecyclerView recyclerView = binding.poiList;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        PoiDetailsListRecyclerViewAdapter adapter = new PoiDetailsListRecyclerViewAdapter(ContentResolverHelper.getPoisDetail(activity.getContentResolver()),
                activity.getContentResolver(), activity, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void updateView() {
        boolean shouldEnableList = shouldEnableList();
        List<PoiDetail> poiDetails = ContentResolverHelper.getPoisDetail(requireActivity().getContentResolver());

        int informationMsgId = shouldEnableList ? R.string.empty_list : R.string.require_category;
        binding.informationLabel.setText(requireActivity().getResources().getText(informationMsgId));

        binding.informationLabel.setVisibility(poiDetails.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        binding.addPoiButton.setVisibility(shouldEnableList ? View.VISIBLE : View.INVISIBLE);
    }
}