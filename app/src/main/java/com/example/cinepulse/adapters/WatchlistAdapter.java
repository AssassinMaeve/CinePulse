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

    private final Context context;
    private List<WatchlistItem> itemList;
    private final OnWatchlistChangeListener listener;

    public interface OnWatchlistChangeListener {
        void onWatchlistChanged();
    }

    public WatchlistAdapter(Context context, List<WatchlistItem> itemList, OnWatchlistChangeListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WatchlistItem item = itemList.get(position);

        // Bind data to views
        holder.title.setText(item.getTitle());
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + item.getPosterPath())
                .into(holder.poster);

        // Show remove button (since this is the watchlist)
        holder.removeButton.setVisibility(View.VISIBLE);

        // Set click listeners
        holder.itemView.setOnClickListener(v -> openDetails(item));
        holder.removeButton.setOnClickListener(v -> removeItem(holder.getAdapterPosition(), item));
    }

    private void openDetails(WatchlistItem item) {
        Intent intent = new Intent(context, MovieDetails.class);
        intent.putExtra("movie_id", item.getId());
        intent.putExtra("type", item.getType());
        context.startActivity(intent);
    }

    private void removeItem(int position, WatchlistItem item) {
        if (position != RecyclerView.NO_POSITION) {
            // Remove from storage
            WatchlistManager.removeFromWatchlist(context, item.getId());

            // Remove from local list
            itemList.remove(position);
            notifyItemRemoved(position);

            // Notify parent activity
            if (listener != null) {
                listener.onWatchlistChanged();
            }

            // Show feedback
            Toast.makeText(context, "Removed from watchlist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateData(List<WatchlistItem> newItems) {
        this.itemList = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;
        ImageView removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
            removeButton = itemView.findViewById(R.id.removeButton);

            // Verify all views are properly initialized
            if (poster == null || title == null || removeButton == null) {
                throw new IllegalStateException("Missing required views in layout");
            }
        }
    }
}