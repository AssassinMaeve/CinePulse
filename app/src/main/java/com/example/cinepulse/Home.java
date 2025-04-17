package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Home extends AppCompatActivity {
    private RecyclerView recyclerTrendingMovies, recyclerTrendingTV;
    private MovieAdapter movieAdapter;
    private TvShowAdapter tvShowAdapter; // Add this line

    private final String apiKey = "580b03ff6e8e1d2881e7ecf2dccaf4c3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        recyclerTrendingMovies = findViewById(R.id.recyclerTrendingMovies);
        recyclerTrendingTV = findViewById(R.id.recyclerTrendingTV);

        // Set layout managers
        recyclerTrendingMovies.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerTrendingTV.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        fetchTrendingMovies();
        fetchTrendingTVShows();

        setupBottomNavigation();
        setupMovieListButton();
    }

    private void setupMovieListButton() {
        Button btnMovieList = findViewById(R.id.btnMovieList);
        btnMovieList.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, GenreListActivity.class);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(Home.this, Search.class));
                return true;
            } else if (itemId == R.id.nav_watchlist) {  // ✅ Add this
                startActivity(new Intent(Home.this, WatchlistActivity.class));
                return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(Home.this, ScheduleActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(Home.this, Profile.class));
                return true;
            }
            return false;
        });
    }


    private void fetchTrendingMovies() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<MovieResponse> call = apiService.getTrendingMovies(apiKey);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    Log.d("API", "Fetched " + movies.size() + " movies.");

                    movieAdapter = new MovieAdapter(Home.this, new ArrayList<>(movies), "movie");
                    recyclerTrendingMovies.setAdapter(movieAdapter);
                } else {
                    Log.e("API", "Movie Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("API", "Movie Failure: " + t.getMessage());
            }
        });
    }

    private void fetchTrendingTVShows() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TvShowResponse> call = apiService.getTrendingTVShows(apiKey);

        call.enqueue(new Callback<TvShowResponse>() {
            @Override
            public void onResponse(@NonNull Call<TvShowResponse> call, @NonNull Response<TvShowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TvShow> shows = response.body().getResults();
                    Log.d("API", "Fetched " + shows.size() + " TV shows.");

                    // Initialize the adapter without the "tv" type parameter
                    tvShowAdapter = new TvShowAdapter(Home.this, shows);
                    recyclerTrendingTV.setAdapter(tvShowAdapter);
                } else {
                    Log.e("API", "TV Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TvShowResponse> call, @NonNull Throwable t) {
                Log.e("API", "TV Failure: " + t.getMessage());
            }
        });
    }
}
