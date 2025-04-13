package com.example.cinepulse;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.adapters.TvShowAdapter;
import com.example.cinepulse.models.TvShow;
import com.example.cinepulse.adapters.MovieAdapter;
import com.example.cinepulse.models.Movie;
import com.example.cinepulse.models.MovieResponse;
import com.example.cinepulse.models.TvShowResponse;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;

import java.util.ArrayList;
import java.util.Collections;
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

    private List<Movie> movieList = new ArrayList<>(); // list for movies
    private List<TvShow> tvShowList = new ArrayList<>(); // list for TV shows

    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_movies);

        // Get API key from strings.xml
        apiKey = getString(R.string.api_key);

        // Get intent data
        genreId = getIntent().getIntExtra("genre_id", -1);
        genreName = getIntent().getStringExtra("genre_name");

        // Bind views
        title = findViewById(R.id.genreTitle);
        toggle = findViewById(R.id.toggleMovieTv);
        recyclerView = findViewById(R.id.recyclerByGenre);

        // Set title and layout manager
        title.setText(genreName + " - Movies");
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Set up the adapters
        movieAdapter = new MovieAdapter(this, new ArrayList<Object>(movieList), "movie");
        tvShowAdapter = new TvShowAdapter(this, tvShowList);

        // Set default adapter to MovieAdapter
        recyclerView.setAdapter(movieAdapter);

        // Fetch the movies by default
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
                    movieAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Handle failure
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
                }
            }

            @Override
            public void onFailure(Call<TvShowResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }
}
