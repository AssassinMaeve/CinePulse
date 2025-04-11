package com.example.cinepulse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepulse.MovieDetails;
import com.example.cinepulse.R;
import com.example.cinepulse.models.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<Movie> movieList;
    private String type; // "movie" or "tv"

    public MovieAdapter(Context context, List<Movie> movies, String type) {
        this.context = context;
        this.movieList = movies;
        this.type = type;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        // Title: use name if title is null (TV shows)
        String displayTitle = movie.getTitle() != null ? movie.getTitle() : "";
        holder.title.setText(displayTitle);

        String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
        Glide.with(context).load(posterUrl).into(holder.poster);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetails.class);
            intent.putExtra("movie_id", movie.getId());
            intent.putExtra("type", type); // this fixes the issue
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
        }
    }
}
