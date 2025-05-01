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

    private final Context context;
    private final List<StreamingProvider> streamingProviders;

    public StreamingProviderAdapter(Context context, List<StreamingProvider> streamingProviders) {
        this.context = context;
        // Avoid null pointer exception by assigning an empty list if null
        this.streamingProviders = streamingProviders != null ? streamingProviders : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the streaming provider item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_streaming, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StreamingProvider provider = streamingProviders.get(position);
        if (provider == null) return;

        // Load provider logo using Glide
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500/" + provider.getLogoPath())
                .placeholder(R.drawable.circle_background_foreground) // Placeholder while loading
                .error(R.drawable.circle_background_foreground)       // Fallback image on error
                .circleCrop()
                .into(holder.providerImage);

        // Set provider name
        holder.providerName.setText(provider.getProviderName());

        // Set click listener to open provider’s website
        holder.itemView.setOnClickListener(v -> {
            String url = getProviderUrl(provider.getProviderName());

            if (url != null) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Unable to open website", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Website not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return streamingProviders.size();
    }

    // ViewHolder pattern for efficient view recycling
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView providerImage;
        TextView providerName;

        public ViewHolder(View itemView) {
            super(itemView);
            providerImage = itemView.findViewById(R.id.imageProvider);
            providerName = itemView.findViewById(R.id.textProviderName);
        }
    }

    /**
     * Maps provider names to their official websites.
     * Prevents hardcoded links scattered across the app.
     */
    private String getProviderUrl(String providerName) {
        if (providerName == null) return null;

        switch (providerName.trim().toLowerCase()) {
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
            case "apple tv plus":
                return "https://tv.apple.com";
            case "zee5":
                return "https://www.zee5.com";
            case "sony liv":
                return "https://www.sonyliv.com";
            case "jio cinema":
            case "jiocinema":
                return "https://www.jiocinema.com";
            case "hotstar":
                return "https://www.hotstar.com";
            default:
                return null;
        }
    }
}
