package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.ToggleTheme.BaseActivity;
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

public class Search extends BaseActivity {

    private EditText editTextSearch;
    private RecyclerView recyclerSearchResults;
    private MultiSearchAdapter searchAdapter;
    // Store API key securely, consider using a more secure approach like environment variables or backend authentication
    private final String apiKey = BuildConfig.TMDB_API_KEY; // API key from build config to avoid hardcoding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize UI elements
        editTextSearch = findViewById(R.id.editTextSearch);
        recyclerSearchResults = findViewById(R.id.recyclerSearchResults);

        // Setup RecyclerView
        recyclerSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerSearchResults.setHasFixedSize(true); // Improve RecyclerView performance

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
        bottomNavigationView.setOnItemSelectedListener(item -> handleNavigationItemSelected(item.getItemId()));

        // Implement debouncing for search text to optimize API calls
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
                    List<MediaItem> filteredItems = filterSearchResults(items);

                    searchAdapter = new MultiSearchAdapter(Search.this, filteredItems);
                    recyclerSearchResults.setAdapter(searchAdapter);
                } else {
                    // Handle the case where no results are found
                    Toast.makeText(Search.this, "No results found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MultiSearchResponse> call, Throwable t) {
                Log.e("Search", "API failure: " + t.getMessage());
                Toast.makeText(Search.this, "Failed to fetch search results. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<MediaItem> filterSearchResults(List<MediaItem> items) {
        List<MediaItem> filtered = new ArrayList<>();
        for (MediaItem item : items) {
            // Filter out non-movie or non-TV items
            if ("movie".equals(item.getMediaType()) || "tv".equals(item.getMediaType())) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private boolean handleNavigationItemSelected(int itemId) {
        // Replace switch with if-else for better clarity and performance
        if (itemId == R.id.nav_home) {
            startActivity(new Intent(Search.this, Home.class));
            return true;
        } else if (itemId == R.id.nav_search) {
            return true; // Current activity
        } else if (itemId == R.id.nav_watchlist) {
            startActivity(new Intent(Search.this, WatchlistActivity.class));
            return true;
        } else if (itemId == R.id.nav_populartrailer) {
            startActivity(new Intent(Search.this, PopularTrailerActivity.class));
            return true;
        } else if (itemId == R.id.nav_profile) {
            startActivity(new Intent(Search.this, Profile.class));
            return true;
        }
        return false;
    }
}
