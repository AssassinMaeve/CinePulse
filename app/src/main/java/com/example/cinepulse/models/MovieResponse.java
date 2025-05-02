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
    private final List<Movie> results;

    public MovieResponse(List<Movie> results) {
        this.results = results;
    }

    /**
     * Returns the list of movies from the API response.
     *
     * @return The list of Movie objects.
     */
    public List<Movie> getResults() {
        return results;
    }

}
