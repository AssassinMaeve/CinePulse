package com.example.cinepulse.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents detailed information about a movie such as title, overview, release date, rating, and poster path.
 * This class is designed to be immutable to ensure data integrity and thread safety.
 */
public final class MovieDetail {

    // The title of the movie
    @SerializedName("title")
    private final String title;

    // A brief description of the movie
    @SerializedName("overview")
    private final String overview;

    // Release date of the movie
    @SerializedName("release_date")
    private final String releaseDate;

    // The average user rating of the movie
    @SerializedName("vote_average")
    private final float rating;

    // Path to the movie's poster image
    @SerializedName("poster_path")
    private final String posterPath;

    // Unique identifier for the movie
    @SerializedName("id")
    private final int id;

    /**
     * Constructor to initialize the MovieDetail object.
     *
     * @param id The unique identifier of the movie.
     * @param title The title of the movie.
     * @param overview A brief description of the movie.
     * @param releaseDate The release date of the movie.
     * @param rating The average rating of the movie.
     * @param posterPath The path to the movie's poster image.
     */
    public MovieDetail(int id, String title, String overview, String releaseDate, float rating, String posterPath) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("title cannot be null or empty");
        }
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.posterPath = posterPath;
    }

    /**
     * Returns the title of the movie.
     *
     * @return The movie's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns a brief description of the movie.
     *
     * @return The movie's overview.
     */
    public String getOverview() {
        return overview;
    }

    /**
     * Returns the release date of the movie.
     *
     * @return The release date.
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Returns the average rating of the movie.
     *
     * @return The movie's rating.
     */
    public float getRating() {
        return rating;
    }

    /**
     * Returns the path to the movie's poster image.
     *
     * @return The poster path.
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * Returns the unique identifier of the movie.
     *
     * @return The movie's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Optional: Override toString() for better logging or debugging.
     *
     * @return A string representation of the MovieDetail object.
     */
    @NonNull
    @Override
    public String toString() {
        return "MovieDetail{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", rating=" + rating +
                ", posterPath='" + posterPath + '\'' +
                '}';
    }
}
