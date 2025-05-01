package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryProvider {
    @SerializedName("link")
    private String link;

    @SerializedName("flatrate")
    private List<StreamingProvider> flatrate;

    public String getLink() {
        return link;
    }

    public List<StreamingProvider> getFlatrate() {
        return flatrate;
    }
}



