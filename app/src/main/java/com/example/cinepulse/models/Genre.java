package com.example.cinepulse.models;

/**
 * Represents a genre in the system.
 * The Genre class is immutable to ensure data consistency.
 */
public final class Genre {

    // The genre's unique identifier
    private final int id;

    // The name of the genre (e.g., "Action", "Drama")
    private final String name;

    /**
     * Constructor for initializing a Genre object.
     *
     * @param id The unique identifier for the genre.
     * @param name The name of the genre.
     */
    public Genre(int id, String name) {
        // Validation for non-null name (optional, depending on your use case)
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        this.id = id;
        this.name = name;
    }

    /**
     * Returns the genre's unique identifier.
     *
     * @return The genre's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the genre.
     *
     * @return The genre's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Optional: Override the toString() method for easier debugging/logging.
     *
     * @return A string representation of the genre.
     */
    @Override
    public String toString() {
        return "Genre{id=" + id + ", name='" + name + "'}";
    }
}
