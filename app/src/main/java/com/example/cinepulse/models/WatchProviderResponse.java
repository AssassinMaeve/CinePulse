package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class WatchProviderResponse {
    @SerializedName("results")
    private Map<String, CountryProvider> results;

    public Map<String, CountryProvider> getResults() {
        return results;
    }
}
