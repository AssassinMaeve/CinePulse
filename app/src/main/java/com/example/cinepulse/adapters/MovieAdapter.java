package com.example.cinepulse.adapters;

import android.annotation.SuppressLint;
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

    private final Context context;
    private final List<Object> itemList;

    public MovieAdapter(Context context, List<Object> items) {
        this.context = context;
        this.itemList = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MOVIE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
            return new MovieViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_tv_show, parent, false);
            return new TvShowViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_MOVIE) {
            Movie movie = (Movie) itemList.get(position);
            ((MovieViewHolder) holder).bindData(movie);
        } else {
            TvShow tvShow = (TvShow) itemList.get(position);
            ((TvShowViewHolder) holder).bindData(tvShow);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position) instanceof Movie ? VIEW_TYPE_MOVIE : VIEW_TYPE_TV_SHOW;
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

        public void bindData(Movie movie) {
            title.setText(movie.getTitle() != null ? movie.getTitle() : "");

            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                    .into(poster);

            itemView.setOnClickListener(v -> {
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

        public void bindData(TvShow tvShow) {
            title.setText(tvShow.getName() != null ? tvShow.getName() : "");

            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" + tvShow.getPosterPath())
                    .into(poster);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, MovieDetails.class);
                intent.putExtra("tv_show_id", tvShow.getId());
                intent.putExtra("type", "tv");
                context.startActivity(intent);
            });
        }
    }

    // Method to update data
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Object> newItems) {
        itemList.clear();
        itemList.addAll(newItems);
        notifyDataSetChanged();  // Can consider DiffUtil for more efficient updates
    }
}
