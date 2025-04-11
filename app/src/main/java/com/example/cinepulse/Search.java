package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.adapters.MultiSearchAdapter;
import com.example.cinepulse.models.MediaItem;
import com.example.cinepulse.models.MultiSearchResponse;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity {

    private EditText editTextSearch;
    private RecyclerView recyclerSearchResults;
    private MultiSearchAdapter searchAdapter;
    private final String apiKey = "580b03ff6e8e1d2881e7ecf2dccaf4c3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editTextSearch = findViewById(R.id.editTextSearch);
        recyclerSearchResults = findViewById(R.id.recyclerSearchResults);
        recyclerSearchResults.setLayoutManager(new GridLayoutManager(this, 2));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(Search.this, Home.class));
                return true;
            } else if (itemId == R.id.nav_search) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(Search.this, Profile.class));
                return true;
            }

            return false;
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                if (query.length() > 2) {
                    searchMovies(query.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchMovies(String query) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<MultiSearchResponse> call = apiService.searchMulti(apiKey, query);

        call.enqueue(new Callback<MultiSearchResponse>() {
            @Override
            public void onResponse(Call<MultiSearchResponse> call, Response<MultiSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MediaItem> items = response.body().getResults();

                    List<MediaItem> filtered = new ArrayList<>();
                    for (MediaItem item : items) {
                        if ("movie".equals(item.getMediaType()) || "tv".equals(item.getMediaType())) {
                            filtered.add(item);
                        }
                    }

                    searchAdapter = new MultiSearchAdapter(Search.this, filtered);
                    recyclerSearchResults.setAdapter(searchAdapter);
                }
            }

            @Override
            public void onFailure(Call<MultiSearchResponse> call, Throwable t) {
                Log.e("Search", "API failure: " + t.getMessage());
            }
        });
    }
}
