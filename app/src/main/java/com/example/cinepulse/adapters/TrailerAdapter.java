package com.example.cinepulse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepulse.R;
import com.example.cinepulse.models.Trailer;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    // Context for inflating views and Glide
    private final Context context;
    // List of trailers to display
    private final List<Trailer> trailers;
    // Listener to handle trailer click events
    private final OnTrailerClickListener listener;

    // Interface for handling trailer item clicks
    public interface OnTrailerClickListener {
        void onTrailerClick(Trailer trailer);
    }

    // Constructor to initialize the adapter with context, trailers list, and listener
    public TrailerAdapter(Context context, List<Trailer> trailers, OnTrailerClickListener listener) {
        this.context = context;
        this.trailers = trailers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the trailer_item layout to create a new view
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);

        // Set the title of the trailer
        holder.title.setText(trailer.getName());

        // Construct the URL for the trailer thumbnail using the YouTube video key
        String thumbnailUrl = "https://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";

        // Use Glide to load the thumbnail image into the ImageView
        Glide.with(context)
                .load(thumbnailUrl)
                .into(holder.thumbnail);

        // Set the item click listener to handle trailer clicks
        holder.itemView.setOnClickListener(v -> listener.onTrailerClick(trailer));
    }

    @Override
    public int getItemCount() {
        return trailers.size(); // Return the size of the trailers list
    }

    // ViewHolder class to hold references to the views in each trailer item
    public static class TrailerViewHolder extends RecyclerView.ViewHolder {

        TextView title; // For displaying the trailer title
        ImageView thumbnail; // For displaying the trailer thumbnail image

        // Constructor to initialize the views
        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the views from the item layout
            title = itemView.findViewById(R.id.title); // Ensure the ID matches your XML
            thumbnail = itemView.findViewById(R.id.thumbnail); // Ensure the ID matches your XML
        }
    }
}
