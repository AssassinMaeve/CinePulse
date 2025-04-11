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
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerTrendingMovies, recyclerTrendingTV;
    private MovieAdapter movieAdapter, tvAdapter;

    private final String apiKey = "580b03ff6e8e1d2881e7ecf2dccaf4c3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);


        recyclerTrendingMovies = findViewById(R.id.recyclerTrendingMovies);
        recyclerTrendingTV = findViewById(R.id.recyclerTrendingTV);

        recyclerTrendingMovies.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerTrendingTV.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        fetchTrendingMovies();
        fetchTrendingTVShows();
    }

    private void fetchTrendingMovies() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<MovieResponse> call = apiService.getTrendingMovies(apiKey);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    movieAdapter = new MovieAdapter(Home.this, movies, "movie");

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
        Call<MovieResponse> call = apiService.getTrendingTVShows(apiKey);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> shows = response.body().getResults();
                    Log.d("API", "Fetched " + shows.size() + " TV shows."); // after getting shows
                    tvAdapter = new MovieAdapter(Home.this, shows, "tv");
                    recyclerTrendingTV.setAdapter(tvAdapter);
                } else {
                    Log.e("API", "TV Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("API", "TV Failure: " + t.getMessage());

            }

        });

        Button btnMovieList = findViewById(R.id.btnMovieList);
        btnMovieList.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, GenreListActivity.class);
            startActivity(intent);
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true; // Already on Home
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(Home.this, Search.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(Home.this, Profile.class));
                return true;
            }

            return false;
        });


    }
}
