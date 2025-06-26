package com.example.cinepulse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.R;
import com.example.cinepulse.adapters.GenreAdapter;
import com.example.cinepulse.models.Genre;

import java.util.ArrayList;
import java.util.List;

public class GenreListFragment extends Fragment {

    private static final int SPAN_COUNT = 2;
    private RecyclerView recyclerAllGenres;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre_list, container, false);
        recyclerAllGenres = view.findViewById(R.id.recyclerAllGenres);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        recyclerAllGenres.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
        recyclerAllGenres.setAdapter(new GenreAdapter(requireActivity().getSupportFragmentManager(), GENRES));
        recyclerAllGenres.setHasFixedSize(true);
    }
}
