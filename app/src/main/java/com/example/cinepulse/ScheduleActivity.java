package com.example.cinepulse;
import com.example.cinepulse.BuildConfig;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.adapters.UpcomingAdapter;
import com.example.cinepulse.models.MediaItem;
import com.example.cinepulse.models.UpcomingResponse;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerMovies, recyclerTV;
    private UpcomingAdapter movieAdapter, tvAdapter;
    private List<MediaItem> upcomingMovies = new ArrayList<>();
    private List<MediaItem> upcomingTVShows = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerMovies = findViewById(R.id.recyclerUpcomingMovies);
        recyclerTV = findViewById(R.id.recyclerUpcomingTV);

        recyclerMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerTV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        movieAdapter = new UpcomingAdapter(this, upcomingMovies, item -> openDetailsScreen(item, "movie"));
        tvAdapter = new UpcomingAdapter(this, upcomingTVShows, item -> openDetailsScreen(item, "tv"));


        recyclerMovies.setAdapter(movieAdapter);
        recyclerTV.setAdapter(tvAdapter);

        setupBottomNavigation();
        fetchUpcomingMovies();
        fetchUpcomingTVShows();
    }

    private void openDetailsScreen(MediaItem item, String type) {
        Intent intent = new Intent(this, MovieDetails.class);
    intent.putExtra("movie_id", item.getId());
        intent.putExtra("type", type); // "movie" or "tv"
        startActivity(intent);
    }


    private void fetchUpcomingMovies() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<UpcomingResponse> call = apiService.getUpcomingMovies(BuildConfig.TMDB_API_KEY);

        call.enqueue(new Callback<UpcomingResponse>() {
            @Override
            public void onResponse(Call<UpcomingResponse> call, Response<UpcomingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    upcomingMovies.clear(); // Clear before adding new items
                    upcomingMovies.addAll(response.body().getResults());
                    movieAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<UpcomingResponse> call, Throwable t) {
                Toast.makeText(ScheduleActivity.this, "Failed to load upcoming movies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(ScheduleActivity.this, Search.class));
                return true;
            } else if (itemId == R.id.nav_watchlist) {  // ✅ Add this
                startActivity(new Intent(ScheduleActivity.this, WatchlistActivity.class));
                return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(ScheduleActivity.this, ScheduleActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(ScheduleActivity.this, Profile.class));
                return true;
            }
            return false;
        });
    }

    private void fetchUpcomingTVShows() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<UpcomingResponse> call = apiService.getUpcomingTV(BuildConfig.TMDB_API_KEY);

        call.enqueue(new Callback<UpcomingResponse>() {
            @Override
            public void onResponse(Call<UpcomingResponse> call, Response<UpcomingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    upcomingTVShows.clear(); // Clear before adding new items
                    upcomingTVShows.addAll(response.body().getResults());
                    tvAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<UpcomingResponse> call, Throwable t) {
                Toast.makeText(ScheduleActivity.this, "Failed to load upcoming TV shows", Toast.LENGTH_SHORT).show();
            }
        });
    }
}