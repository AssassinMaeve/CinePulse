package com.example.cinepulse;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepulse.adapters.CastAdapter;
import com.example.cinepulse.models.Cast;
import com.example.cinepulse.models.CastResponse;
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

public class TVDetailActivity extends AppCompatActivity {
    private static final String TAG = "TVDetailActivity";

    // Views
    private ImageView imagePoster;
    private TextView textTitle;
    private TextView textReleaseDate;
    private TextView textOverview;
    private RecyclerView recyclerCast;
    private TextView textNoCast;
    private RecyclerView recyclerStreaming;
    private YouTubePlayerView youtubePlayerView;
    private View btnAddToWatchlist;
    private TVDetail currentTVDetail; // Hold the fetched TV details for watchlist use

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvdetail);

        // Initialize views
        imagePoster = findViewById(R.id.imagePoster);
        textTitle = findViewById(R.id.textTitle);
        textReleaseDate = findViewById(R.id.textReleaseDate);
        textOverview = findViewById(R.id.textOverview);
        recyclerCast = findViewById(R.id.recyclerCast);
        textNoCast = findViewById(R.id.textNoCast);
        recyclerStreaming = findViewById(R.id.recyclerStreaming);
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        btnAddToWatchlist = findViewById(R.id.btnAddToWatchlist);

        // YouTube lifecycle
        getLifecycle().addObserver(youtubePlayerView);

        int tvId = getIntent().getIntExtra("tv_id", -1);
        if (tvId != -1) {
            fetchTVShowDetails(tvId);
            fetchTVCast(tvId);
            fetchTVTrailers(tvId);
        }
    }

    private void fetchTVShowDetails(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TVDetail> call = apiService.getTVDetail(tvId, "580b03ff6e8e1d2881e7ecf2dccaf4c3");

        call.enqueue(new Callback<TVDetail>() {
            @Override
            public void onResponse(Call<TVDetail> call, Response<TVDetail> response) {
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
            public void onFailure(Call<TVDetail> call, Throwable t) {
                Log.e(TAG, "Error fetching TV details", t);
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
        Call<CastResponse> call = apiService.getTVCredits(tvId, "580b03ff6e8e1d2881e7ecf2dccaf4c3");

        call.enqueue(new Callback<CastResponse>() {
            @Override
            public void onResponse(Call<CastResponse> call, Response<CastResponse> response) {
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
            public void onFailure(Call<CastResponse> call, Throwable t) {
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
        Call<TrailerResponse> call = apiService.getTVTrailers(tvId, "580b03ff6e8e1d2881e7ecf2dccaf4c3");

        call.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Trailer> trailers = response.body().getResults();
                    if (trailers != null && !trailers.isEmpty()) {
                        setupYouTubePlayer(trailers.get(0).getKey());
                    }
                }
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
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
