package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UpcomingResponse {

    @SerializedName("results")
    private List<MediaItem> results;

    public List<MediaItem> getResults() {
        return results;
    }

    public void setResults(List<MediaItem> results) {
        this.results = results;
    }
}
