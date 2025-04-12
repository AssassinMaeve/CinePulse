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
import com.example.cinepulse.models.TVDetail;
import com.example.cinepulse.models.TvShow;

import java.util.List;

public class TvShowAdapter extends RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder> {
    private final Context context;
    private final List<TvShow> tvShowList;

    // Remove the 'type' parameter since this is TV-specific adapter
    public TvShowAdapter(Context context, List<TvShow> tvShows, String tv) {
        this.context = context;
        this.tvShowList = tvShows;
    }

    @NonNull
    @Override
    public TvShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tv_show, parent, false);
        return new TvShowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TvShowViewHolder holder, int position) {
        TvShow tvShow = tvShowList.get(position);

        holder.title.setText(tvShow.getName());

        // Add placeholder and error handling for Glide
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + tvShow.getPosterPath())
                .placeholder(R.drawable.cinepulsewhitelogo) // Add a placeholder
                .error(R.drawable.cinepulsewhitelogo) // Add an error image
                .into(holder.poster);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TVDetail.class);
            intent.putExtra("tv_id", tvShow.getId());
            context.startActivity(intent);
        });
    }

    // Add method to update data
    public void updateData(List<TvShow> newShows) {
        tvShowList.clear();
        tvShowList.addAll(newShows);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tvShowList.size();
    }

    public static class TvShowViewHolder extends RecyclerView.ViewHolder {
        final ImageView poster;
        final TextView title;

        public TvShowViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
        }
    }
}
