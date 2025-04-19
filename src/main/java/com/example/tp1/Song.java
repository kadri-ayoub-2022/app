package com.example.tp1;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "songs")
public class Song implements Serializable {

    @PrimaryKey
    @NonNull
    private String title;

    private String artist;
    private String lyrics;

    public Song(@NonNull String title, String artist, String lyrics) {
        this.title = title;
        this.artist = artist;
        this.lyrics = lyrics;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
}

