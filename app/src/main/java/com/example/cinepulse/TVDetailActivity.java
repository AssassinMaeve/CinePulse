package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepulse.ToggleTheme.BaseActivity;
import com.example.cinepulse.adapters.CastAdapter;
import com.example.cinepulse.adapters.StreamingProviderAdapter;
import com.example.cinepulse.models.Cast;
import com.example.cinepulse.models.CastResponse;
import com.example.cinepulse.models.CountryProvider;
import com.example.cinepulse.models.StreamingProvider;
import com.example.cinepulse.models.TVDetail;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVDetailActivity extends BaseActivity {
    private static final String TAG = "TVDetailActivity";

    // Views
    private ImageView imagePoster;
    private TextView textTitle;
    private TextView textReleaseDate;
    private TextView textOverview;
    private RecyclerView recyclerCast;
    private TextView textNoCast;
    private RecyclerView recyclerStreaming;
    private TextView textNoStreamingProviders;  // Added this line for the missing TextView reference
    private YouTubePlayerView youtubePlayerView;
    private View btnAddToWatchlist;
    private TVDetail currentTVDetail; // Hold the fetched TV details for watchlist use
    private List<StreamingProvider> streamingProviders = new ArrayList<>();
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvdetail);

        // Initialize views
        imagePoster = findViewById(R.id.imagePoster);
        textNoStreamingProviders = findViewById(R.id.textNoStreamingProviders);

        textTitle = findViewById(R.id.textTitle);
        textReleaseDate = findViewById(R.id.textReleaseDate);
        textOverview = findViewById(R.id.textOverview);
        recyclerCast = findViewById(R.id.recyclerCast);
        textNoCast = findViewById(R.id.textNoCast);
        recyclerStreaming = findViewById(R.id.recyclerStreaming);
        textNoStreamingProviders = findViewById(R.id.textNoStreamingProviders);  // Added this line to initialize the textNoStreamingProviders
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        btnAddToWatchlist = findViewById(R.id.btnAddToWatchlist);
        View btnReview = findViewById(R.id.btnViewReviews);

        btnReview.setOnClickListener(v -> {
            if (currentTVDetail != null) {
                Intent intent = new Intent(TVDetailActivity.this, ReviewActivity.class);
                intent.putExtra("MOVIE_ID", currentTVDetail.getId()); // Use the same key as ReviewActivity expects
                intent.putExtra("TYPE", "tv");
                startActivity(intent);
            } else {
                Toast.makeText(this, "TV details not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });

        // YouTube lifecycle
        getLifecycle().addObserver(youtubePlayerView);

        int tvId = getIntent().getIntExtra("tv_id", -1);
        if (tvId != -1) {
            fetchTVShowDetails(tvId);
            fetchTVCast(tvId);
            fetchTVTrailers(tvId);
            fetchStreamingProviders(tvId);  // Call fetchStreamingProviders here to load streaming data
        }
    }

    private void fetchTVShowDetails(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TVDetail> call = apiService.getTVDetail(tvId, API_KEY);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TVDetail> call, @NonNull Response<TVDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTVDetail = response.body();
                    updateTVShowUI(currentTVDetail);

                    // Set up watchlist button
                    btnAddToWatchlist.setOnClickListener(v -> {
                        WatchlistItem item = new WatchlistItem(
                                currentTVDetail.getId(),
                                currentTVDetail.getName(),
                                currentTVDetail.getPosterPath(),
                                "tv"
                        );
                        WatchlistManager.addToWatchlist(TVDetailActivity.this, item);
                        Toast.makeText(TVDetailActivity.this, "Added to watchlist!", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVDetail> call, @NonNull Throwable t) {
                Log.e(TAG, "Error fetching TV details", t);
            }
        });
    }

    private void fetchStreamingProviders(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getTVWatchProviders(tvId, API_KEY).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<WatchProviderResponse> call, @NonNull Response<WatchProviderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WatchProviderResponse watchProviderResponse = response.body();
                    Map<String, CountryProvider> countryProviders = watchProviderResponse.getResults();

                    Log.d("StreamingProviders", "Received country providers: " + countryProviders);

                    if (countryProviders != null && !countryProviders.isEmpty()) {
                        CountryProvider countryProvider = countryProviders.get("IN"); // Change country if needed
                        if (countryProvider != null && countryProvider.getFlatrate() != null && !countryProvider.getFlatrate().isEmpty()) {
                            streamingProviders = countryProvider.getFlatrate();

                            recyclerStreaming.setLayoutManager(new LinearLayoutManager(TVDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            recyclerStreaming.setAdapter(new StreamingProviderAdapter(TVDetailActivity.this, streamingProviders));
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
            public void onFailure(@NonNull Call<WatchProviderResponse> call, @NonNull Throwable t) {
                Log.e("STREAMING_PROVIDERS", "Error fetching streaming providers", t);
                textNoStreamingProviders.setVisibility(View.VISIBLE);
            }
        });
    }


    private void updateTVShowUI(TVDetail tvDetail) {
        textTitle.setText(tvDetail.getName());
        textReleaseDate.setText(tvDetail.getFirstAirDate());
        textOverview.setText(tvDetail.getOverview());

        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500" + tvDetail.getPosterPath())
                .into(imagePoster);
    }

    private void fetchTVCast(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<CastResponse> call = apiService.getTVCredits(tvId, API_KEY);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CastResponse> call, @NonNull Response<CastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cast> castList = response.body().getCast();
                    if (castList != null && !castList.isEmpty()) {
                        setupCastRecycler(castList);
                    } else {
                        textNoCast.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CastResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error fetching TV cast", t);
            }
        });
    }

    private void setupCastRecycler(List<Cast> castList) {
        recyclerCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        CastAdapter castAdapter = new CastAdapter(this, castList);
        recyclerCast.setAdapter(castAdapter);
    }

    private void fetchTVTrailers(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TrailerResponse> call = apiService.getTVTrailers(tvId, API_KEY);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Trailer> trailers = response.body().getResults();
                    if (trailers != null && !trailers.isEmpty()) {
                        setupYouTubePlayer(trailers.get(0).getKey());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error fetching TV trailers", t);
            }
        });
    }

    private void setupYouTubePlayer(String videoId) {
        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youtubePlayerView.release();
    }
}
