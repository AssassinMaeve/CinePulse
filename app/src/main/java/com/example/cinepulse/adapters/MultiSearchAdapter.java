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
    public MultiSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiSearchAdapter.ViewHolder holder, int position) {
        MediaItem item = mediaItems.get(position);
        holder.titleTextView.setText(item.getDisplayTitle());

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + item.getPosterPath())
                .placeholder(R.drawable.profile_user)
                .into(holder.posterImageView);

        // 🚀 Click listener to launch details activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetails.class);
            intent.putExtra("movie_id", item.getId());
            intent.putExtra("type", item.getMediaType());

            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.imagePoster);
            titleTextView = itemView.findViewById(R.id.textTitle);
        }
    }
}
