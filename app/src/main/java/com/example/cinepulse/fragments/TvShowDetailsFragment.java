package com.example.cinepulse.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepulse.BuildConfig;
import com.example.cinepulse.R;
import com.example.cinepulse.adapters.CastAdapter;
import com.example.cinepulse.adapters.StreamingProviderAdapter;
import com.example.cinepulse.models.CastResponse;
import com.example.cinepulse.models.CountryProvider;
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

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvShowDetailsFragment extends Fragment {

    private static final String TAG = "TvShowDetailsFragment";
    private static final String ARG_TV_ID = "tv_id";

    private int tvId;
    private TVDetail currentTVDetail;

    private ImageView imagePoster;
    private TextView textTitle, textReleaseDate, textOverview, textNoCast, textNoStreamingProviders;
    private RecyclerView recyclerCast, recyclerStreaming;
    private YouTubePlayerView youtubePlayerView;
    private View btnAddToWatchlist, btnReview;

    public static TvShowDetailsFragment newInstance(int tvId, String type) {
        TvShowDetailsFragment fragment = new TvShowDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TV_ID, tvId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tvId = getArguments().getInt(ARG_TV_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tv_show_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imagePoster = view.findViewById(R.id.imagePoster);
        textTitle = view.findViewById(R.id.textTitle);
        textReleaseDate = view.findViewById(R.id.textReleaseDate);
        textOverview = view.findViewById(R.id.textOverview);
        recyclerCast = view.findViewById(R.id.recyclerCast);
        textNoCast = view.findViewById(R.id.textNoCast);
        recyclerStreaming = view.findViewById(R.id.recyclerStreaming);
        textNoStreamingProviders = view.findViewById(R.id.textNoStreamingProviders);
        youtubePlayerView = view.findViewById(R.id.youtubePlayerView);
        btnAddToWatchlist = view.findViewById(R.id.btnAddToWatchlist);
        btnReview = view.findViewById(R.id.btnViewReviews);

        requireActivity().getLifecycle().addObserver(youtubePlayerView);

        fetchTVShowDetails(tvId);
        fetchTVCast(tvId);
        fetchTVTrailers(tvId);
        fetchStreamingProviders(tvId);
    }

    private void fetchTVShowDetails(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getTVDetail(tvId, BuildConfig.TMDB_API_KEY).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TVDetail> call, @NonNull Response<TVDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTVDetail = response.body();
                    updateUI(currentTVDetail);

                    btnAddToWatchlist.setOnClickListener(v -> {
                        WatchlistItem item = new WatchlistItem(
                                currentTVDetail.getId(),
                                currentTVDetail.getName(),
                                currentTVDetail.getPosterPath(),
                                "tv"
                        );
                        WatchlistManager.addToWatchlist(requireContext(), item);
                        Toast.makeText(getContext(), "Added to watchlist!", Toast.LENGTH_SHORT).show();
                    });

                    btnReview.setOnClickListener(v -> {
                        if (currentTVDetail != null) {
                            ReviewFragment reviewFragment = ReviewFragment.newInstance(
                                    currentTVDetail.getId(),
                                    "tv"
                            );

                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, reviewFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            Toast.makeText(getContext(), "TV show details not loaded yet", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVDetail> call, @NonNull Throwable t) {
                Log.e(TAG, "TV Details Fetch Error", t);
            }
        });
    }

    private void updateUI(TVDetail detail) {
        textTitle.setText(detail.getName());
        textReleaseDate.setText(detail.getFirstAirDate());
        textOverview.setText(detail.getOverview());

        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500" + detail.getPosterPath())
                .into(imagePoster);
    }

    private void fetchTVCast(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getTVCredits(tvId, BuildConfig.TMDB_API_KEY).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CastResponse> call, @NonNull Response<CastResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCast() != null) {
                    recyclerCast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    recyclerCast.setAdapter(new CastAdapter(getContext(), response.body().getCast()));
                    textNoCast.setVisibility(View.GONE);
                } else {
                    textNoCast.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CastResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "TV Cast Fetch Error", t);
                textNoCast.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchStreamingProviders(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getTVWatchProviders(tvId, BuildConfig.TMDB_API_KEY).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<WatchProviderResponse> call, @NonNull Response<WatchProviderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, CountryProvider> providers = response.body().getResults();
                    CountryProvider inProvider = providers.get("IN");

                    if (inProvider != null && inProvider.getFlatrate() != null && !inProvider.getFlatrate().isEmpty()) {
                        recyclerStreaming.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        recyclerStreaming.setAdapter(new StreamingProviderAdapter(getContext(), inProvider.getFlatrate()));
                        textNoStreamingProviders.setVisibility(View.GONE);
                    } else {
                        textNoStreamingProviders.setVisibility(View.VISIBLE);
                    }
                } else {
                    textNoStreamingProviders.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WatchProviderResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Streaming Provider Error", t);
                textNoStreamingProviders.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchTVTrailers(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getTVTrailers(tvId, BuildConfig.TMDB_API_KEY).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Trailer trailer : response.body().getResults()) {
                        if ("Trailer".equalsIgnoreCase(trailer.getType())) {
                            setupYouTubePlayer(trailer.getKey());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Trailer Fetch Error", t);
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
    public void onDestroyView() {
        super.onDestroyView();
        youtubePlayerView.release();
    }
}
