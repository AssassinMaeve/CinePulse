package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents the response from the API when fetching a list of movies.
 * This class holds a list of Movie objects contained in the "results" key from the JSON response.
 */
public final class MovieResponse {

    // The list of movies returned in the API response
    @SerializedName("results")
    private List<Movie> results;

    /**
     * Returns the list of movies from the API response.
     *
     * @return The list of Movie objects.
     */
    public List<Movie> getResults() {
        return results;
    }

    /**
     * Sets the list of movies in the response.
     * This setter method may be used when manually deserializing the response or testing.
     *
     * @param results The list of Movie objects to set.
     */
    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
