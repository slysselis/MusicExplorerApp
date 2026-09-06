package com.citycollege.musicexplorer.network;

import android.os.Handler;
import android.os.Looper;

import com.citycollege.musicexplorer.BuildConfig;
import com.citycollege.musicexplorer.model.Album;
import com.citycollege.musicexplorer.model.Artist;
import com.citycollege.musicexplorer.model.ArtistDetail;
import com.citycollege.musicexplorer.model.Track;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LastFmApiClient {
    private static final String BASE_URL = "https://ws.audioscrobbler.com/2.0/";
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void getTopArtists(ApiCallback<List<Artist>> callback) {
        run(callback, () -> parseArtists(request("chart.gettopartists&limit=30")));
    }

    public void getTopChartTracks(ApiCallback<List<Track>> callback) {
        run(callback, () -> parseChartTracks(request("chart.gettoptracks&limit=10")));
    }

    public void searchArtists(String query, ApiCallback<List<Artist>> callback) {
        run(callback, () -> {
            JSONObject json = request("artist.search&artist=" + encode(query) + "&limit=30");
            JSONArray artists = json.getJSONObject("results")
                    .getJSONObject("artistmatches")
                    .optJSONArray("artist");
            return parseArtistArray(artists);
        });
    }

    public void getArtistDetail(String artistName, ApiCallback<ArtistDetail> callback) {
        run(callback, () -> {
            Artist artist = parseArtistInfo(request("artist.getinfo&artist=" + encode(artistName)));
            List<Track> tracks = parseTracks(request("artist.gettoptracks&artist=" + encode(artistName) + "&limit=8"));
            List<Album> albums = parseAlbums(request("artist.gettopalbums&artist=" + encode(artistName) + "&limit=6"));
            List<Artist> similar = parseSimilarArtists(request("artist.getsimilar&artist=" + encode(artistName) + "&limit=8"));
            return new ArtistDetail(artist, tracks, albums, similar);
        });
    }

    private <T> void run(ApiCallback<T> callback, ApiTask<T> task) {
        executorService.execute(() -> {
            try {
                if (BuildConfig.LASTFM_API_KEY.equals("PUT_YOUR_LASTFM_API_KEY_HERE")) {
                    throw new IllegalStateException("Add your Last.fm API key in gradle.properties, then sync and run again.");
                }
                T result = task.execute();
                mainHandler.post(() -> callback.onSuccess(result));
            } catch (Exception exception) {
                mainHandler.post(() -> callback.onError(friendlyError(exception)));
            }
        });
    }

    private JSONObject request(String query) throws Exception {
        String urlString = BASE_URL + "?method=" + query
                + "&api_key=" + encode(BuildConfig.LASTFM_API_KEY)
                + "&format=json";
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setConnectTimeout(12000);
        connection.setReadTimeout(12000);
        connection.setRequestMethod("GET");

        int code = connection.getResponseCode();
        InputStream stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
        String body = readBody(stream);
        JSONObject json = new JSONObject(body);
        if (json.has("error")) {
            throw new IllegalStateException(json.optString("message", "Last.fm could not load that request."));
        }
        return json;
    }

    private String readBody(InputStream stream) throws Exception {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    private List<Artist> parseArtists(JSONObject json) throws Exception {
        return parseArtistArray(json.getJSONObject("artists").optJSONArray("artist"));
    }

    private List<Artist> parseSimilarArtists(JSONObject json) throws Exception {
        return parseArtistArray(json.getJSONObject("similarartists").optJSONArray("artist"));
    }

    private List<Artist> parseArtistArray(JSONArray array) {
        List<Artist> artists = new ArrayList<>();
        if (array == null) {
            return artists;
        }
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.optJSONObject(i);
            if (item == null) {
                continue;
            }
            artists.add(new Artist(
                    item.optString("name"),
                    bestImage(item.optJSONArray("image")),
                    item.optString("listeners"),
                    item.optString("url"),
                    ""
            ));
        }
        return artists;
    }

    private Artist parseArtistInfo(JSONObject json) throws Exception {
        JSONObject item = json.getJSONObject("artist");
        JSONObject stats = item.optJSONObject("stats");
        JSONObject bio = item.optJSONObject("bio");
        String summary = bio == null ? "" : cleanBio(bio.optString("summary"));
        return new Artist(
                item.optString("name"),
                bestImage(item.optJSONArray("image")),
                stats == null ? "" : stats.optString("listeners"),
                item.optString("url"),
                summary
        );
    }

    private List<Track> parseTracks(JSONObject json) throws Exception {
        List<Track> tracks = new ArrayList<>();
        JSONArray array = json.getJSONObject("toptracks").optJSONArray("track");
        appendTracks(tracks, array);
        return tracks;
    }

    private List<Track> parseChartTracks(JSONObject json) throws Exception {
        List<Track> tracks = new ArrayList<>();
        JSONArray array = json.getJSONObject("tracks").optJSONArray("track");
        appendTracks(tracks, array);
        return tracks;
    }

    private void appendTracks(List<Track> tracks, JSONArray array) {
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.optJSONObject(i);
            if (item != null) {
                tracks.add(new Track(item.optString("name"), item.optString("playcount")));
            }
        }
    }

    private List<Album> parseAlbums(JSONObject json) throws Exception {
        List<Album> albums = new ArrayList<>();
        JSONArray array = json.getJSONObject("topalbums").optJSONArray("album");
        if (array == null) {
            return albums;
        }
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.optJSONObject(i);
            if (item != null) {
                albums.add(new Album(item.optString("name"), bestImage(item.optJSONArray("image"))));
            }
        }
        return albums;
    }

    private String bestImage(JSONArray images) {
        if (images == null) {
            return "";
        }
        for (int i = images.length() - 1; i >= 0; i--) {
            JSONObject image = images.optJSONObject(i);
            if (image != null) {
                String url = image.optString("#text");
                if (url != null && !url.trim().isEmpty()) {
                    return url;
                }
            }
        }
        return "";
    }

    private String cleanBio(String html) {
        return html.replaceAll("<a[^>]*>Read more on Last.fm</a>.*", "")
                .replaceAll("<[^>]+>", "")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .trim();
    }

    private String friendlyError(Exception exception) {
        String message = exception.getMessage();
        if (message != null && message.contains("API key")) {
            return message;
        }
        if (message != null && !message.trim().isEmpty() && !message.contains("ws.audioscrobbler")) {
            return message;
        }
        return "We could not load music data right now. Please check your connection and try again.";
    }

    private String encode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8");
    }

    private interface ApiTask<T> {
        T execute() throws Exception;
    }
}
