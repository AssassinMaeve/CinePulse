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

    private Context context;
    private List<Trailer> trailers;
    private OnTrailerClickListener listener;

    public interface OnTrailerClickListener {
        void onTrailerClick(Trailer trailer);
    }

    public TrailerAdapter(Context context, List<Trailer> trailers, OnTrailerClickListener listener) {
        this.context = context;
        this.trailers = trailers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);

        holder.trailerTitle.setText(trailer.getName());
        Glide.with(context)
                .load("https://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg")
                .into(holder.trailerThumbnail);

        holder.itemView.setOnClickListener(v -> listener.onTrailerClick(trailer));
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public static class TrailerViewHolder extends RecyclerView.ViewHolder {
        ImageView trailerThumbnail;
        TextView trailerTitle;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerThumbnail = itemView.findViewById(R.id.trailerThumbnail);
            trailerTitle = itemView.findViewById(R.id.trailerTitle);
        }
    }
}