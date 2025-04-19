package com.example.tp1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Song song);

    @Delete
    void delete(Song song);

    @Query("SELECT * FROM songs")
    List<Song> getAllSongs();

    @Query("SELECT * FROM songs WHERE title = :title LIMIT 1")
    Song getSongByTitle(String title);
}

