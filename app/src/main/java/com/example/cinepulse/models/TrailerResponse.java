package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private List<Trailer> results;

    public int getId() {
        return id;
    }

    public List<Trailer> getResults() {
        return results;
    }
}
