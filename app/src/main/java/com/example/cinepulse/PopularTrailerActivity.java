package com.example.cinepulse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.ToggleTheme.BaseActivity;
import com.example.cinepulse.adapters.TrailerAdapter;
import com.example.cinepulse.models.Movie;
import com.example.cinepulse.models.MovieResponse;
import com.example.cinepulse.models.Trailer;
import com.example.cinepulse.models.TrailerResponse;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularTrailerActivity extends BaseActivity {

    private static final String TAG = "PopularTrailerActivity";
    private TrailerAdapter trailerAdapter;
    private final List<Trailer> popularTrailers = new ArrayList<>();
    private boolean hasShownDialog = false; // Ensures dialog is shown once per launch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_populartraileractivity);

        initializeViews();
        setupBottomNavigation();
        showSpoilerWarningDialog(); // Show dialog before fetching trailers
    }

    private void initializeViews() {
        RecyclerView recyclerTrailers = findViewById(R.id.recyclerTrailers);
        recyclerTrailers.setLayoutManager(new LinearLayoutManager(this));
        trailerAdapter = new TrailerAdapter(this, popularTrailers, this::onTrailerClicked);
        recyclerTrailers.setAdapter(trailerAdapter);
    }

    private void showSpoilerWarningDialog() {
        if (!hasShownDialog) {
            hasShownDialog = true;

            new AlertDialog.Builder(this)
                    .setTitle("Spoiler Warning")
                    .setMessage("Trailers may contain spoilers. Do you want to continue?")
                    .setCancelable(false)
                    .setPositiveButton("Yes, show me", (dialog, which) -> fetchPopularTrailers())
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                        finish(); // Close the activity if the user cancels
                    })
                    .show();
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_populartrailer);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, Home.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, Search.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_watchlist) {
                startActivity(new Intent(this, WatchlistActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_populartrailer) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, Profile.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void onTrailerClicked(Trailer trailer) {
        try {
            String youtubeUrl = "https://www.youtube.com/watch?v=" + trailer.getKey();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.google.android.youtube");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                intent.setPackage(null); // Use the default browser if YouTube is not installed
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error opening trailer", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error opening trailer", e);
        }
    }

    private void fetchPopularTrailers() {
        TMDbApiService apiService = RetroFitClient.getApiService();

        Log.d(TAG, "Fetching popular movies...");
        apiService.getPopularMovies(BuildConfig.TMDB_API_KEY)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            Log.d(TAG, "Got " + movies.size() + " popular movies");

                            if (!movies.isEmpty()) {
                                // Fetch trailers for the top 5 movies
                                fetchTrailersForMovies(movies.subList(0, Math.min(5, movies.size())));
                            } else {
                                showEmptyState();
                            }
                        } else {
                            handleApiError(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        handleNetworkError(t);
                    }
                });
    }

    private void fetchTrailersForMovies(List<Movie> movies) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        popularTrailers.clear();

        for (Movie movie : movies) {
            apiService.getMovieTrailers(movie.getId(), BuildConfig.TMDB_API_KEY)
                    .enqueue(new Callback<>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Trailer> trailers = response.body().getResults();
                                if (trailers != null && !trailers.isEmpty()) {
                                    popularTrailers.addAll(trailers);
                                    trailerAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {
                            Log.e(TAG, "Failed to fetch trailers for movie: " + movie.getId(), t);
                        }
                    });
        }
    }

    private void handleApiError(int errorCode) {
        String errorMessage = "API Error: " + errorCode;
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        Log.e(TAG, errorMessage);
    }

    private void handleNetworkError(Throwable t) {
        Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Network Error", t);
    }

    private void showEmptyState() {
        Toast.makeText(this, "No movies found", Toast.LENGTH_SHORT).show();
    }
}
