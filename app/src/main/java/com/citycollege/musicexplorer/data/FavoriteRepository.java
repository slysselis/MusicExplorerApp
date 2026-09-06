package com.citycollege.musicexplorer.data;

import android.content.Context;

import com.citycollege.musicexplorer.model.Artist;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteRepository {
    public interface FavoritesCallback {
        void onResult(List<FavoriteArtist> artists);
    }

    public interface ExistsCallback {
        void onResult(boolean exists);
    }

    public interface DoneCallback {
        void onDone();
    }

    private final FavoriteArtistDao dao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public FavoriteRepository(Context context) {
        dao = AppDatabase.getInstance(context).favoriteArtistDao();
    }

    public void getAll(FavoritesCallback callback) {
        executorService.execute(() -> callback.onResult(dao.getAll()));
    }

    public void exists(String name, ExistsCallback callback) {
        executorService.execute(() -> callback.onResult(dao.exists(name)));
    }

    public void save(Artist artist, DoneCallback callback) {
        executorService.execute(() -> {
            dao.insert(new FavoriteArtist(artist.name, artist.imageUrl, artist.listeners, artist.url));
            callback.onDone();
        });
    }

    public void delete(FavoriteArtist artist, DoneCallback callback) {
        executorService.execute(() -> {
            dao.delete(artist);
            callback.onDone();
        });
    }
}
