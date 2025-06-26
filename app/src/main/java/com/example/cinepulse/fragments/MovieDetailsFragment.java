package com.example.cinepulse.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.cinepulse.models.MovieDetail;
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

public class MovieDetailsFragment extends Fragment {

    private static final String ARG_MOVIE_ID = "movie_id";
    private int movieId;

    private TextView titleText, overviewText, releaseDateText, textNoCast, textNoStreamingProviders;
    private ImageView posterImage;
    private Button btnAddToWatchlist;
    private YouTubePlayerView youTubePlayerView;
    private RecyclerView recyclerCast, recyclerStreamingProviders;

    private String trailerKey = "";
    private MovieDetail currentMovieDetails;

    public static MovieDetailsFragment newInstance(int movieId) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt(ARG_MOVIE_ID);
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        titleText = view.findViewById(R.id.textTitle);
        overviewText = view.findViewById(R.id.textOverview);
        releaseDateText = view.findViewById(R.id.textReleaseDate);
        posterImage = view.findViewById(R.id.imagePoster);
        youTubePlayerView = view.findViewById(R.id.youtubePlayerView);
        recyclerCast = view.findViewById(R.id.recyclerCast);
        recyclerStreamingProviders = view.findViewById(R.id.recyclerStreamingProviders);
        textNoCast = view.findViewById(R.id.textNoCast);
        textNoStreamingProviders = view.findViewById(R.id.textNoStreamingProviders);
        btnAddToWatchlist = view.findViewById(R.id.btnAddToWatchlist);
        Button btnViewReviews = view.findViewById(R.id.btnViewReviews);

        recyclerCast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerStreamingProviders.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        btnViewReviews.setOnClickListener(v -> {
            if (currentMovieDetails != null) {
                ReviewFragment reviewFragment = ReviewFragment.newInstance(
                        currentMovieDetails.getId(),
                        "movie"
                );

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, reviewFragment) // container from activity layout
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Movie details not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });


        fetchMovieDetails(movieId);
        fetchTrailer(movieId);
        fetchCast(movieId);
        fetchStreamingProviders(movieId);

        return view;
    }

    private void fetchMovieDetails(int movieId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getMovieDetail(movieId, BuildConfig.TMDB_API_KEY).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetail> call, @NonNull Response<MovieDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetail details = response.body();
                    currentMovieDetails = details;

                    titleText.setText(details.getTitle());
                    overviewText.setText(details.getOverview());
                    releaseDateText.setText("Release Date: " + details.getReleaseDate());

                    Glide.with(requireContext())
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
            public void onFailure(@NonNull Call<MovieDetail> call, @NonNull Throwable t) {
                Log.e("MOVIE_DETAILS", "Error fetching movie details", t);
            }
        });
    }

    private void fetchTrailer(int movieId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getMovieTrailers(movieId, BuildConfig.TMDB_API_KEY).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {
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
            public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {
                Log.e("TRAILER", "Error fetching trailer", t);
            }
        });
    }

    private void playTrailer() {
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                if (!trailerKey.isEmpty()) {
                    youTubePlayer.loadVideo(trailerKey, 0);
                }
            }
        });
    }

    private void fetchCast(int movieId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getMovieCredits(movieId, BuildConfig.TMDB_API_KEY).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CastResponse> call, @NonNull Response<CastResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCast() != null) {
                    recyclerCast.setAdapter(new CastAdapter(requireContext(), response.body().getCast()));
                    textNoCast.setVisibility(View.GONE);
                } else {
                    textNoCast.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CastResponse> call, @NonNull Throwable t) {
                Log.e("CAST", "Error fetching cast", t);
                textNoCast.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchStreamingProviders(int movieId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        apiService.getMovieWatchProviders(movieId, BuildConfig.TMDB_API_KEY).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<WatchProviderResponse> call, @NonNull Response<WatchProviderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, CountryProvider> providers = response.body().getResults();
                    if (providers != null && providers.containsKey("IN")) {
                        CountryProvider inProvider = providers.get("IN");
                        if (inProvider.getFlatrate() != null && !inProvider.getFlatrate().isEmpty()) {
                            recyclerStreamingProviders.setAdapter(new StreamingProviderAdapter(requireContext(), inProvider.getFlatrate()));
                            textNoStreamingProviders.setVisibility(View.GONE);
                        } else {
                            textNoStreamingProviders.setVisibility(View.VISIBLE);
                        }
                    } else {
                        textNoStreamingProviders.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WatchProviderResponse> call, @NonNull Throwable t) {
                Log.e("STREAMING", "Failed to fetch providers", t);
                textNoStreamingProviders.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleWatchlist(WatchlistItem item) {
        if (WatchlistManager.addToWatchlist(requireContext(), item)) {
            Toast.makeText(requireContext(), "Added to watchlist!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Already in watchlist!", Toast.LENGTH_SHORT).show();
        }
    }
}
