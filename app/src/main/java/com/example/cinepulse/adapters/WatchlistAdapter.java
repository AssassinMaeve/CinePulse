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
import com.example.cinepulse.MovieDetails;
import com.example.cinepulse.R;
import com.example.cinepulse.models.WatchlistItem;
import com.example.cinepulse.utils.WatchlistManager;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private final Context context; // Context for UI-related operations like inflating views and starting activities
    private List<WatchlistItem> itemList; // List to hold the watchlist items
    private final OnWatchlistChangeListener listener; // Listener to notify when watchlist changes

    // Interface for notifying changes to the watchlist
    public interface OnWatchlistChangeListener {
        void onWatchlistChanged();
    }

    // Constructor to initialize context, item list, and listener
    public WatchlistAdapter(Context context, List<WatchlistItem> itemList, OnWatchlistChangeListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_movie layout for each movie in the watchlist
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WatchlistItem item = itemList.get(position);

        // Set the movie title and poster image using Glide
        holder.title.setText(item.getTitle());
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + item.getPosterPath()) // Construct image URL
                .into(holder.poster); // Set the image into the ImageView

        // Make the remove button visible since this is the watchlist
        holder.removeButton.setVisibility(View.VISIBLE);

        // Set click listeners for opening details and removing from the watchlist
        holder.itemView.setOnClickListener(v -> openDetails(item));
        holder.removeButton.setOnClickListener(v -> removeItem(holder.getAdapterPosition(), item));
    }

    // Method to open the MovieDetails activity with the selected movie's ID and type
    private void openDetails(WatchlistItem item) {
        Intent intent = new Intent(context, MovieDetails.class);
        intent.putExtra("movie_id", item.getId()); // Pass the movie ID
        intent.putExtra("type", item.getType()); // Pass the movie type
        context.startActivity(intent); // Start the MovieDetails activity
    }

    // Method to remove an item from the watchlist
    private void removeItem(int position, WatchlistItem item) {
        if (position != RecyclerView.NO_POSITION) {
            // Remove from persistent storage (e.g., SharedPreferences, database, etc.)
            WatchlistManager.removeFromWatchlist(context, item.getId());

            // Remove from the local list
            itemList.remove(position);
            notifyItemRemoved(position); // Notify adapter to update UI

            // Notify the parent activity or fragment that the watchlist has changed
            if (listener != null) {
                listener.onWatchlistChanged();
            }

            // Show feedback to the user
            Toast.makeText(context, "Removed from watchlist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size(); // Return the number of items in the watchlist
    }

    // ViewHolder class to hold the views for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster; // ImageView to show the movie poster
        TextView title; // TextView to show the movie title
        ImageView removeButton; // Button to remove the item from the watchlist

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the views by finding them in the layout
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
            removeButton = itemView.findViewById(R.id.removeButton);

            // Verify that all required views are properly initialized to avoid crashes
            if (poster == null || title == null || removeButton == null) {
                throw new IllegalStateException("Missing required views in layout");
            }
        }
    }
}
