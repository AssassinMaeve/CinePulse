package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a TV show with details like ID, name, poster, overview, rating, and first air date.
 * This class is used to deserialize the TV show data response from the API.
 */
public final class TvShow {

    // Unique identifier for the TV show
    @SerializedName("id")
    private int id;

    // Name of the TV show
    @SerializedName("name")
    private String name;

    // Path to the poster image for the TV show
    @SerializedName("poster_path")
    private String posterPath;

    // Overview or synopsis of the TV show
    @SerializedName("overview")
    private String overview;

    // Average vote rating for the TV show
    @SerializedName("vote_average")
    private float voteAverage;

    // Date when the TV show first aired
    @SerializedName("first_air_date")
    private String firstAirDate;

    // Getter method for the unique identifier of the TV show
    /**
     * Returns the unique identifier of the TV show.
     *
     * @return The ID of the TV show.
     */
    public int getId() {
        return id;
    }

    // Getter method for the name of the TV show
    /**
     * Returns the name of the TV show.
     *
     * @return The name of the TV show.
     */
    public String getName() {
        return name;
    }

    // Getter method for the poster path of the TV show
    /**
     * Returns the path to the poster image of the TV show.
     *
     * @return The poster path of the TV show.
     */
    public String getPosterPath() {
        return posterPath;
    }

    // Getter method for the overview of the TV show
    /**
     * Returns the overview or synopsis of the TV show.
     *
     * @return The overview of the TV show.
     */
    public String getOverview() {
        return overview;
    }

    // Getter method for the average vote rating of the TV show
    /**
     * Returns the average vote rating for the TV show.
     *
     * @return The vote average for the TV show.
     */
    public float getVoteAverage() {
        return voteAverage;
    }

    // Getter method for the first air date of the TV show
    /**
     * Returns the first air date of the TV show.
     *
     * @return The first air date of the TV show.
     */
    public String getFirstAirDate() {
        return firstAirDate;
    }

    // Optional: Override toString() for better logging or debugging
    @Override
    public String toString() {
        return "TvShow{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", overview='" + overview + '\'' +
                ", voteAverage=" + voteAverage +
                ", firstAirDate='" + firstAirDate + '\'' +
                '}';
    }
}
