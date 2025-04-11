package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

public class Cast {

    private String name;

    @SerializedName("profile_path")
    private String profilePath;

    private String character;

    public String getName() {
        return name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public String getCharacter() {
        return character;
    }
}
