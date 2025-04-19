package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepulse.adapters.CastAdapter;
import com.example.cinepulse.models.Cast;
import com.example.cinepulse.models.CastResponse;
import com.example.cinepulse.models.MovieDetail;
import com.example.cinepulse.models.TVDetail;
import com.example.cinepulse.models.Trailer;
import com.example.cinepulse.models.TrailerResponse;
import com.example.cinepulse.models.WatchlistItem;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;
import com.example.cinepulse.utils.WatchlistManager;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MovieDetails extends AppCompatActivity {

    private TextView titleText, overviewText, releaseDateText;
    private ImageView posterImage;
    private YouTubePlayerView youTubePlayerView;
    private RecyclerView recyclerCast, recyclerStreaming;
    private TextView textNoCast;
    private Button btnAddToWatchlist;
    private static final String API_KEY = "580b03ff6e8e1d2881e7ecf2dccaf4c3";

    private String trailerKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        int itemId = getIntent().getIntExtra("movie_id", -1);
        String type = getIntent().getStringExtra("type");

        // Initialize UI components
        titleText = findViewById(R.id.textTitle);
        overviewText = findViewById(R.id.textOverview);
        releaseDateText = findViewById(R.id.textReleaseDate);
        posterImage = findViewById(R.id.imagePoster);
        youTubePlayerView = findViewById(R.id.youtubePlayerView);
        recyclerCast = findViewById(R.id.recyclerCast);
        recyclerStreaming = findViewById(R.id.recyclerStreaming);
        textNoCast = findViewById(R.id.textNoCast);
        btnAddToWatchlist = findViewById(R.id.btnAddToWatchlist);
        Button btnViewReviews = findViewById(R.id.btnViewReviews);


        recyclerCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerStreaming.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));






        if (itemId != -1 && type != null) {
            btnViewReviews.setOnClickListener(v -> {
                // Navigate to ReviewActivity with the movie/show ID and type
                Intent reviewIntent = new Intent(MovieDetails.this, ReviewActivity.class);
                reviewIntent.putExtra("movie_id", itemId);
                reviewIntent.putExtra("type", type); // "movie" or "tv"
                startActivity(reviewIntent);
            });

            if ("tv".equalsIgnoreCase(type)) {
                fetchTVDetails(itemId);
                fetchTVTrailer(itemId);
                fetchTVCast(itemId);
            } else {
                fetchMovieDetails(itemId);
                fetchTrailer(itemId);
                fetchCast(itemId);
            }
        } else {
            Log.e("MOVIE_DETAILS", "Invalid ID or Type");
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            finish();
        }
}

        // Fetch movie details
    private void fetchMovieDetails(int movieId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<MovieDetail> call = apiService.getMovieDetail(movieId, API_KEY);

        call.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetail details = response.body();

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
                Log.e("MOVIE_DETAILS", "Failed to fetch movie details", t);
            }
        });
    }

    // Fetch TV details
    private void fetchTVDetails(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TVDetail> call = apiService.getTVDetail(tvId, API_KEY);

        call.enqueue(new Callback<TVDetail>() {
            @Override
            public void onResponse(Call<TVDetail> call, Response<TVDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TVDetail details = response.body();

                    titleText.setText(details.getName());
                    overviewText.setText(details.getOverview());
                    releaseDateText.setText("First Air Date: " + details.getFirstAirDate());

                    Glide.with(MovieDetails.this)
                            .load("https://image.tmdb.org/t/p/w500/" + details.getPosterPath())
                            .into(posterImage);

                    btnAddToWatchlist.setOnClickListener(v -> {
                        WatchlistItem item = new WatchlistItem(
                                details.getId(),
                                details.getName(),
                                details.getPosterPath(),
                                "tv"
                        );
                        handleWatchlist(item);
                    });
                }
            }

            @Override
            public void onFailure(Call<TVDetail> call, Throwable t) {
                Log.e("TV_DETAILS", "Failed to fetch TV details", t);
            }
        });
    }

    // Fetch cast for movies
    private void fetchCast(int movieId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<CastResponse> call = apiService.getMovieCredits(movieId, API_KEY);

        call.enqueue(new Callback<CastResponse>() {
            @Override
            public void onResponse(Call<CastResponse> call, Response<CastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recyclerCast.setAdapter(new CastAdapter(MovieDetails.this, response.body().getCast()));
                }
            }

            @Override
            public void onFailure(Call<CastResponse> call, Throwable t) {
                Log.e("CAST", "Failed to load cast", t);
            }
        });
    }

    // Fetch cast for TV shows
    private void fetchTVCast(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<CastResponse> call = apiService.getTVCredits(tvId, API_KEY);

        call.enqueue(new Callback<CastResponse>() {
            @Override
            public void onResponse(Call<CastResponse> call, Response<CastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cast> castList = response.body().getCast();
                    if (castList != null && !castList.isEmpty()) {
                        recyclerCast.setAdapter(new CastAdapter(MovieDetails.this, castList));
                        textNoCast.setVisibility(TextView.GONE);
                    } else {
                        textNoCast.setVisibility(TextView.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<CastResponse> call, Throwable t) {
                Log.e("TV_CAST", "Failed to fetch TV cast", t);
                textNoCast.setVisibility(TextView.VISIBLE);
            }
        });
    }

    // Fetch trailer for movies
    private void fetchTrailer(int movieId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TrailerResponse> call = apiService.getMovieTrailers(movieId, API_KEY);

        call.enqueue(new Callback<TrailerResponse>() {
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
                Log.e("TRAILER", "Failed to load trailer", t);
            }
        });
    }

    // Fetch trailer for TV shows
    private void fetchTVTrailer(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TrailerResponse> call = apiService.getTVTrailers(tvId, API_KEY);

        call.enqueue(new Callback<TrailerResponse>() {
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
                Log.e("TV_TRAILER", "Failed to load TV trailer", t);
            }
        });
    }

    // Play the trailer
    private void playTrailer() {
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(trailerKey, 0);
            }
        });
    }

    // Handle adding to watchlist
    private void handleWatchlist(WatchlistItem item) {
        if (WatchlistManager.addToWatchlist(MovieDetails.this, item)) {
            Toast.makeText(MovieDetails.this, "Added to watchlist!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MovieDetails.this, "Already in watchlist!", Toast.LENGTH_SHORT).show();
        }
    }
}
