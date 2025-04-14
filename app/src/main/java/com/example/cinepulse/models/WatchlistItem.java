package com.example.cinepulse.models;

import java.util.Objects;

public class WatchlistItem {
    private int id;
    private String title;
    private String posterPath;
    private String type; // "movie" or "tv"

    public WatchlistItem(int id, String title, String posterPath, String type) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.type = type;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getPosterPath() { return posterPath; }
    public String getType() { return type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WatchlistItem)) return false;
        WatchlistItem item = (WatchlistItem) o;
        return id == item.id && Objects.equals(type, item.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
