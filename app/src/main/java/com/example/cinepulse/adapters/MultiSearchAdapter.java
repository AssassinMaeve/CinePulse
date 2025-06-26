package com.example.cinepulse.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepulse.R;
import com.example.cinepulse.fragments.MovieDetailsFragment;
import com.example.cinepulse.fragments.TvShowDetailsFragment;
import com.example.cinepulse.models.MediaItem;

import java.util.List;

public class MultiSearchAdapter extends RecyclerView.Adapter<MultiSearchAdapter.ViewHolder> {

    private final AppCompatActivity activity;
    private final List<MediaItem> mediaItems;

    public MultiSearchAdapter(AppCompatActivity activity, List<MediaItem> mediaItems) {
        this.activity = activity;
        this.mediaItems = mediaItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_movie, parent, false);
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

            Glide.with(activity)
                    .load("https://image.tmdb.org/t/p/w500" + item.getPosterPath())
                    .into(posterImageView);

            itemView.setOnClickListener(v -> launchDetailsFragment(item));
        }

        private void launchDetailsFragment(MediaItem item) {
            Fragment fragment;

            if ("movie".equals(item.getMediaType())) {
                fragment = MovieDetailsFragment.newInstance(item.getId());
            } else if ("tv".equals(item.getMediaType())) {
                fragment = TvShowDetailsFragment.newInstance(item.getId(), "tv");
            } else {
                return; // Unsupported media type
            }

            itemView.post(() -> {
                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();  // avoid crash if state is already saved
                }
            });
        }
    }
}