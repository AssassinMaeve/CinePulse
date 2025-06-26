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
import com.example.cinepulse.fragments.TvShowDetailsFragment; // Ensure this import matches your package
import com.example.cinepulse.models.TvShow;

import java.util.List;

public class TvShowAdapter extends RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder> {

    private final AppCompatActivity activity;
    private final List<TvShow> tvShowList;

    // Constructor
    public TvShowAdapter(AppCompatActivity activity, List<TvShow> tvShows) {
        this.activity = activity;
        this.tvShowList = tvShows;
    }

    @NonNull
    @Override
    public TvShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_tv_show, parent, false);
        return new TvShowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TvShowViewHolder holder, int position) {
        TvShow tvShow = tvShowList.get(position);

        holder.title.setText(tvShow.getName());

        Glide.with(activity)
                .load("https://image.tmdb.org/t/p/w500" + tvShow.getPosterPath())
                .into(holder.poster);

        holder.itemView.setOnClickListener(v -> {
            Fragment fragment = TvShowDetailsFragment.newInstance(tvShow.getId(), "tv");
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment) // Ensure this ID exists in your activity layout
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return tvShowList.size();
    }

    public static class TvShowViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;

        public TvShowViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
        }
    }
}
