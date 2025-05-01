package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepulse.ToggleTheme.BaseActivity;
import com.example.cinepulse.adapters.CastAdapter;
import com.example.cinepulse.adapters.StreamingProviderAdapter;  // Add this import for your adapter
import com.example.cinepulse.models.CastResponse;
import com.example.cinepulse.models.CountryProvider;
import com.example.cinepulse.models.MovieDetail;
import com.example.cinepulse.models.StreamingProvider;
import com.example.cinepulse.models.Trailer;
import com.example.cinepulse.models.TrailerResponse;
import com.example.cinepulse.models.WatchProviderResponse;
import com.example.cinepulse.models.WatchlistItem;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;
import com.example.cinepulse.utils.WatchlistManager;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetails extends BaseActivity {

    private TextView titleText, overviewText, releaseDateText;
    private ImageView posterImage;
    private YouTubePlayerView youTubePlayerView;
    private RecyclerView recyclerCast, recyclerStreamingProviders;
    private TextView textNoCast, textNoStreamingProviders;
    private Button btnAddToWatchlist;
    private static final String API_KEY = "580b03ff6e8e1d2881e7ecf2dccaf4c3";

    private String trailerKey = "";
    private MovieDetail currentMovieDetails;
    private List<StreamingProvider> streamingProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        int movieId = getIntent().getIntExtra("movie_id", -1);
        if (movieId == -1) {
            Toast.makeText(this, "Invalid Movie ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Init UI
        titleText = findViewById(R.id.textTitle);
        overviewText = findViewById(R.id.textOverview);
        releaseDateText = findViewById(R.id.textReleaseDate);
        posterImage = findViewById(R.id.imagePoster);
        youTubePlayerView = findViewById(R.id.youtubePlayerView);
        recyclerCast = findViewById(R.id.recyclerCast);
        recyclerStreamingProviders = findViewById(R.id.recyclerStreamingProviders);  // Recycler view for streaming providers
        textNoCast = findViewById(R.id.textNoCast);
        textNoStreamingProviders = findViewById(R.id.textNoStreamingProviders);  // TextView for no streaming providers
        btnAddToWatchlist = findViewById(R.id.btnAddToWatchlist);
        Button btnViewReviews = findViewById(R.id.btnViewReviews);

        recyclerCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerStreamingProviders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));  // Setup layout for streaming providers

        btnViewReviews.setOnClickListener(v -> {
            if (currentMovieDetails != null) {
                Intent intent = new Intent(MovieDetails.this, ReviewActivity.class);
                intent.putExtra("MOVIE_ID", currentMovieDetails.getId()); // Use the same key as ReviewActivity expects
                intent.putExtra("TYPE", "movie");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Movie details not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch everything for the movie
        fetchMovieDetails(movieId);
        fetchTrailer(movieId);
        fetchCast(movieId);
        fetchStreamingProviders(movieId);  // Fetch streaming providers

    }

    private void fetchMovieDetails(int movieId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getMovieDetail(movieId, API_KEY).enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetail details = response.body();
                    currentMovieDetails = details;
                    titleText.setText(details.getTitle());
                    overviewText.setText(details.getOverview());
                    releaseDateText.setText("Release Date: " + details.getReleaseDate());

                    Glide.with(MovieDetails.this)
                            .load("https://image.tmdb.org/t/p/w500/" + details.getPosterPath())
                            .into(posterImage);

                    btnAddToWatchlist.setOnClickListener(v -> {
                        WatchlistItem item = new WatchlistItem(
                                details.getId(),
                                details.getTitle(),
                                details.getPosterPath(),
                                "movie"
                        );
                        handleWatchlist(item);
                    });
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Log.e("MOVIE_DETAILS", "Error fetching movie details", t);
            }
        });
    }

    private void fetchTrailer(int movieId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getMovieTrailers(movieId, API_KEY).enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Trailer trailer : response.body().getResults()) {
                        if ("Trailer".equalsIgnoreCase(trailer.getType())) {
                            trailerKey = trailer.getKey();
                            playTrailer();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.e("TRAILER", "Error fetching trailer", t);
            }
        });
    }

    private void fetchCast(int movieId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getMovieCredits(movieId, API_KEY).enqueue(new Callback<CastResponse>() {
            @Override
            public void onResponse(Call<CastResponse> call, Response<CastResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCast() != null) {
                    recyclerCast.setAdapter(new CastAdapter(MovieDetails.this, response.body().getCast()));
                    textNoCast.setVisibility(TextView.GONE);
                } else {
                    textNoCast.setVisibility(TextView.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CastResponse> call, Throwable t) {
                Log.e("CAST", "Error fetching cast", t);
                textNoCast.setVisibility(TextView.VISIBLE);
            }
        });
    }

    private void fetchStreamingProviders(int movieId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getMovieStreamingProviders(movieId, API_KEY).enqueue(new Callback<WatchProviderResponse>() {
            @Override
            public void onResponse(Call<WatchProviderResponse> call, Response<WatchProviderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WatchProviderResponse watchProviderResponse = response.body();
                    Map<String, CountryProvider> countryProviders = watchProviderResponse.getResults();

                    Log.d("StreamingProviders", "Received country providers: " + countryProviders);

                    if (countryProviders != null && !countryProviders.isEmpty()) {
                        CountryProvider countryProvider = countryProviders.get("IN"); // Use "IN" or change as needed
                        if (countryProvider != null && countryProvider.getFlatrate() != null && !countryProvider.getFlatrate().isEmpty()) {
                            streamingProviders = countryProvider.getFlatrate();

                            recyclerStreamingProviders.setAdapter(new StreamingProviderAdapter(MovieDetails.this, streamingProviders));
                            textNoStreamingProviders.setVisibility(View.GONE);
                        } else {
                            textNoStreamingProviders.setVisibility(View.VISIBLE);
                        }
                    } else {
                        textNoStreamingProviders.setVisibility(View.VISIBLE);
                    }
                } else {
                    textNoStreamingProviders.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<WatchProviderResponse> call, Throwable t) {
                Log.e("STREAMING_PROVIDERS", "Error fetching streaming providers", t);
                textNoStreamingProviders.setVisibility(View.VISIBLE);
            }
        });
    }




    private void playTrailer() {
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(trailerKey, 0);
            }
        });
    }

    private void handleWatchlist(WatchlistItem item) {
        if (WatchlistManager.addToWatchlist(MovieDetails.this, item)) {
            Toast.makeText(MovieDetails.this, "Added to watchlist!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MovieDetails.this, "Already in watchlist!", Toast.LENGTH_SHORT).show();
        }
    }
}
