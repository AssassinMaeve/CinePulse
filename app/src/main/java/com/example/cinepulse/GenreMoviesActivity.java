package com.example.cinepulse;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.ToggleTheme.BaseActivity;
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

public class GenreMoviesActivity extends BaseActivity {

    private int genreId;
    private String genreName;

    private TextView title;
    private RecyclerView recyclerView;

    private MovieAdapter movieAdapter;
    private TvShowAdapter tvShowAdapter;

    private final List<Movie> movieList = new ArrayList<>();
    private final List<TvShow> tvShowList = new ArrayList<>();

    private String apiKey;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_movies);

        // Fetch the API key from resources
        apiKey = getString(R.string.api_key);

        // Retrieve genre information passed from the previous screen
        genreId = getIntent().getIntExtra("genre_id", -1);
        genreName = getIntent().getStringExtra("genre_name");

        Log.d("GenreMoviesActivity", "Received Genre ID: " + genreId + ", Name: " + genreName);

        // Initialize UI components
        title = findViewById(R.id.genreTitle);
        ToggleButton toggle = findViewById(R.id.toggleMovieTv);
        recyclerView = findViewById(R.id.recyclerByGenre);

        // Set the initial genre title (Movies)
        title.setText(genreName + " - Movies");
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Initialize the adapters
        movieAdapter = new MovieAdapter(this, new ArrayList<>(movieList));
        tvShowAdapter = new TvShowAdapter(this, tvShowList);

        // Set the initial adapter to movieAdapter
        recyclerView.setAdapter(movieAdapter);

        // Fetch movies for the given genre
        fetchGenreMovies(genreId);

        // Toggle button listener for switching between movies and TV shows
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // If switched to TV shows, fetch TV shows and update the title
            if (isChecked) {
                title.setText(genreName + " - TV Shows");
                recyclerView.setAdapter(tvShowAdapter);
                fetchGenreTVShows(genreId);
            } else {
                // If switched back to Movies, fetch movies and update the title
                title.setText(genreName + " - Movies");
                recyclerView.setAdapter(movieAdapter);
                fetchGenreMovies(genreId);
            }
        });
    }

    // Method to fetch movies by genre using Retrofit
    private void fetchGenreMovies(int genreId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<MovieResponse> call = apiService.getMoviesByGenre(genreId, apiKey);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Clear the movie list and add fetched movies
                    movieList.clear();
                    movieList.addAll(response.body().getResults());

                    // Update the adapter with new data
                    movieAdapter.updateData(new ArrayList<>(movieList));

                    Log.d("GenreMoviesActivity", "Movies fetched: " + movieList.size());

                    // Show a message if no movies are found
                    if (movieList.isEmpty()) {
                        Toast.makeText(GenreMoviesActivity.this, "No movies found in this genre", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("GenreMoviesActivity", "Failed to fetch movies. Code: " + response.code());
                    Toast.makeText(GenreMoviesActivity.this, "Failed to load movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("GenreMoviesActivity", "Movie fetch error: " + t.getMessage());
                Toast.makeText(GenreMoviesActivity.this, "Error fetching movies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to fetch TV shows by genre using Retrofit
    private void fetchGenreTVShows(int genreId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TvShowResponse> call = apiService.getTVByGenre(genreId, apiKey);
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<TvShowResponse> call, @NonNull Response<TvShowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Clear the TV shows list and add fetched shows
                    tvShowList.clear();
                    tvShowList.addAll(response.body().getResults());

                    // Notify adapter of new data
                    tvShowAdapter.notifyDataSetChanged();

                    Log.d("GenreMoviesActivity", "TV shows fetched: " + tvShowList.size());

                    // Show a message if no TV shows are found
                    if (tvShowList.isEmpty()) {
                        Toast.makeText(GenreMoviesActivity.this, "No TV shows found in this genre", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("GenreMoviesActivity", "Failed to fetch TV shows. Code: " + response.code());
                    Toast.makeText(GenreMoviesActivity.this, "Failed to load TV shows", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TvShowResponse> call, @NonNull Throwable t) {
                Log.e("GenreMoviesActivity", "TV show fetch error: " + t.getMessage());
                Toast.makeText(GenreMoviesActivity.this, "Error fetching TV shows", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
