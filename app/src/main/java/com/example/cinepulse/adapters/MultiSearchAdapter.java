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
import com.example.cinepulse.TVDetailActivity;
import com.example.cinepulse.models.MediaItem;

import java.util.List;

public class MultiSearchAdapter extends RecyclerView.Adapter<MultiSearchAdapter.ViewHolder> {

    private final Context context;
    private final List<MediaItem> mediaItems;

    public MultiSearchAdapter(Context context, List<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaItem item = mediaItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView posterImageView;
        private final TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.imagePoster);
            titleTextView = itemView.findViewById(R.id.textTitle);
        }

        public void bind(MediaItem item) {
            titleTextView.setText(item.getDisplayTitle());

            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" + item.getPosterPath())
                    .into(posterImageView);

            // Set the click listener for the item
            itemView.setOnClickListener(v -> launchDetailsActivity(item));
        }

        // Handle the logic of navigating to the correct details screen
        private void launchDetailsActivity(MediaItem item) {
            Intent intent;

            if ("movie".equals(item.getMediaType())) {
                // Navigate to MovieDetails if it's a movie
                intent = new Intent(context, MovieDetails.class);
                intent.putExtra("movie_id", item.getId());
            } else if ("tv".equals(item.getMediaType())) {
                // Navigate to TVDetailActivity if it's a TV show
                intent = new Intent(context, TVDetailActivity.class);
                intent.putExtra("tv_id", item.getId()); // Pass TV show ID
            } else {
                return; // If the media type is not recognized, do nothing
            }

            intent.putExtra("type", item.getMediaType());
            context.startActivity(intent);
        }
    }
}
