// MovieDetails.java
package com.example.cinepulse;

import com.google.gson.annotations.SerializedName;

public class MovieDetails {
    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("vote_average")
    private float rating;

    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getReleaseDate() { return releaseDate; }
    public float getRating() { return rating; }
}
