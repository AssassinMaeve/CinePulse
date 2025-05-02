package com.example.cinepulse.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Movie object with properties such as id, title, poster path, overview, and release date.
 * The class is immutable to ensure data consistency and thread safety.
 */
public final class Movie {

    // Unique identifier for the movie
    @SerializedName("id")
    private final int id;

    // Title of the movie, which may be the same as the name for TV shows
    @SerializedName(value = "title", alternate = {"name"})
    private final String title;

    // Path to the movie's poster image
    @SerializedName("poster_path")
    private final String posterPath;

    // A brief description of the movie
    @SerializedName("overview")
    private final String overview;

    // Release date of the movie or first air date for TV shows
    @SerializedName(value = "release_date", alternate = {"first_air_date"})
    private final String releaseDate;

    /**
     * Constructor for initializing the Movie object.
     *
     * @param id The unique identifier of the movie.
     * @param title The title of the movie (or name of a TV show).
     * @param posterPath The path to the movie's poster image.
     * @param overview A brief description of the movie.
     * @param releaseDate The release date of the movie (or first air date for TV shows).
     */
    public Movie(int id, String title, String posterPath, String overview, String releaseDate) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("title cannot be null or empty");
        }
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    /**
     * Returns the unique identifier for the movie.
     *
     * @return The movie's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the title of the movie (or name of the TV show).
     *
     * @return The title or name.
     */
    public String getTitle() {
        return title;
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
     * Optional: Override toString() for easier debugging/logging.
     *
     * @return A string representation of the Movie object.
     */
    @NonNull
    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
