package com.citycollege.musicexplorer.model;

public class Artist {
    public final String name;
    public final String imageUrl;
    public final String listeners;
    public final String url;
    public final String biography;

    public Artist(String name, String imageUrl, String listeners, String url, String biography) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.listeners = listeners;
        this.url = url;
        this.biography = biography;
    }
}
