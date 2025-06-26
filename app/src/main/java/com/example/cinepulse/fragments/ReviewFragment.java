package com.example.cinepulse.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.BuildConfig;
import com.example.cinepulse.R;
import com.example.cinepulse.adapters.ReviewAdapter;
import com.example.cinepulse.models.Review;
import com.example.cinepulse.models.ReviewResponse;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private ProgressBar progressBar;
    private TextView noReviewsTextView;

    private static final String ARG_MOVIE_ID = "movie_id";
    private static final String ARG_TYPE = "type";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;

    public static ReviewFragment newInstance(int movieId, String type) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerReviews);
        progressBar = view.findViewById(R.id.progressBar);
        noReviewsTextView = view.findViewById(R.id.noReviewsTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        reviewAdapter = new ReviewAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(reviewAdapter);

        Bundle args = getArguments();
        if (args != null) {
            int movieId = args.getInt(ARG_MOVIE_ID, -1);
            String type = args.getString(ARG_TYPE);

            if (movieId != -1 && type != null) {
                fetchReviews(movieId, type);
            } else {
                Toast.makeText(requireContext(), "Invalid data passed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchReviews(int id, String type) {
        progressBar.setVisibility(View.VISIBLE);
        noReviewsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<ReviewResponse> call = type.equalsIgnoreCase("tv")
                ? apiService.getTVReviews(id, API_KEY)
                : apiService.getMovieReviews(id, API_KEY);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull Response<ReviewResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Review> reviews = response.body().getResults();

                    if (reviews != null && !reviews.isEmpty()) {
                        reviewAdapter.setReviews(reviews);
                        recyclerView.setVisibility(View.VISIBLE);
                        noReviewsTextView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noReviewsTextView.setVisibility(View.VISIBLE);
                        Log.e("ReviewFragment", "No reviews found.");
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noReviewsTextView.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Failed to load reviews: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ReviewFragment", "Network Failure: " + t.getMessage(), t);
            }
        });
    }

    private void handleError(Response<ReviewResponse> response) {
        recyclerView.setVisibility(View.GONE);
        noReviewsTextView.setVisibility(View.VISIBLE);
        Log.e("ReviewFragment", "Error: " + response.code() + " - " + response.message());
        try {
            if (response.errorBody() != null) {
                Log.e("ReviewFragment", "Error Body: " + response.errorBody().string());
            }
        } catch (IOException e) {
            Log.e("ReviewFragment", "Error parsing response body", e);
        }
    }
}
