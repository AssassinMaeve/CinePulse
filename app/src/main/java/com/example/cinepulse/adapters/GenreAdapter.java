package com.example.cinepulse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.GenreMoviesActivity;
import com.example.cinepulse.R;
import com.example.cinepulse.models.Genre;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private final Context context;
    private final List<Genre> genreList;

    public GenreAdapter(Context context, List<Genre> genreList) {
        this.context = context;
        this.genreList = genreList;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for the individual genre item
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        // Get the current genre
        Genre genre = genreList.get(position);

        // Set the genre name to the button
        holder.genreButton.setText(genre.getName());

        // Set click listener to navigate to GenreMoviesActivity with the genre data
        holder.genreButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, GenreMoviesActivity.class);
            intent.putExtra("genre_id", genre.getId());
            intent.putExtra("genre_name", genre.getName());
            context.startActivity(intent);
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
            // Initialize the button in the ViewHolder
            genreButton = itemView.findViewById(R.id.textGenreName);  // Ensure this is a Button in XML
        }
    }
}
