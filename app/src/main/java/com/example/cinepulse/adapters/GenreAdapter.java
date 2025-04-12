// GenreAdapter.java

package com.example.cinepulse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.GenreMoviesActivity;
import com.example.cinepulse.R;
import com.example.cinepulse.models.Genre;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private Context context;
    private List<Genre> genreList;

    public GenreAdapter(Context context, List<Genre> genreList) {
        this.context = context;
        this.genreList = genreList;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genreList.get(position);
        holder.button.setText(genre.getName());
        holder.button.setOnClickListener(v -> {
            Intent intent = new Intent(context, GenreMoviesActivity.class);
            intent.putExtra("genre_id", genre.getId());
            intent.putExtra("genre_name", genre.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.textGenreName);  // Make sure this ID is a Button in XML
        }
    }
}
