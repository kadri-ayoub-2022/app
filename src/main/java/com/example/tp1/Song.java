package com.example.tp1;


import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.io.Serializable;

public class Song implements Serializable {

    private final String title;
    private final String artist;
    private final String lyrics;

    public Song(String title, String artist, String lyrics) {
        this.title = title;
        this.artist = artist;
        this.lyrics = lyrics;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getLyrics() { return lyrics; }
}
