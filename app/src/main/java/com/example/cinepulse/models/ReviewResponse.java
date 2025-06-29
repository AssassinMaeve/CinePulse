package com.example.cinepulse.models;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Represents the response from the API containing a list of reviews.
 * This class is used to deserialize the "results" array in the JSON response.
 */
public final class ReviewResponse {

    // A list of reviews obtained from the API
    private final List<Review> results;

    public ReviewResponse(List<Review> results) {
        this.results = results;
    }

    /**
     * Returns the list of reviews.
     *
     * @return A list of Review objects.
     */
    public List<Review> getResults() {
        return results;
    }

    /**
     * Optional: Override toString for better logging or debugging.
     * Provides a string representation of the ReviewResponse.
     *
     * @return A string representation of the ReviewResponse.
     */
    @NonNull
    @Override
    public String toString() {
        return "ReviewResponse{" +
                "results=" + results +
                '}';
    }
}
