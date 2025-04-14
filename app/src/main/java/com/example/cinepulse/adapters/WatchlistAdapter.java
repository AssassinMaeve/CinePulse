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
import com.example.cinepulse.models.WatchlistItem;
import com.example.cinepulse.utils.WatchlistManager;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private Context context;
    private List<WatchlistItem> itemList;

    public WatchlistAdapter(Context context, List<WatchlistItem> itemList) {
        this.context = context;
        this.itemList = itemList;
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
        holder.title.setText(item.getTitle());
        Glide.with(context).load("https://image.tmdb.org/t/p/w500" + item.getPosterPath()).into(holder.poster);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetails.class);
            intent.putExtra(item.getType().equals("movie") ? "movie_id" : "tv_show_id", item.getId());
            intent.putExtra("type", item.getType());
            context.startActivity(intent);
        });

        holder.removeButton.setVisibility(View.VISIBLE);
        holder.removeButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                WatchlistItem toRemove = itemList.get(currentPosition);
                WatchlistManager.removeFromWatchlist(context, toRemove.getId());
                itemList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, itemList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;
        ImageView removeButton; // 🛠️ Add this!

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
            removeButton = itemView.findViewById(R.id.removeButton); // 🛠️ Initialize here
        }
    }
}
