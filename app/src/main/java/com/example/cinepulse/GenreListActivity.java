package com.example.cinepulse;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.ToggleTheme.BaseActivity;
import com.example.cinepulse.adapters.GenreAdapter;
import com.example.cinepulse.models.Genre;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display a list of genres in a grid layout.
 */
public class GenreListActivity extends BaseActivity {

    private static final int SPAN_COUNT = 2; // Grid columns
    private RecyclerView recyclerAllGenres;

    // Static genre list for performance (avoids recreation on config changes)
    private static final List<Genre> GENRES = new ArrayList<>() {{
        add(new Genre(28, "Action"));
        add(new Genre(35, "Comedy"));
        add(new Genre(27, "Horror"));
        add(new Genre(12, "Adventure"));
        add(new Genre(16, "Animation"));
        add(new Genre(80, "Crime"));
        add(new Genre(99, "Documentary"));
        add(new Genre(18, "Drama"));
        add(new Genre(14, "Fantasy"));
        add(new Genre(10749, "Romance"));
        add(new Genre(878, "Sci-Fi"));
        add(new Genre(53, "Thriller"));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_list);

        initializeViews();
        setupRecyclerView();
    }

    private void initializeViews() {
        recyclerAllGenres = findViewById(R.id.recyclerAllGenres);
    }

    private void setupRecyclerView() {
        // Set up grid layout with 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, SPAN_COUNT);
        recyclerAllGenres.setLayoutManager(layoutManager);

        // Use pre-initialized static genre list
        GenreAdapter genreAdapter = new GenreAdapter(this, GENRES);
        recyclerAllGenres.setAdapter(genreAdapter);

        // Optimization if item sizes are consistent
        recyclerAllGenres.setHasFixedSize(true);
    }
}
