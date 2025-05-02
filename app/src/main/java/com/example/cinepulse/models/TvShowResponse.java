package com.example.cinepulse.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents the response from the TV show API.
 * Contains pagination details and the list of TV shows in the current page.
 */
public final class TvShowResponse {

    // Current page number in the response
    @SerializedName("page")
    private final int page;

    // List of TV shows in the current page
    @SerializedName("results")
    private final List<TvShow> results;

    // Total number of results available across all pages
    @SerializedName("total_results")
    private final int totalResults;

    // Total number of pages available for pagination
    @SerializedName("total_pages")
    private final int totalPages;

    public TvShowResponse(int page, List<TvShow> results, int totalResults, int totalPages) {
        this.page = page;
        this.results = results;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    // Getter method for the current page number

    // Getter method for the list of TV shows in the current page
    /**
     * Returns the list of TV shows in the current page.
     *
     * @return The list of TV shows.
     */
    public List<TvShow> getResults() {
        return results;
    }

    // Getter method for the total number of results available

    // Getter method for the total number of pages available

    // Optional: Override toString() for logging and debugging
    @NonNull
    @Override
    public String toString() {
        return "TvShowResponse{" +
                "page=" + page +
                ", results=" + results +
                ", totalResults=" + totalResults +
                ", totalPages=" + totalPages +
                '}';
    }
}
