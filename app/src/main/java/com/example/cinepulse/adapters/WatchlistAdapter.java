package com.example.cinepulse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.cinepulse.MovieDetails;
import com.example.cinepulse.R;
import com.example.cinepulse.TVDetailActivity;
import com.example.cinepulse.models.WatchlistItem;
import com.example.cinepulse.utils.WatchlistManager;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private final Context context;
    private final List<WatchlistItem> itemList;
    private final OnWatchlistChangeListener listener;

    // Interface to notify external listener when watchlist changes
    public interface OnWatchlistChangeListener {
        void onWatchlistChanged();
    }

    public WatchlistAdapter(Context context, List<WatchlistItem> itemList, OnWatchlistChangeListener listener) {
        // Use application context to prevent memory leaks
        this.context = context.getApplicationContext();
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate view efficiently
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WatchlistItem item = itemList.get(position);

        // Set movie/TV title
        holder.title.setText(item.getTitle());

        // Load image with Glide using disk caching and fallbacks
        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + item.getPosterPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and resized images
                .placeholder(R.drawable.circle_background_foreground) // Optional: show while loading
                .error(R.drawable.circle_background_foreground) // Optional: fallback if failed
                .into(holder.poster);

        // Show remove button
        holder.removeButton.setVisibility(View.VISIBLE);

        // Click listeners
        holder.itemView.setOnClickListener(v -> openDetails(item));
        holder.removeButton.setOnClickListener(v -> removeItem(holder.getAdapterPosition(), item));
    }

    // Launch the correct activity based on item type
    private void openDetails(WatchlistItem item) {
        Intent intent;

        switch (item.getType().toLowerCase()) {
            case "movie":
                intent = new Intent(context, MovieDetails.class);
                intent.putExtra("movie_id", item.getId());
                intent.putExtra("type", "movie");
                break;

            case "tv":
                intent = new Intent(context, TVDetailActivity.class);
                intent.putExtra("tv_id", item.getId());
                intent.putExtra("type", "tv");
                break;

            default:
                Toast.makeText(context, "Unknown type: " + item.getType(), Toast.LENGTH_SHORT).show();
                return;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Required when using application context
        context.startActivity(intent);
    }

    // Remove an item from watchlist safely
    private void removeItem(int position, WatchlistItem item) {
        if (position != RecyclerView.NO_POSITION && item != null) {
            WatchlistManager.removeFromWatchlist(context, item.getId()); // Persistent removal
            itemList.remove(position); // Remove from adapter list
            notifyItemRemoved(position); // Notify adapter

            if (listener != null) {
                listener.onWatchlistChanged();
            }

            Toast.makeText(context, "Removed from watchlist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    // ViewHolder holds references to the views for one item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;
        ImageView removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
            removeButton = itemView.findViewById(R.id.removeButton);

            // Fail-fast check in case view IDs are missing
            if (poster == null || title == null || removeButton == null) {
                throw new IllegalStateException("Missing required views in layout");
            }
        }
    }
}
