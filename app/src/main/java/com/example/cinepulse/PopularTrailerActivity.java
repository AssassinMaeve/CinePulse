package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

public class PopularTrailerActivity extends AppCompatActivity {

    private RecyclerView recyclerTrailers;
    private TrailerAdapter trailerAdapter;
    private List<Trailer> popularTrailers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_populartraileractivity);

        recyclerTrailers = findViewById(R.id.recyclerTrailers);

        // Setup RecyclerView
        recyclerTrailers.setLayoutManager(new LinearLayoutManager(this));
        trailerAdapter = new TrailerAdapter(this, popularTrailers, this::onTrailerClicked);
        recyclerTrailers.setAdapter(trailerAdapter);

        fetchPopularTrailers();
    }

    private void onTrailerClicked(Trailer trailer) {
        Intent intent = new Intent(this, TrailerPlayerActivity.class);
        intent.putExtra("trailer_key", trailer.getKey());
        startActivity(intent);
    }

    private void fetchPopularTrailers() {
        TMDbApiService apiService = RetroFitClient.getApiService();

        // First fetch popular movies
        Call<MovieResponse> moviesCall = apiService.getPopularMovies(BuildConfig.TMDB_API_KEY);

        moviesCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the first 5 popular movies to fetch their trailers
                    List<Movie> movies = response.body().getResults().subList(0, Math.min(5, response.body().getResults().size()));
                    fetchTrailersForMovies(movies);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(PopularTrailerActivity.this, "Failed to load popular movies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTrailersForMovies(List<Movie> movies) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        popularTrailers.clear();

        for (Movie movie : movies) {
            Call<TrailerResponse> trailerCall = apiService.getMovieTrailers(movie.getId(), BuildConfig.TMDB_API_KEY);

            trailerCall.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Trailer> trailers = response.body().getResults();
                        if (!trailers.isEmpty()) {
                            // Add all trailers for each movie
                            popularTrailers.addAll(trailers);
                            trailerAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Toast.makeText(PopularTrailerActivity.this, "Failed to load trailers for movie", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}