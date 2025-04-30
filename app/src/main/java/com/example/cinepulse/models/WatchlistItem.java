package com.example.cinepulse.models;

import java.util.Objects;

/**
 * Represents a movie or TV show item in the user's watchlist.
 * Contains information like ID, title, poster path, and type (movie/tv).
 */
public final class WatchlistItem {

    // Unique identifier for the watchlist item
    private final int id;

    // Title of the movie or TV show
    private final String title;

    // Path to the poster image of the movie or TV show
    private final String posterPath;

    // Type of the item: "movie" or "tv"
    private final String type;

    /**
     * Constructs a new WatchlistItem with the provided parameters.
     *
     * @param id        The unique ID of the item.
     * @param title     The title of the movie or TV show.
     * @param posterPath The poster path for the item.
     * @param type      The type of the item ("movie" or "tv").
     */
    public WatchlistItem(int id, String title, String posterPath, String type) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.type = type;
    }

    // Getter for the unique ID of the item
    public int getId() {
        return id;
    }

    // Getter for the title of the item
    public String getTitle() {
        return title;
    }

    // Getter for the poster path of the item
    public String getPosterPath() {
        return posterPath;
    }

    // Getter for the type of the item ("movie" or "tv")
    public String getType() {
        return type;
    }

    /**
     * Compares this watchlist item to another object for equality.
     * Two items are considered equal if they have the same ID and type.
     *
     * @param o The object to compare this item with.
     * @return True if the items are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        // Fast check for reference equality
        if (this == o) return true;

        // Check if the object is of the same type
        if (!(o instanceof WatchlistItem)) return false;

        // Cast the object to WatchlistItem and compare ID and type
        WatchlistItem item = (WatchlistItem) o;
        return id == item.id && Objects.equals(type, item.type);
    }

    /**
     * Returns a hash code for the watchlist item.
     * The hash code is based on the item's ID and type.
     *
     * @return The hash code of the watchlist item.
     */
    @Override
    public int hashCode() {
        // Using Objects.hash for a consistent hash based on ID and type
        return Objects.hash(id, type);
    }

    /**
     * Optional: Override toString() for logging or debugging purposes.
     *
     * @return A string representation of the watchlist item.
     */
    @Override
    public String toString() {
        return "WatchlistItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
