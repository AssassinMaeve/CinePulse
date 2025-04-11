package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;
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

    private String trailerKey = "";
    private static final String API_KEY = "580b03ff6e8e1d2881e7ecf2dccaf4c3";

    private TextView textNoCast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        titleText = findViewById(R.id.textTitle);
        overviewText = findViewById(R.id.textOverview);
        releaseDateText = findViewById(R.id.textReleaseDate);
        posterImage = findViewById(R.id.imagePoster);
        youTubePlayerView = findViewById(R.id.youtubePlayerView);
        recyclerCast = findViewById(R.id.recyclerCast);
        recyclerStreaming = findViewById(R.id.recyclerStreaming);
        textNoCast = findViewById(R.id.textNoCast);


        recyclerCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerStreaming.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Intent intent = getIntent();
        int itemId = intent.getIntExtra("movie_id", -1);
        String type = intent.getStringExtra("type");

        if (itemId != -1) {
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
            Log.e("MOVIE_DETAILS", "Invalid ID");
        }
    }

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
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Log.e("MOVIE_DETAILS", "Failed to fetch movie details", t);
            }
        });
    }

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
                }
            }

            @Override
            public void onFailure(Call<TVDetail> call, Throwable t) {
                Log.e("TV_DETAILS", "Failed to fetch TV details", t);
            }
        });
    }

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
                } else {
                    Log.e("TV_CAST", "Response error: " + response.message());
                    textNoCast.setVisibility(TextView.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CastResponse> call, Throwable t) {
                Log.e("TV_CAST", "Failed to fetch TV cast", t);
                textNoCast.setVisibility(TextView.VISIBLE);
            }
        });
    }




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

    private void playTrailer() {
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(trailerKey, 0);
            }
        });
    }

}
