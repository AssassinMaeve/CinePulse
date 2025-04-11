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
import com.example.cinepulse.models.Cast;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private Context context;
    private List<Cast> castList;

    public CastAdapter(Context context, List<Cast> castList) {
        this.context = context;
        this.castList = castList;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cast, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast = castList.get(position);
        holder.nameText.setText(cast.getName());
        holder.characterText.setText(cast.getCharacter());

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w185" + cast.getProfilePath())
                .circleCrop()
                .placeholder(R.drawable.profile_user) // your drawable placeholder
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameText, characterText;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageCast);
            nameText = itemView.findViewById(R.id.textCastName);
            characterText = itemView.findViewById(R.id.textCharacter);
        }
    }
}
