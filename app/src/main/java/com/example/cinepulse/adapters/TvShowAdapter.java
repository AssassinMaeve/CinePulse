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
import com.example.cinepulse.models.TVDetail;
import com.example.cinepulse.models.TvShow;

import java.util.List;

public class TvShowAdapter extends RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder> {
    private Context context;
    private List<TvShow> tvShowList;

    // Remove the 'type' parameter as it's not needed for a TV-specific adapter
    public TvShowAdapter(Context context, List<TvShow> tvShows) {
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

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + tvShow.getPosterPath())
                .into(holder.poster);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TVDetailActivity.class);
            intent.putExtra("tv_id", tvShow.getId());
            context.startActivity(intent);
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
