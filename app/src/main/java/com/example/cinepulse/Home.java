package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.ToggleTheme.BaseActivity;
import com.example.cinepulse.adapters.MovieAdapter;
import com.example.cinepulse.models.Movie;
import com.example.cinepulse.models.MovieResponse;
import com.example.cinepulse.models.TvShow;
import com.example.cinepulse.models.TvShowResponse;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.cinepulse.adapters.TvShowAdapter;

public class Home extends BaseActivity {

    private RecyclerView recyclerTrendingMovies, recyclerTrendingTV;
    private MovieAdapter movieAdapter;
    private TvShowAdapter tvShowAdapter;

    // API Key should be loaded securely (not hardcoded)
    private final String apiKey = BuildConfig.TMDB_API_KEY; // Use a build config to securely store the API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Find RecyclerViews from layout
        recyclerTrendingMovies = findViewById(R.id.recyclerTrendingMovies);
        recyclerTrendingTV = findViewById(R.id.recyclerTrendingTV);

        // Set layout managers for horizontal scrolling
        recyclerTrendingMovies.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerTrendingTV.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        // Fetch trending movies and TV shows
        fetchTrendingMovies();
        fetchTrendingTVShows();

        // Setup Bottom Navigation and Movie List button
        setupBottomNavigation();
        setupMovieListButton();
    }

    // Setup button to navigate to Genre List Activity
    private void setupMovieListButton() {
        Button btnMovieList = findViewById(R.id.btnMovieList);
        btnMovieList.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, GenreListActivity.class);
            startActivity(intent);
        });
    }

    // Setup Bottom Navigation for different screens
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Handle different bottom navigation items with if-else
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(Home.this, Search.class));
                return true;
            } else if (itemId == R.id.nav_watchlist) {
                startActivity(new Intent(Home.this, WatchlistActivity.class));
                return true;
            } else if (itemId == R.id.nav_populartrailer) {
                startActivity(new Intent(Home.this, PopularTrailerActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(Home.this, Profile.class));
                return true;
            }

            return false;
        });
    }

        // Fetch trending movies
    private void fetchTrendingMovies() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<MovieResponse> call = apiService.getTrendingMovies(apiKey);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                // Handle successful response
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    Log.d("API", "Fetched " + movies.size() + " movies.");

                    // Update RecyclerView with movies
                    movieAdapter = new MovieAdapter(Home.this, new ArrayList<>(movies));
                    recyclerTrendingMovies.setAdapter(movieAdapter);
                } else {
                    Log.e("API", "Movie Error: " + response.code());
                    handleApiError(response.code()); // Handle errors more gracefully
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                // Log failure and notify user
                Log.e("API", "Movie Failure: " + t.getMessage());
                Toast.makeText(Home.this, "Failed to load movies. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch trending TV shows
    private void fetchTrendingTVShows() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TvShowResponse> call = apiService.getTrendingTVShows(apiKey);

        call.enqueue(new Callback<TvShowResponse>() {
            @Override
            public void onResponse(@NonNull Call<TvShowResponse> call, @NonNull Response<TvShowResponse> response) {
                // Handle successful response
                if (response.isSuccessful() && response.body() != null) {
                    List<TvShow> shows = response.body().getResults();
                    Log.d("API", "Fetched " + shows.size() + " TV shows.");

                    // Update RecyclerView with TV shows
                    tvShowAdapter = new TvShowAdapter(Home.this, shows);
                    recyclerTrendingTV.setAdapter(tvShowAdapter);
                } else {
                    Log.e("API", "TV Error: " + response.code());
                    handleApiError(response.code()); // Handle errors more gracefully
                }
            }

            @Override
            public void onFailure(@NonNull Call<TvShowResponse> call, @NonNull Throwable t) {
                // Log failure and notify user
                Log.e("API", "TV Failure: " + t.getMessage());
                Toast.makeText(Home.this, "Failed to load TV shows. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle API error by providing appropriate feedback to users
    private void handleApiError(int errorCode) {
        String message = "An error occurred while fetching data.";
        switch (errorCode) {
            case 404:
                message = "Data not found.";
                break;
            case 500:
                message = "Server error. Please try again later.";
                break;
            default:
                message = "Unknown error occurred.";
        }
        Toast.makeText(Home.this, message, Toast.LENGTH_SHORT).show();
    }
}
