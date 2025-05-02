package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class WatchProviderResponse {
    @SerializedName("results")
    private final Map<String, CountryProvider> results;

    public WatchProviderResponse(Map<String, CountryProvider> results) {
        this.results = results;
    }

    public Map<String, CountryProvider> getResults() {
        return results;
    }
}
