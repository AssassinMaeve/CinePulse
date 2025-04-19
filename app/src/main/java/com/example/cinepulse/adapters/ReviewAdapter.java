package com.example.cinepulse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.R;
import com.example.cinepulse.models.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item_review layout for each review
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        // Check if the list is empty and display the "No reviews available" text
        if (reviewList.isEmpty()) {
            holder.authorTextView.setVisibility(View.GONE);
            holder.contentTextView.setText("No reviews available");
            holder.contentTextView.setVisibility(View.VISIBLE);
        } else {
            // Get the review object at the specified position
            Review review = reviewList.get(position);
            holder.authorTextView.setVisibility(View.VISIBLE);
            holder.contentTextView.setVisibility(View.VISIBLE);

            holder.authorTextView.setText("By: " + review.getAuthor());
            holder.contentTextView.setText(review.getContent());

            // Limit content length to avoid overflow (optional)
            if (review.getContent().length() > 500) {
                holder.contentTextView.setText(review.getContent().substring(0, 500) + "...");
            }
        }
    }

    @Override
    public int getItemCount() {
        // Return the count based on the review list size
        return reviewList.isEmpty() ? 1 : reviewList.size(); // Ensure at least 1 item is displayed for "No reviews available"
    }

    // ViewHolder class to hold the views for each review
    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView;
        TextView contentTextView;
        LinearLayout reviewContainer;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            // Initialize the TextViews for author and content
            authorTextView = itemView.findViewById(R.id.author_text_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
            reviewContainer = itemView.findViewById(R.id.review_container);
        }
    }

    // Method to update the review list and refresh the adapter
    public void setReviews(List<Review> reviews) {
        this.reviewList = reviews; // Update the list
        notifyDataSetChanged();
    }

    // Optional: Add a method to handle when no reviews are available
    public void setNoReviewsAvailable() {
        // Clear the reviews and notify the adapter that there's no data to show
        this.reviewList.clear();
        notifyDataSetChanged();
    }
}
