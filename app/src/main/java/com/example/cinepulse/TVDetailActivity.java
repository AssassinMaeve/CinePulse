package com.example.cinepulse;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cinepulse.models.Genre;
import com.example.cinepulse.models.TVDetail;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVDetailActivity extends AppCompatActivity {
    private static final String TAG = "TVDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvdetail);

        int tvId = getIntent().getIntExtra("tv_id", -1);
        if (tvId != -1) {
            fetchTVShowDetails(tvId);
        }
    }

    private void fetchTVShowDetails(int tvId) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        Call<TVDetail> call = apiService.getTVDetail(tvId, "580b03ff6e8e1d2881e7ecf2dccaf4c3");

        call.enqueue(new Callback<TVDetail>() {
            @Override
            public void onResponse(Call<TVDetail> call, Response<TVDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Log.e(TAG, "Error response: " + response.code());
                    // Show error to user
                }
            }

            @Override
            public void onFailure(Call<TVDetail> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                // Show error to user
            }
        });
    }

    private void updateUI(TVDetail tvDetail) {
        TextView title = findViewById(R.id.tv_title);
        TextView overview = findViewById(R.id.tv_overview);
        TextView firstAirDate = findViewById(R.id.tv_first_air_date);
        TextView rating = findViewById(R.id.tv_rating);
        ImageView poster = findViewById(R.id.iv_poster);

        // Set basic info
        title.setText(tvDetail.getName());
        overview.setText(tvDetail.getOverview());
        firstAirDate.setText(tvDetail.getFirstAirDate());
        rating.setText(String.format("%.1f", tvDetail.getVoteAverage()));

        // Load poster image
        if (tvDetail.getPosterPath() != null) {
            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500" + tvDetail.getPosterPath())
                    .into(poster);
        }

        // Handle genres if available
        if (tvDetail.getGenres() != null && !tvDetail.getGenres().isEmpty()) {
            TextView genres = findViewById(R.id.tv_genres);
            StringBuilder genresText = new StringBuilder();
            for (Genre genre : tvDetail.getGenres()) {
                if (genresText.length() > 0) genresText.append(", ");
                genresText.append(genre.getName());
            }
            genres.setText(genresText.toString());
        }
    }
}