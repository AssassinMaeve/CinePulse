package com.example.cinepulse.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinepulse.R;
import com.example.cinepulse.models.StreamingProvider;

import java.util.ArrayList;
import java.util.List;

public class StreamingProviderAdapter extends RecyclerView.Adapter<StreamingProviderAdapter.ViewHolder> {

    private Context context;
    private List<StreamingProvider> streamingProviders;

    public StreamingProviderAdapter(Context context, List<StreamingProvider> streamingProviders) {
        this.context = context;
        this.streamingProviders = streamingProviders != null ? streamingProviders : new ArrayList<>(); // Prevent NullPointer
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_streaming, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StreamingProvider provider = streamingProviders.get(position);

        if (provider != null) {
            // Set provider logo
            if (provider.getLogoPath() != null) {
                Glide.with(context)
                        .load("https://image.tmdb.org/t/p/w500/" + provider.getLogoPath())
                        .circleCrop()
                        .into(holder.providerImage);
            } else {
                holder.providerImage.setImageResource(R.drawable.circle_background_foreground);
            }

            // Set provider name
            holder.providerName.setText(provider.getProviderName());

            // Handle click with manual URL mapping
            holder.itemView.setOnClickListener(v -> {
                String url = getProviderUrl(provider.getProviderName());

                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Website not available", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return streamingProviders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView providerImage;
        TextView providerName;

        public ViewHolder(View itemView) {
            super(itemView);
            providerImage = itemView.findViewById(R.id.imageProvider);
            providerName = itemView.findViewById(R.id.textProviderName);
        }
    }

    // Add this method inside the StreamingProviderAdapter class
    private String getProviderUrl(String providerName) {
        if (providerName == null) return null;

        switch (providerName.toLowerCase()) {
            case "netflix":
                return "https://www.netflix.com";
            case "amazon prime video":
            case "prime video":
                return "https://www.primevideo.com";
            case "disney+":
            case "disney plus":
                return "https://www.disneyplus.com";
            case "hbo max":
                return "https://www.hbomax.com";
            case "hulu":
                return "https://www.hulu.com";
            case "apple tv+":
                return "https://tv.apple.com";
            case "zee5":
                return "https://www.zee5.com";
            case "sony liv":
                return "https://www.sonyliv.com";
            case "jio cinema":
                return "https://www.jiocinema.com";
            case "hotstar":
                return "https://www.hotstar.com";
            default:
                return null;
        }
    }
}
