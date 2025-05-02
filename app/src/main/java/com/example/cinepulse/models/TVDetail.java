package com.example.cinepulse.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents detailed information about a TV show, including its genres, overview,
 * release date, and rating. This class is used to deserialize the TV show details response
 * from the API.
 */
public final class TVDetail {

    // Unique identifier for the TV show
    private final int id;

    // Name of the TV show
    private final String name;

    // Date when the TV show first aired
    @SerializedName("first_air_date")
    private final String firstAirDate;

    // Overview or synopsis of the TV show
    private final String overview;

    // Path to the poster image for the TV show
    @SerializedName("poster_path")
    private final String posterPath;

    // Average vote rating for the TV show
    @SerializedName("vote_average")
    private final float voteAverage;

    // List of genres associated with the TV show
    private final List<Genre> genres;

    public TVDetail(int id, String name, String firstAirDate, String overview, String posterPath, float voteAverage, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.firstAirDate = firstAirDate;
        this.overview = overview;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.genres = genres;
    }

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

    // Getter method for the first air date of the TV show
    /**
     * Returns the first air date of the TV show.
     *
     * @return The first air date of the TV show.
     */
    public String getFirstAirDate() {
        return firstAirDate;
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

    // Getter method for the poster path of the TV show
    /**
     * Returns the path to the poster image of the TV show.
     *
     * @return The poster path of the TV show.
     */
    public String getPosterPath() {
        return posterPath;
    }

    // Getter method for the average vote rating of the TV show

    // Getter method for the list of genres associated with the TV show

    // Optional: Override toString() for better logging or debugging
    @NonNull
    @Override
    public String toString() {
        return "TVDetail{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", firstAirDate='" + firstAirDate + '\'' +
                ", overview='" + overview + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", voteAverage=" + voteAverage +
                ", genres=" + genres +
                '}';
    }
}
