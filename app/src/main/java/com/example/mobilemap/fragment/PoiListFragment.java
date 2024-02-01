package com.example.mobilemap.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobilemap.R;
import com.example.mobilemap.adapter.PoisListRecyclerViewAdapter;
import com.example.mobilemap.database.ContentResolverHelper;
import com.example.mobilemap.database.table.PoiDetail;
import com.example.mobilemap.databinding.FragmentPoiListBinding;
import com.example.mobilemap.listener.ShowPoiListener;

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

        initRecyclerView(binding, (AppCompatActivity) requireActivity());
        binding.addPoiButton.setOnClickListener(new ShowPoiListener((AppCompatActivity) requireActivity()));

        return binding.getRoot();
    }

    private boolean shouldEnableList() {
        return !ContentResolverHelper.getCategories(requireActivity().getContentResolver()).isEmpty();
    }

    private void initRecyclerView(FragmentPoiListBinding binding, AppCompatActivity activity) {
        List<PoiDetail> poiDetails = ContentResolverHelper.getPoisDetail(activity.getContentResolver());

        if (!poiDetails.isEmpty()) {
            binding.informationLabel.setVisibility(View.INVISIBLE);
        }

        int informationMsgId = shouldEnableList() ? R.string.empty_list : R.string.require_category;
        binding.informationLabel.setText(requireActivity().getResources().getText(informationMsgId));
        binding.addPoiButton.setVisibility(shouldEnableList() ? View.VISIBLE : View.INVISIBLE);

        RecyclerView recyclerView = binding.poiList;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        PoisListRecyclerViewAdapter adapter = new PoisListRecyclerViewAdapter(poiDetails, activity.getContentResolver(), activity);
        recyclerView.setAdapter(adapter);
    }
}