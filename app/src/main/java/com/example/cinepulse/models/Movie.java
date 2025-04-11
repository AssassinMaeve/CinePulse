package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("id")
    private int id;

    // TMDb uses "title" for movies and "name" for TV shows.
    // This makes "title" take "name" if "title" is missing.
    @SerializedName(value = "title", alternate = {"name"})
    private String title;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("overview")
    private String overview;

    @SerializedName(value = "release_date", alternate = {"first_air_date"})
    private String releaseDate;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
