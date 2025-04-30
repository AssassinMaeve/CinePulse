package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Represents the response from an API call that fetches the cast details for a movie or TV show.
 * This class is immutable, ensuring the integrity of the data after instantiation.
 */
public class CastResponse {

    // The list of cast members, should be final for immutability
    @SerializedName("cast")
    private final List<Cast> cast;

    /**
     * Constructor for initializing the CastResponse object.
     * The list is passed in as an argument, ensuring that the CastResponse class is immutable.
     *
     * @param cast List of Cast objects representing the cast of a movie or TV show.
     */
    public CastResponse(List<Cast> cast) {
        // To ensure immutability, use unmodifiableList if modifications aren't necessary
        this.cast = cast == null ? Collections.emptyList() : Collections.unmodifiableList(cast);
    }

    /**
     * Gets the list of cast members.
     *
     * @return A list of Cast objects. This list is unmodifiable to prevent changes.
     */
    public List<Cast> getCast() {
        return cast;
    }
}
