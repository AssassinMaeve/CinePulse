package com.example.cinepulse.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a review from the API response.
 * Each review contains information about the author and the content of the review.
 */
public final class Review {

    // The author of the review
    @SerializedName("author")
    private final String author;

    // The content of the review
    @SerializedName("content")
    private final String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    /**
     * Returns the name of the author of the review.
     *
     * @return The author's name.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the content of the review.
     *
     * @return The review content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Optional: Override toString for better logging or debugging.
     * Provides a string representation of the review.
     *
     * @return A string representation of the review.
     */
    @NonNull
    @Override
    public String toString() {
        return "Review{" +
                "author='" + author + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
