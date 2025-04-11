package com.example.cinepulse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.GenreMoviesActivity;
import com.example.cinepulse.R;
import com.example.cinepulse.models.Genre;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    private final Context context;
    private final List<Genre> genres;

    public GenreAdapter(Context context, List<Genre> genres) {
        this.context = context;
        this.genres = genres;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.genreText.setText(genre.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GenreMoviesActivity.class);
            intent.putExtra("genre_id", genre.getId());
            intent.putExtra("genre_name", genre.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView genreText;

        ViewHolder(View itemView) {
            super(itemView);
            genreText = itemView.findViewById(R.id.textGenreName);
        }
    }
}
