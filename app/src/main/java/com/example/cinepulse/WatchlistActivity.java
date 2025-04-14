package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.adapters.WatchlistAdapter;
import com.example.cinepulse.models.WatchlistItem;
import com.example.cinepulse.utils.WatchlistManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class WatchlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WatchlistAdapter adapter;
    private List<WatchlistItem> watchlist;
    private TextView textNoWatchlist;
    private Button btnClearAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        recyclerView = findViewById(R.id.recyclerWatchlist);
        textNoWatchlist = findViewById(R.id.textNoWatchlist);
        btnClearAll = findViewById(R.id.btnClearAll);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        watchlist = WatchlistManager.getWatchlist(this);

        adapter = new WatchlistAdapter(this, watchlist);
        recyclerView.setAdapter(adapter);

        updateUI();

        setupBottomNavigation();
        setupClearAllButton();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_watchlist);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, Home.class));
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, Search.class));
                return true;
            } else if (itemId == R.id.nav_watchlist) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, Profile.class));
                return true;
            }
            return false;
        });
    }

    private void setupClearAllButton() {
        btnClearAll.setOnClickListener(v -> {
            WatchlistManager.clearWatchlist(this);
            watchlist.clear();
            adapter.notifyDataSetChanged();
            updateUI();
        });
    }

    private void updateUI() {
        if (watchlist == null || watchlist.isEmpty()) {
            textNoWatchlist.setVisibility(View.VISIBLE);
            btnClearAll.setVisibility(View.GONE);
        } else {
            textNoWatchlist.setVisibility(View.GONE);
            btnClearAll.setVisibility(View.VISIBLE);
        }
    }
}
