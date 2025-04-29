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
    private static final String API_KEY = "580b03ff6e8e1d2881e7ecf2dccaf4c3"; // Store securely later

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        recyclerView = findViewById(R.id.recyclerReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);
        noReviewsTextView = findViewById(R.id.noReviewsTextView);

        reviewAdapter = new ReviewAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(reviewAdapter);

        // Get movieId and type from the Intent
        int movieId = getIntent().getIntExtra("MOVIE_ID", -1);
        String type = getIntent().getStringExtra("TYPE");

        // Validate the passed data
        if (movieId != -1 && type != null) {
            fetchReviews(movieId, type);
        } else {
            Toast.makeText(this, "Invalid data passed", Toast.LENGTH_SHORT).show();
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

        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
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
                        Log.e("ReviewActivity", "No reviews found.");
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noReviewsTextView.setVisibility(View.VISIBLE);
                    Log.e("ReviewActivity", "Error: " + response.code() + " - " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("ReviewActivity", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noReviewsTextView.setVisibility(View.VISIBLE);
                Toast.makeText(ReviewActivity.this, "Failed to load reviews: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ReviewActivity", "Network Failure: " + t.getMessage(), t);
            }
        });
    }
}

