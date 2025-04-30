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
import com.example.cinepulse.R;
import com.example.cinepulse.TVDetailActivity;
import com.example.cinepulse.models.TvShow;

import java.util.List;

public class TvShowAdapter extends RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder> {

    // Context to inflate views and use Glide for image loading
    private final Context context;
    // List to hold TV show data
    private final List<TvShow> tvShowList;

    // Constructor to initialize the context and the TV shows list
    public TvShowAdapter(Context context, List<TvShow> tvShows) {
        this.context = context;
        this.tvShowList = tvShows;
    }

    @NonNull
    @Override
    public TvShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_tv_show layout for each TV show item
        View view = LayoutInflater.from(context).inflate(R.layout.item_tv_show, parent, false);
        return new TvShowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TvShowViewHolder holder, int position) {
        // Get the TV show object at the given position
        TvShow tvShow = tvShowList.get(position);

        // Set the title of the TV show in the TextView
        holder.title.setText(tvShow.getName());

        // Use Glide to load the poster image into the ImageView
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + tvShow.getPosterPath())  // Construct the full image URL
                .into(holder.poster);

        // Set a click listener on the item view to navigate to the TVDetailActivity
        holder.itemView.setOnClickListener(v -> {
            // Create an Intent to start the TVDetailActivity
            Intent intent = new Intent(context, TVDetailActivity.class);
            // Pass the TV show ID to the next activity
            intent.putExtra("tv_id", tvShow.getId());
            context.startActivity(intent);  // Start the activity
        });
    }

    @Override
    public int getItemCount() {
        return tvShowList.size(); // Return the size of the TV shows list
    }

    // ViewHolder class to hold the views for each TV show item
    public static class TvShowViewHolder extends RecyclerView.ViewHolder {
        // Views to display the poster image and title of the TV show
        ImageView poster;
        TextView title;

        public TvShowViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views by finding them in the layout
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
        }
    }
}
