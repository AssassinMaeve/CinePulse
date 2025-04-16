package com.example.cinepulse;
import com.example.cinepulse.BuildConfig;

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
        Toast.makeText(this, "API Key: " + BuildConfig.TMDB_API_KEY, Toast.LENGTH_SHORT).show();


        movieAdapter = new UpcomingAdapter(this, upcomingMovies);
        tvAdapter = new UpcomingAdapter(this, upcomingTVShows);

        recyclerMovies.setAdapter(movieAdapter);
        recyclerTV.setAdapter(tvAdapter);

        fetchUpcomingMovies();
        fetchUpcomingTVShows();
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