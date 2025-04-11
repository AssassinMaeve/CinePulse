package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CastResponse {

    @SerializedName("cast")
    private List<Cast> cast;

    public List<Cast> getCast() {
        return cast;
    }
}
