package com.example.cinepulse;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class GenreMoviesActivity extends AppCompatActivity {

    private int genreId;
    private String genreName;

    private TextView title;
    private ToggleButton toggle;
    private RecyclerView recyclerView;

    private MovieAdapter movieAdapter;
    private TvShowAdapter tvShowAdapter;

    private List<Movie> movieList = new ArrayList<>();
    private List<TvShow> tvShowList = new ArrayList<>();

    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_movies);

        apiKey = getString(R.string.api_key);

        genreId = getIntent().getIntExtra("genre_id", -1);
        genreName = getIntent().getStringExtra("genre_name");

        Log.d("GenreMoviesActivity", "Received Genre ID: " + genreId + ", Name: " + genreName);

        title = findViewById(R.id.genreTitle);
        toggle = findViewById(R.id.toggleMovieTv);
        recyclerView = findViewById(R.id.recyclerByGenre);

        title.setText(genreName + " - Movies");
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        movieAdapter = new MovieAdapter(this, new ArrayList<>(movieList), "movie");
        tvShowAdapter = new TvShowAdapter(this, tvShowList);

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
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    movieAdapter.updateData(new ArrayList<>(movieList));
                    Log.d("GenreMoviesActivity", "Movies fetched: " + movieList.size());
                    if (movieList.isEmpty()) {
                        Toast.makeText(GenreMoviesActivity.this, "No movies found in this genre", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("GenreMoviesActivity", "Failed to fetch movies. Code: " + response.code());
                    Toast.makeText(GenreMoviesActivity.this, "Failed to load movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("GenreMoviesActivity", "Movie fetch error: " + t.getMessage());
                Toast.makeText(GenreMoviesActivity.this, "Error fetching movies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchGenreTVShows(int genreId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TvShowResponse> call = apiService.getTVByGenre(genreId, apiKey);
        call.enqueue(new Callback<TvShowResponse>() {
            @Override
            public void onResponse(Call<TvShowResponse> call, Response<TvShowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvShowList.clear();
                    tvShowList.addAll(response.body().getResults());
                    tvShowAdapter.notifyDataSetChanged();
                    Log.d("GenreMoviesActivity", "TV shows fetched: " + tvShowList.size());
                    if (tvShowList.isEmpty()) {
                        Toast.makeText(GenreMoviesActivity.this, "No TV shows found in this genre", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("GenreMoviesActivity", "Failed to fetch TV shows. Code: " + response.code());
                    Toast.makeText(GenreMoviesActivity.this, "Failed to load TV shows", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TvShowResponse> call, Throwable t) {
                Log.e("GenreMoviesActivity", "TV show fetch error: " + t.getMessage());
                Toast.makeText(GenreMoviesActivity.this, "Error fetching TV shows", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
