package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents the response from the TV show API.
 * Contains pagination details and the list of TV shows in the current page.
 */
public final class TvShowResponse {

    // Current page number in the response
    @SerializedName("page")
    private int page;

    // List of TV shows in the current page
    @SerializedName("results")
    private List<TvShow> results;

    // Total number of results available across all pages
    @SerializedName("total_results")
    private int totalResults;

    // Total number of pages available for pagination
    @SerializedName("total_pages")
    private int totalPages;

    // Getter method for the current page number
    /**
     * Returns the current page number.
     *
     * @return The current page number.
     */
    public int getPage() {
        return page;
    }

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
    /**
     * Returns the total number of TV shows available across all pages.
     *
     * @return The total number of TV shows.
     */
    public int getTotalResults() {
        return totalResults;
    }

    // Getter method for the total number of pages available
    /**
     * Returns the total number of pages available for pagination.
     *
     * @return The total number of pages.
     */
    public int getTotalPages() {
        return totalPages;
    }

    // Optional: Override toString() for logging and debugging
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
