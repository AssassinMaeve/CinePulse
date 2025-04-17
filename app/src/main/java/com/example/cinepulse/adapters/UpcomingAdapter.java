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
import com.example.cinepulse.models.MediaItem;

import java.util.List;

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MediaItem item);
    }

    private Context context;
    private List<MediaItem> itemList;
    private OnItemClickListener listener;

    public UpcomingAdapter(Context context, List<MediaItem> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaItem item = itemList.get(position);
        holder.title.setText(item.getDisplayTitle());

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + item.getPosterPath())
                .into(holder.poster);

        // Set click listener here
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
        }
    }
}

