package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("author")
    private String author;

    @SerializedName("content")
    private String content;

    // Getter methods
    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    // Setter methods
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Optional: toString method for logging purposes
    @Override
    public String toString() {
        return "Review{" +
                "author='" + author + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
