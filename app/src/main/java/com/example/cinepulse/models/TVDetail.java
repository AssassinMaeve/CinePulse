package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TVDetail {

    private int id;

    private String name;

    @SerializedName("first_air_date")
    private String firstAirDate;

    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("vote_average")
    private float voteAverage;

    private List<Genre> genres;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public List<Genre> getGenres() {
        return genres;
    }
}
