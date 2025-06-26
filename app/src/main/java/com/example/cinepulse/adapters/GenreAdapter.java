package com.example.cinepulse.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.R;
import com.example.cinepulse.fragments.GenreMoviesFragment;
import com.example.cinepulse.models.Genre;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private final FragmentManager fragmentManager;
    private final List<Genre> genreList;

    public GenreAdapter(FragmentManager fragmentManager, List<Genre> genreList) {
        this.fragmentManager = fragmentManager;
        this.genreList = genreList;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genreList.get(position);
        holder.genreButton.setText(genre.getName());

        holder.genreButton.setOnClickListener(v -> {
            Fragment genreFragment = GenreMoviesFragment.newInstance(genre.getId(), genre.getName());
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, genreFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        final Button genreButton;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreButton = itemView.findViewById(R.id.textGenreName);
        }
    }
}
