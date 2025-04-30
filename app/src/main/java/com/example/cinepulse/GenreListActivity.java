package com.example.cinepulse;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinepulse.ToggleTheme.BaseActivity;
import com.example.cinepulse.adapters.GenreAdapter;
import com.example.cinepulse.models.Genre;
import java.util.ArrayList;
import java.util.List;

public class GenreListActivity extends BaseActivity {

    private static final int SPAN_COUNT = 2;
    private RecyclerView recyclerAllGenres;
    private GenreAdapter genreAdapter;

    // Static list of genres to avoid recreation
    private static final List<Genre> GENRES = new ArrayList<Genre>() {{
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
        // Reuse the same layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, SPAN_COUNT);
        recyclerAllGenres.setLayoutManager(layoutManager);

        // Initialize adapter once and reuse
        genreAdapter = new GenreAdapter(this, GENRES);
        recyclerAllGenres.setAdapter(genreAdapter);

        // Optional: Set fixed size if all items have consistent dimensions
        recyclerAllGenres.setHasFixedSize(true);
    }
}