package com.citycollege.musicexplorer.model;

import java.util.List;

public class ArtistDetail {
    public final Artist artist;
    public final List<Track> topTracks;
    public final List<Album> topAlbums;
    public final List<Artist> similarArtists;

    public ArtistDetail(Artist artist, List<Track> topTracks, List<Album> topAlbums, List<Artist> similarArtists) {
        this.artist = artist;
        this.topTracks = topTracks;
        this.topAlbums = topAlbums;
        this.similarArtists = similarArtists;
    }
}
