package com.example.cinepulse.models;

public class Genre {

    private int id;
    private String name;

    // ✅ Add this constructor
    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
