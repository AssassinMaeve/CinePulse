package com.example.cinepulse.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.R;
import com.example.cinepulse.models.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private List<Review> reviewList;

    // Constructor to initialize context and review list
    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    // Create a new ViewHolder for each review item
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_review layout to create a new view
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    // Bind data to the ViewHolder
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        // Check if the review list is empty
        if (reviewList.isEmpty()) {
            // Display "No reviews available" message if the list is empty
            holder.authorTextView.setVisibility(View.GONE);
            holder.contentTextView.setText("No reviews available");
            holder.contentTextView.setVisibility(View.VISIBLE);
        } else {
            // Get the review at the current position
            Review review = reviewList.get(position);

            // Show the author and content if the review list is not empty
            holder.authorTextView.setVisibility(View.VISIBLE);
            holder.contentTextView.setVisibility(View.VISIBLE);

            // Display the review author and content
            holder.authorTextView.setText("By: " + review.getAuthor());
            holder.contentTextView.setText(review.getContent());

            // Optional: Limit content length to avoid overflow
            if (review.getContent().length() > 500) {
                holder.contentTextView.setText(review.getContent().substring(0, 500) + "...");
            }
        }
    }

    // Return the total number of reviews or 1 if the list is empty (to show "No reviews available")
    @Override
    public int getItemCount() {
        return reviewList.isEmpty() ? 1 : reviewList.size();
    }

    // ViewHolder class for holding references to the views in each review item
    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView;  // For displaying the review author
        TextView contentTextView; // For displaying the review content
        LinearLayout reviewContainer; // Container for review items

        // Constructor to initialize the views
        public ReviewViewHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.author_text_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
            reviewContainer = itemView.findViewById(R.id.review_container);
        }
    }

    // Method to update the review list and notify the adapter
    @SuppressLint("NotifyDataSetChanged")
    public void setReviews(List<Review> reviews) {
        this.reviewList = reviews; // Update the review list
        notifyDataSetChanged();    // Refresh the adapter
    }

}
