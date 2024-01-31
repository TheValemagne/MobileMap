package com.example.mobilemap.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilemap.PoisActivity;
import com.example.mobilemap.adapter.PoisListRecyclerViewAdapter;
import com.example.mobilemap.database.PoiDetail;
import com.example.mobilemap.databinding.FragmentPoiListBinding;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PoiListFragment extends Fragment {

    public PoiListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentPoiListBinding binding = FragmentPoiListBinding.inflate(inflater, container, false);

        PoisActivity activity = (PoisActivity) requireActivity();

        initRecyclerView(binding, activity);

        return binding.getRoot();
    }

    private void initRecyclerView(FragmentPoiListBinding binding, PoisActivity activity) {
        List<PoiDetail> poiDetails = activity.getPois();

        if (!poiDetails.isEmpty()) {
            binding.emptyLabel.setVisibility(View.INVISIBLE);
        }

        RecyclerView recyclerView = binding.poiList;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        PoisListRecyclerViewAdapter adapter = new PoisListRecyclerViewAdapter(poiDetails, activity.getContentResolver(), activity);
        recyclerView.setAdapter(adapter);
    }
}