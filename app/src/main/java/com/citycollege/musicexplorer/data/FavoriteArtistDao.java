package com.citycollege.musicexplorer.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteArtist artist);

    @Delete
    void delete(FavoriteArtist artist);

    @Query("SELECT * FROM favorite_artists ORDER BY name COLLATE NOCASE")
    List<FavoriteArtist> getAll();

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_artists WHERE name = :name LIMIT 1)")
    boolean exists(String name);
}
