package com.example.cinepulse.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.cinepulse.R;
import com.example.cinepulse.fragments.MovieDetailsFragment;
import com.example.cinepulse.fragments.TvShowDetailsFragment;
import com.example.cinepulse.models.WatchlistItem;
import com.example.cinepulse.utils.WatchlistManager;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private final AppCompatActivity activity;
    private final List<WatchlistItem> itemList;
    private final OnWatchlistChangeListener listener;

    public interface OnWatchlistChangeListener {
        void onWatchlistChanged();
    }

    public WatchlistAdapter(AppCompatActivity activity, List<WatchlistItem> itemList, OnWatchlistChangeListener listener) {
        this.activity = activity;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WatchlistItem item = itemList.get(position);

        holder.title.setText(item.getTitle());

        Glide.with(activity)
                .load("https://image.tmdb.org/t/p/w500" + item.getPosterPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.circle_background_foreground)
                .error(R.drawable.circle_background_foreground)
                .into(holder.poster);

        holder.removeButton.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(v -> openDetailsFragment(item));
        holder.removeButton.setOnClickListener(v -> removeItem(holder.getAdapterPosition(), item));
    }

    private void openDetailsFragment(WatchlistItem item) {
        Fragment fragment;

        switch (item.getType().toLowerCase()) {
            case "movie":
                fragment = MovieDetailsFragment.newInstance(item.getId());
                break;
            case "tv":
                fragment = TvShowDetailsFragment.newInstance(item.getId(), "tv");
                break;
            default:
                Toast.makeText(activity, "Unknown type: " + item.getType(), Toast.LENGTH_SHORT).show();
                return;
        }

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void removeItem(int position, WatchlistItem item) {
        if (position != RecyclerView.NO_POSITION && item != null) {
            WatchlistManager.removeFromWatchlist(activity, item.getId());
            itemList.remove(position);
            notifyItemRemoved(position);

            if (listener != null) {
                listener.onWatchlistChanged();
            }

            Toast.makeText(activity, "Removed from watchlist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
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

            if (poster == null || title == null || removeButton == null) {
                throw new IllegalStateException("Missing required views in layout");
            }
        }
    }
}
