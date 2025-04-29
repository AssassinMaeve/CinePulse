package com.example.cinepulse;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.ToggleTheme.BaseActivity;
import com.example.cinepulse.adapters.GenreAdapter;
import com.example.cinepulse.models.Genre;

import java.util.Arrays;
import java.util.List;

public class GenreListActivity extends BaseActivity {

    private RecyclerView recyclerAllGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_list);

        recyclerAllGenres = findViewById(R.id.recyclerAllGenres);
        recyclerAllGenres.setLayoutManager(new GridLayoutManager(this, 2));

        List<Genre> allGenres = Arrays.asList(
                new Genre(28, "Action"),
                new Genre(35, "Comedy"),
                new Genre(27, "Horror"),
                new Genre(12, "Adventure"),
                new Genre(16, "Animation"),
                new Genre(80, "Crime"),
                new Genre(99, "Documentary"),
                new Genre(18, "Drama"),
                new Genre(14, "Fantasy"),
                new Genre(10749, "Romance"),
                new Genre(878, "Sci-Fi"),
                new Genre(53, "Thriller")
        );

        recyclerAllGenres.setAdapter(new GenreAdapter(this, allGenres));
    }
}
