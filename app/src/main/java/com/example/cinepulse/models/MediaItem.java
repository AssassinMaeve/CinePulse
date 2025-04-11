package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

public class MediaItem {
    @SerializedName("id")
    private int id;

    @SerializedName("media_type")
    private String mediaType; // "movie", "tv", "person"

    @SerializedName("title")
    private String title; // movie title

    @SerializedName("name")
    private String name; // TV show name

    @SerializedName("poster_path")
    private String posterPath;

    public int getId() {
        return id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getDisplayTitle() {
        return title != null ? title : name;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
