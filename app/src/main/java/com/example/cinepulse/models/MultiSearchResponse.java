package com.example.cinepulse.models;

import java.util.List;

/**
 * Represents the response from the API when performing a multi-search.
 * This class holds a list of MediaItem objects contained in the "results" key of the JSON response.
 */
public final class MultiSearchResponse {

    // The list of search results returned by the API
    private List<MediaItem> results;

    /**
     * Returns the list of media items from the multi-search API response.
     *
     * @return The list of MediaItem objects.
     */
    public List<MediaItem> getResults() {
        return results;
    }

}
