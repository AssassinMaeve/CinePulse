package com.example.cinepulse.models;

import java.util.List;

public class MultiSearchResponse {
    private List<MediaItem> results;

    public List<MediaItem> getResults() {
        return results;
    }

    public void setResults(List<MediaItem> results) {
        this.results = results;
    }
}
