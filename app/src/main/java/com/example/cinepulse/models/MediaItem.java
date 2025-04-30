package com.example.cinepulse.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a media item (movie, TV show, or person) with properties like title, poster, and release dates.
 * The MediaItem class is immutable to ensure data consistency and thread safety.
 */
public final class MediaItem {

    // The unique identifier for the media item (movie, TV show, person)
    @SerializedName("id")
    private final int id;

    // Type of the media item, e.g., "movie", "tv", "person"
    @SerializedName("media_type")
    private final String mediaType;

    // Title of the movie (if mediaType is "movie")
    @SerializedName("title")
    private final String title;

    // Name of the TV show (if mediaType is "tv")
    @SerializedName("name")
    private final String name;

    // Path to the poster image
    @SerializedName("poster_path")
    private final String posterPath;

    // Release date of the movie
    @SerializedName("release_date")
    private final String releaseDate;

    // First air date of the TV show
    @SerializedName("first_air_date")
    private final String firstAirDate;

    /**
     * Constructor for initializing the MediaItem object.
     *
     * @param id The unique identifier of the media item.
     * @param mediaType The type of media (movie, tv, person).
     * @param title The title of the movie.
     * @param name The name of the TV show.
     * @param posterPath The path to the media item's poster image.
     * @param releaseDate The release date of the movie.
     * @param firstAirDate The first air date of the TV show.
     */
    public MediaItem(int id, String mediaType, String title, String name, String posterPath, String releaseDate, String firstAirDate) {
        if (mediaType == null || mediaType.isEmpty()) {
            throw new IllegalArgumentException("mediaType cannot be null or empty");
        }

        this.id = id;
        this.mediaType = mediaType;
        this.title = title;
        this.name = name;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.firstAirDate = firstAirDate;
    }

    /**
     * Returns the unique identifier for the media item.
     *
     * @return The media item's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the type of media (movie, tv, person).
     *
     * @return The media type.
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Returns the display title. If the media type is "movie", returns the title.
     * If the media type is "tv", returns the name of the TV show.
     *
     * @return The title or name depending on the media type.
     */
    public String getDisplayTitle() {
        // Return title if available, otherwise return name (for TV shows)
        return title != null ? title : name;
    }

    /**
     * Returns the path to the media item's poster image.
     *
     * @return The poster path.
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * Returns the release date of the movie.
     *
     * @return The release date, or null if not applicable.
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Returns the first air date of the TV show.
     *
     * @return The first air date, or null if not applicable.
     */
    public String getFirstAirDate() {
        return firstAirDate;
    }

    /**
     * Optional: Override toString() for easier debugging/logging.
     *
     * @return A string representation of the media item.
     */
    @NonNull
    @Override
    public String toString() {
        return "MediaItem{" +
                "id=" + id +
                ", mediaType='" + mediaType + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", firstAirDate='" + firstAirDate + '\'' +
                '}';
    }
}
