package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryProvider {

    @SerializedName("flatrate")
    private final List<StreamingProvider> flatrate;

    public CountryProvider(List<StreamingProvider> flatrate) {
        this.flatrate = flatrate;
    }

    public List<StreamingProvider> getFlatrate() {
        return flatrate;
    }

}



