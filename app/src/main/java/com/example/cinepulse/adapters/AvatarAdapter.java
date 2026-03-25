package com.example.cinepulse.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {

    private final List<Integer> avatars;
    private int selectedPosition = -1;
    private final OnAvatarClickListener listener;

    public interface OnAvatarClickListener {
        void onAvatarClick(int resId, int position);
    }

    public AvatarAdapter(List<Integer> avatars, int selectedResId, OnAvatarClickListener listener) {
        this.avatars = avatars;
        this.listener = listener;
        for (int i = 0; i < avatars.size(); i++) {
            if (avatars.get(i) == selectedResId) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_avatar, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        int resId = avatars.get(position);
        holder.imageAvatar.setImageResource(resId);

        // Highlight selected avatar
        if (position == selectedPosition) {
            holder.cardAvatar.setStrokeWidth(4);
        } else {
            holder.cardAvatar.setStrokeWidth(0);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            listener.onAvatarClick(resId, selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return avatars.size();
    }

    public static class AvatarViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardAvatar;
        ImageView imageAvatar;

        public AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAvatar = itemView.findViewById(R.id.cardAvatar);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
        }
    }
}
