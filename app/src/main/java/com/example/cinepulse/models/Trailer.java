package com.example.cinepulse.models;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a trailer object that contains information related to a video trailer.
 */
public final class Trailer {

    @SerializedName("id")
    private final String id;

    @SerializedName("key")
    private final String key;

    @SerializedName("name")
    private final String name;

    @SerializedName("site")
    private final String site;

    @SerializedName("type")
    private final String type;

    @SerializedName("official")
    private final boolean official;

    // ✅ Constructor initializes ALL final fields
    public Trailer(
            String id,
            String key,
            String name,
            String site,
            String type,
            boolean official
    ) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
        this.official = official;
    }

    // ✅ Getters

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }

    public boolean isOfficial() {
        return official;
    }

    @NonNull
    @Override
    public String toString() {
        return "Trailer{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                ", type='" + type + '\'' +
                ", official=" + official +
                '}';
    }
}
