package com.example.cinepulse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.BuildConfig;
import com.example.cinepulse.GenreListActivity;
import com.example.cinepulse.R;
import com.example.cinepulse.adapters.MovieAdapter;
import com.example.cinepulse.adapters.TvShowAdapter;
import com.example.cinepulse.models.Movie;
import com.example.cinepulse.models.MovieResponse;
import com.example.cinepulse.models.TvShow;
import com.example.cinepulse.models.TvShowResponse;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerTrendingMovies, recyclerTrendingTV;
    private MovieAdapter movieAdapter;
    private TvShowAdapter tvShowAdapter;

    private final String apiKey = BuildConfig.TMDB_API_KEY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerTrendingMovies = view.findViewById(R.id.recyclerTrendingMovies);
        recyclerTrendingTV = view.findViewById(R.id.recyclerTrendingTV);

        recyclerTrendingMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerTrendingTV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        fetchTrendingMovies();
        fetchTrendingTVShows();

        Button btnMovieList = view.findViewById(R.id.btnMovieList);
        btnMovieList.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), GenreListActivity.class));
        });

        return view;
    }

    private void fetchTrendingMovies() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<MovieResponse> call = apiService.getTrendingMovies(apiKey);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    movieAdapter = new MovieAdapter(requireContext(), new ArrayList<>(movies));
                    recyclerTrendingMovies.setAdapter(movieAdapter);
                } else {
                    handleApiError(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Failed to load movies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTrendingTVShows() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TvShowResponse> call = apiService.getTrendingTVShows(apiKey);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TvShowResponse> call, @NonNull Response<TvShowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TvShow> shows = response.body().getResults();
                    tvShowAdapter = new TvShowAdapter(requireContext(), shows);
                    recyclerTrendingTV.setAdapter(tvShowAdapter);
                } else {
                    handleApiError(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TvShowResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Failed to load TV shows", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleApiError(int errorCode) {
        String message;
        switch (errorCode) {
            case 404:
                message = "Data not found.";
                break;
            case 500:
                message = "Server error.";
                break;
            default:
                message = "Something went wrong.";
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
