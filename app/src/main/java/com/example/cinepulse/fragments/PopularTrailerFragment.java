package com.example.cinepulse.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.BuildConfig;
import com.example.cinepulse.R;
import com.example.cinepulse.adapters.TrailerAdapter;
import com.example.cinepulse.models.Movie;
import com.example.cinepulse.models.MovieResponse;
import com.example.cinepulse.models.Trailer;
import com.example.cinepulse.models.TrailerResponse;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularTrailerFragment extends Fragment {

    private static final String TAG = "PopularTrailerFragment";
    private TrailerAdapter trailerAdapter;
    private final List<Trailer> popularTrailers = new ArrayList<>();
    private boolean hasShownDialog = false;

    private RecyclerView recyclerTrailers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_populartrailer, container, false);

        recyclerTrailers = view.findViewById(R.id.recyclerTrailers);
        recyclerTrailers.setLayoutManager(new LinearLayoutManager(getContext()));
        trailerAdapter = new TrailerAdapter(getContext(), popularTrailers, this::onTrailerClicked);
        recyclerTrailers.setAdapter(trailerAdapter);

        showSpoilerWarningDialog();

        return view;
    }

    private void showSpoilerWarningDialog() {
        if (!hasShownDialog && getContext() != null) {
            hasShownDialog = true;
            new AlertDialog.Builder(getContext())
                    .setTitle("Spoiler Warning")
                    .setMessage("Trailers may contain spoilers. Do you want to continue?")
                    .setCancelable(false)
                    .setPositiveButton("Yes, show me", (dialog, which) -> fetchPopularTrailers())
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                        // Do nothing — just stay on the current fragment
                    })
                    .show();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void onTrailerClicked(Trailer trailer) {
        try {
            String youtubeUrl = "https://www.youtube.com/watch?v=" + trailer.getKey();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.google.android.youtube");

            if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                intent.setPackage(null);
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error opening trailer", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error opening trailer", e);
        }
    }

    private void fetchPopularTrailers() {
        TMDbApiService apiService = RetroFitClient.getApiService();

        Log.d(TAG, "Fetching popular movies...");
        apiService.getPopularMovies(BuildConfig.TMDB_API_KEY)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            Log.d(TAG, "Got " + movies.size() + " popular movies");

                            if (!movies.isEmpty()) {
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
                    .enqueue(new Callback<TrailerResponse>() {
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
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        Log.e(TAG, errorMessage);
    }

    private void handleNetworkError(Throwable t) {
        Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Network Error", t);
    }

    private void showEmptyState() {
        Toast.makeText(getContext(), "No movies found", Toast.LENGTH_SHORT).show();
    }
}
