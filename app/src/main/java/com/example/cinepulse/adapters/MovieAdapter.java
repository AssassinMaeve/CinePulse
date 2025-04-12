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
import com.example.cinepulse.models.TvShow;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MOVIE = 1;
    private static final int VIEW_TYPE_TV_SHOW = 2;

    private Context context;
    private List<Object> itemList; // Unified list to hold both Movies and TV Shows
    private String type; // "movie" or "tv"

    public MovieAdapter(Context context, List<Object> items, String type) {
        this.context = context;
        this.itemList = items;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MOVIE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
            return new MovieViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tv_show, parent, false);
            return new TvShowViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_MOVIE) {
            Movie movie = (Movie) itemList.get(position);
            MovieViewHolder movieHolder = (MovieViewHolder) holder;
            movieHolder.bind(movie);
        } else {
            TvShow tvShow = (TvShow) itemList.get(position);
            TvShowViewHolder tvShowHolder = (TvShowViewHolder) holder;
            tvShowHolder.bind(tvShow);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Checking the type of the item at the position
        if (itemList.get(position) instanceof Movie) {
            return VIEW_TYPE_MOVIE;
        } else if (itemList.get(position) instanceof TvShow) {
            return VIEW_TYPE_TV_SHOW;
        }
        return super.getItemViewType(position);
    }

    // Movie ViewHolder
    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
        }

        public void bind(Movie movie) {
            // Setting the title and poster for the movie item
            String displayTitle = movie.getTitle() != null ? movie.getTitle() : "";
            title.setText(displayTitle);

            String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
            Glide.with(context).load(posterUrl).into(poster);

            itemView.setOnClickListener(v -> {
                // Launching MovieDetails activity with movie data
                Intent intent = new Intent(context, MovieDetails.class);
                intent.putExtra("movie_id", movie.getId());
                intent.putExtra("type", "movie");
                context.startActivity(intent);
            });
        }
    }

    // TV Show ViewHolder
    class TvShowViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;

        public TvShowViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
        }

        public void bind(TvShow tvShow) {
            // Setting the title and poster for the TV show item
            String displayTitle = tvShow.getName() != null ? tvShow.getName() : "";
            title.setText(displayTitle);

            String posterUrl = "https://image.tmdb.org/t/p/w500" + tvShow.getPosterPath();
            Glide.with(context).load(posterUrl).into(poster);

            itemView.setOnClickListener(v -> {
                // Launching MovieDetails activity with TV show data
                Intent intent = new Intent(context, MovieDetails.class);
                intent.putExtra("tv_show_id", tvShow.getId()); // Pass TV Show ID
                intent.putExtra("type", "tv"); // Specify TV Show type
                context.startActivity(intent);
            });
        }
    }

    // Method to update data
    public void updateData(List<Object> newItems) {
        itemList.clear();
        itemList.addAll(newItems);
        notifyDataSetChanged();
    }
}
