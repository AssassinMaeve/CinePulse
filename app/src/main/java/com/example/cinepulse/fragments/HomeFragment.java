package com.example.cinepulse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cinepulse.BuildConfig;
import com.example.cinepulse.NavFrag;
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
    private RecyclerView recyclerLatestMovies, recyclerLatestSeries;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MovieAdapter movieAdapter, latestMovieAdapter;
    private TvShowAdapter tvShowAdapter, latestTvShowAdapter;

    private final String apiKey = BuildConfig.TMDB_API_KEY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshHome);
        recyclerLatestMovies = view.findViewById(R.id.recyclerTrending); // Note: ID in new layout is recyclerTrending for latest movies
        recyclerLatestSeries = view.findViewById(R.id.recyclerTrendingSeries);
        RecyclerView recyclerHome = view.findViewById(R.id.recyclerHome);
        recyclerTrendingMovies = recyclerHome; // Re-mapping for simplicity if needed, but let's stick to IDs
        
        // Let's use the explicit IDs from the new fragment_home.xml
        recyclerLatestMovies = view.findViewById(R.id.recyclerTrending);
        recyclerLatestSeries = view.findViewById(R.id.recyclerTrendingSeries);
        RecyclerView recyclerWatchlist = view.findViewById(R.id.recyclerHome);
        
        View cardSearch = view.findViewById(R.id.cardSearch);
        View cardTrailers = view.findViewById(R.id.cardTrailers);

        setupSwipeRefresh();
        fetchAllData();

        cardSearch.setOnClickListener(v -> openSearch());
        cardTrailers.setOnClickListener(v -> openTrailers());

        return view;
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_blue);
        swipeRefreshLayout.setOnRefreshListener(this::fetchAllData);
    }

    private void fetchAllData() {
        fetchLatestMovies();
        fetchLatestTVShows();
        fetchTrendingMovies(); // Reusing Trending for Watchlist/Explore for now
        swipeRefreshLayout.setRefreshing(false);
    }

    private void fetchLatestMovies() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getNowPlayingMovies(apiKey).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (!isAdded() || getView() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    latestMovieAdapter = new MovieAdapter(requireContext(), new ArrayList<>(response.body().getResults()));
                    recyclerLatestMovies.setAdapter(latestMovieAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Failed to load movies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLatestTVShows() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getOnTheAirTVShows(apiKey).enqueue(new Callback<TvShowResponse>() {
            @Override
            public void onResponse(@NonNull Call<TvShowResponse> call, @NonNull Response<TvShowResponse> response) {
                if (!isAdded() || getView() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    latestTvShowAdapter = new TvShowAdapter((AppCompatActivity) requireContext(), response.body().getResults());
                    recyclerLatestSeries.setAdapter(latestTvShowAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TvShowResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Failed to load series", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTrendingMovies() {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getTrendingMovies(apiKey).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (!isAdded() || getView() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    movieAdapter = new MovieAdapter(requireContext(), new ArrayList<>(response.body().getResults()));
                    RecyclerView recyclerWatchlist = getView().findViewById(R.id.recyclerHome);
                    if (recyclerWatchlist != null) recyclerWatchlist.setAdapter(movieAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {}
        });
    }

    private void openSearch() {
        Intent intent = new Intent(getActivity(), NavFrag.class);
        intent.putExtra("openFragment", "search");
        startActivity(intent);
    }

    private void openTrailers() {
        Intent intent = new Intent(getActivity(), NavFrag.class);
        intent.putExtra("openFragment", "trailers");
        startActivity(intent);
    }
}
