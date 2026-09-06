package com.citycollege.musicexplorer.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_artists")
public class FavoriteArtist {
    @PrimaryKey
    @NonNull
    public String name;
    public String imageUrl;
    public String listeners;
    public String profileUrl;

    public FavoriteArtist(@NonNull String name, String imageUrl, String listeners, String profileUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.listeners = listeners;
        this.profileUrl = profileUrl;
    }
}
