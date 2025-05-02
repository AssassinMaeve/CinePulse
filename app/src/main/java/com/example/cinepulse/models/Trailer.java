package com.example.cinepulse.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a trailer object that contains information related to a video trailer.
 * This class is used to deserialize the trailer data from the API response.
 */
public final class Trailer {

    // Unique identifier for the trailer
    @SerializedName("id")
    private final String id;

    // Key used to fetch the trailer video from the video site (e.g., YouTube)
    @SerializedName("key")
    private final String key;

    // Name of the trailer (typically used to differentiate multiple trailers)
    @SerializedName("name")
    private final String name;

    // The site where the trailer is hosted (e.g., YouTube, Vimeo)
    @SerializedName("site")
    private final String site;

    // Type of the trailer (e.g., "Official", "Teaser")
    @SerializedName("type")
    private final String type;

    public Trailer(String id, String key, String name, String site, String type) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
    }

    // Getter methods

    /**
     * Returns the ID of the trailer.
     *
     * @return The trailer ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the key of the trailer, used to fetch the trailer from the site.
     *
     * @return The trailer key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the name of the trailer.
     *
     * @return The trailer name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of the trailer.
     *
     * @return The trailer type (e.g., "Official", "Teaser").
     */
    public String getType() {
        return type;
    }

    // Optional: Override toString() for better logging or debugging

    @NonNull
    @Override
    public String toString() {
        return "Trailer{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
