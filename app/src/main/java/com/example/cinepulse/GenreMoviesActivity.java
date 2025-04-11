package com.example.cinepulse;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.adapters.MovieAdapter;
import com.example.cinepulse.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class GenreMoviesActivity extends AppCompatActivity {

    private RecyclerView recyclerGenreMovies;
    private MovieAdapter movieAdapter;
    private List<Movie> genreMovies = new ArrayList<>(); // Replace with real data later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_movies);

        int genreId = getIntent().getIntExtra("genre_id", -1);
        String genreName = getIntent().getStringExtra("genre_name");

        TextView title = findViewById(R.id.textGenreTitle);
        title.setText("Movies in Genre: " + genreName);

        recyclerGenreMovies = findViewById(R.id.recyclerGenreMovies);
        recyclerGenreMovies.setLayoutManager(new GridLayoutManager(this, 2));

        movieAdapter = new MovieAdapter(this, genreMovies, "genre"); // Make sure MovieAdapter is created
        recyclerGenreMovies.setAdapter(movieAdapter);

        // TODO: Fetch movies by genreId from TMDb API and update genreMovies list
    }
}
