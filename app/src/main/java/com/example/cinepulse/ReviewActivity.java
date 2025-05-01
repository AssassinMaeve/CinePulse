package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.ToggleTheme.BaseActivity;
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

public class ReviewActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private ProgressBar progressBar;
    private TextView noReviewsTextView;

    // Use BuildConfig to secure the API key
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerReviews);
        progressBar = findViewById(R.id.progressBar);
        noReviewsTextView = findViewById(R.id.noReviewsTextView);

        // Set layout manager and adapter for RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);  // Optimization if the list size is fixed

        // Initialize the review adapter
        reviewAdapter = new ReviewAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(reviewAdapter);

        // Retrieve movieId and type from the intent
        int movieId = getIntent().getIntExtra("MOVIE_ID", -1);
        String type = getIntent().getStringExtra("TYPE");

        // Validate data and fetch reviews if valid
        if (movieId != -1 && type != null) {
            fetchReviews(movieId, type);
        } else {
            Toast.makeText(this, "Invalid data passed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetches reviews for the movie or TV show from the TMDb API.
     * @param id The movie or TV show ID.
     * @param type The type ("movie" or "tv").
     */
    private void fetchReviews(int id, String type) {
        // Show loading indicator and hide the reviews initially
        progressBar.setVisibility(View.VISIBLE);
        noReviewsTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        // Create API service and make the network call
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<ReviewResponse> call = type.equalsIgnoreCase("tv")
                ? apiService.getTVReviews(id, API_KEY)
                : apiService.getMovieReviews(id, API_KEY);

        // Enqueue the API call for reviews
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                progressBar.setVisibility(View.GONE);

                // Check if the response is successful
                if (response.isSuccessful() && response.body() != null) {
                    List<Review> reviews = response.body().getResults();

                    // If there are reviews, display them, otherwise show "no reviews" message
                    if (reviews != null && !reviews.isEmpty()) {
                        reviewAdapter.setReviews(reviews);
                        recyclerView.setVisibility(View.VISIBLE);
                        noReviewsTextView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noReviewsTextView.setVisibility(View.VISIBLE);
                        Log.e("ReviewActivity", "No reviews found.");
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                // Hide loading indicator and show error message
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noReviewsTextView.setVisibility(View.VISIBLE);
                Toast.makeText(ReviewActivity.this, "Failed to load reviews: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ReviewActivity", "Network Failure: " + t.getMessage(), t);
            }
        });
    }

    /**
     * Handles errors in the API response.
     * @param response The response object.
     */
    private void handleError(Response<ReviewResponse> response) {
        recyclerView.setVisibility(View.GONE);
        noReviewsTextView.setVisibility(View.VISIBLE);
        Log.e("ReviewActivity", "Error: " + response.code() + " - " + response.message());
        try {
            if (response.errorBody() != null) {
                Log.e("ReviewActivity", "Error Body: " + response.errorBody().string());
            }
        } catch (IOException e) {
            Log.e("ReviewActivity", "Error parsing response body", e);
        }
    }
}
