package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model class representing a Cast member for a movie or TV show.
 */
public class Cast {

    // Fields should be private to encapsulate the data
    private final String name;

    @SerializedName("profile_path")
    private final String profilePath;

    private final String character;

    /**
     * Constructor for initialization of Cast object.
     * Optimized for immutability (fields cannot be changed after object creation).
     * This helps with thread-safety and ensures the object remains constant.
     *
     * @param name        Name of the cast member.
     * @param profilePath Path to the cast member's profile image.
     * @param character   The character played by the cast member.
     */
    public Cast(String name, String profilePath, String character) {
        this.name = name;
        this.profilePath = profilePath;
        this.character = character;
    }

    /**
     * Gets the name of the cast member.
     *
     * @return The name of the cast member.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the profile image path of the cast member.
     *
     * @return The profile image path of the cast member.
     */
    public String getProfilePath() {
        return profilePath;
    }

    /**
     * Gets the character name the cast member plays.
     *
     * @return The character played by the cast member.
     */
    public String getCharacter() {
        return character;
    }

    /**
     * Provides a string representation of the Cast object.
     * Useful for logging or debugging purposes.
     *
     * @return A string representation of the Cast object.
     */
    @Override
    public String toString() {
        // StringBuilder is more efficient than string concatenation in a loop or frequent calls
        return new StringBuilder("Cast{")
                .append("name='").append(name).append('\'')
                .append(", profilePath='").append(profilePath).append('\'')
                .append(", character='").append(character).append('\'')
                .append('}')
                .toString();
    }
}
