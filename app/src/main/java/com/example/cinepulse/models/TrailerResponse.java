package com.example.cinepulse.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents the response object for trailers, which includes a list of trailer objects.
 * This class is used to deserialize the trailer response from the API.
 */
public final class TrailerResponse {

    // Unique identifier for the trailer response (usually refers to the movie or TV show ID)
    @SerializedName("id")
    private final int id;

    // List of trailer objects associated with the movie or TV show
    @SerializedName("results")
    private final List<Trailer> results;

    public TrailerResponse(int id, List<Trailer> results) {
        this.id = id;
        this.results = results;
    }

    // Getter method for the ID of the response

    /**
     * Returns the unique identifier of the trailer response.
     * This ID typically corresponds to the movie or TV show ID.
     *
     * @return The ID of the trailer response.
     */
    public int getId() {
        return id;
    }

    // Getter method for the list of trailers

    /**
     * Returns the list of trailers associated with the movie or TV show.
     *
     * @return A list of Trailer objects.
     */
    public List<Trailer> getResults() {
        return results;
    }

    // Optional: Override toString() for better logging or debugging

    @NonNull
    @Override
    public String toString() {
        return "TrailerResponse{" +
                "id=" + id +
                ", results=" + results +
                '}';
    }
}
