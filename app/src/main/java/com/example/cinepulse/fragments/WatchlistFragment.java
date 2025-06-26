package com.example.cinepulse.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.R;
import com.example.cinepulse.adapters.WatchlistAdapter;
import com.example.cinepulse.models.WatchlistItem;
import com.example.cinepulse.utils.WatchlistManager;

import java.util.ArrayList;
import java.util.List;

public class WatchlistFragment extends Fragment implements WatchlistAdapter.OnWatchlistChangeListener {

    private RecyclerView recyclerView;
    private WatchlistAdapter adapter;
    private List<WatchlistItem> watchlist = new ArrayList<>();
    private TextView textNoWatchlist;
    private Button btnClearAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watchlist, container, false);

        initializeViews(view);
        loadWatchlist();
        setupClearAllButton();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerWatchlist);
        textNoWatchlist = view.findViewById(R.id.textNoWatchlist);
        btnClearAll = view.findViewById(R.id.btnClearAll);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
    }

    private void loadWatchlist() {
        watchlist = WatchlistManager.getWatchlist(requireContext());
        adapter = new WatchlistAdapter((AppCompatActivity) requireContext(), watchlist, this);
        recyclerView.setAdapter(adapter);
        updateUI();
    }

    private void setupClearAllButton() {
        btnClearAll.setOnClickListener(v -> {
            if (!watchlist.isEmpty()) {
                WatchlistManager.clearWatchlist(requireContext());
                int size = watchlist.size();
                watchlist.clear();
                adapter.notifyItemRangeRemoved(0, size);
                updateUI();
                Toast.makeText(requireContext(), "Watchlist cleared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        boolean isEmpty = watchlist == null || watchlist.isEmpty();
        textNoWatchlist.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        btnClearAll.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onWatchlistChanged() {
        List<WatchlistItem> updatedWatchlist = WatchlistManager.getWatchlist(requireContext());
        if (updatedWatchlist != null) {
            watchlist.clear();
            watchlist.addAll(updatedWatchlist);
            adapter.notifyDataSetChanged();
            updateUI();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        List<WatchlistItem> updatedWatchlist = WatchlistManager.getWatchlist(requireContext());
        if (updatedWatchlist != null) {
            watchlist.clear();
            watchlist.addAll(updatedWatchlist);
            adapter.notifyDataSetChanged();
            updateUI();
        }
    }
}
