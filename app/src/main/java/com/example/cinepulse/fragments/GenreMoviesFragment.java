package com.example.cinepulse.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class GenreMoviesFragment extends Fragment {

    private int genreId;
    private String genreName;

    private TextView title;
    private RecyclerView recyclerView;

    private MovieAdapter movieAdapter;
    private TvShowAdapter tvShowAdapter;

    private final List<Movie> movieList = new ArrayList<>();
    private final List<TvShow> tvShowList = new ArrayList<>();

    private String apiKey;

    public static GenreMoviesFragment newInstance(int genreId, String genreName) {
        GenreMoviesFragment fragment = new GenreMoviesFragment();
        Bundle args = new Bundle();
        args.putInt("genre_id", genreId);
        args.putString("genre_name", genreName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_genre_movies, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            genreId = getArguments().getInt("genre_id", -1);
            genreName = getArguments().getString("genre_name", "");
        }

        apiKey = com.example.cinepulse.BuildConfig.TMDB_API_KEY;

        title = view.findViewById(R.id.genreTitle);
        ToggleButton toggle = view.findViewById(R.id.toggleMovieTv);
        recyclerView = view.findViewById(R.id.recyclerByGenre);

        title.setText(genreName + " - Movies");
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        movieAdapter = new MovieAdapter(requireContext(), new ArrayList<>(movieList));
        tvShowAdapter = new TvShowAdapter((AppCompatActivity) requireContext(), tvShowList);
        recyclerView.setAdapter(movieAdapter);

        fetchGenreMovies(genreId);

        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                title.setText(genreName + " - TV Shows");
                recyclerView.setAdapter(tvShowAdapter);
                fetchGenreTVShows(genreId);
            } else {
                title.setText(genreName + " - Movies");
                recyclerView.setAdapter(movieAdapter);
                fetchGenreMovies(genreId);
            }
        });
    }

    private void fetchGenreMovies(int genreId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<MovieResponse> call = apiService.getMoviesByGenre(genreId, apiKey);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    movieAdapter.updateData(new ArrayList<>(movieList));

                    if (movieList.isEmpty()) {
                        Toast.makeText(requireContext(), "No movies found in this genre", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error fetching movies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchGenreTVShows(int genreId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TvShowResponse> call = apiService.getTVByGenre(genreId, apiKey);
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<TvShowResponse> call, @NonNull Response<TvShowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvShowList.clear();
                    tvShowList.addAll(response.body().getResults());
                    tvShowAdapter.notifyDataSetChanged();

                    if (tvShowList.isEmpty()) {
                        Toast.makeText(requireContext(), "No TV shows found in this genre", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load TV shows", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TvShowResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error fetching TV shows", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
