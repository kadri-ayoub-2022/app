package com.example.tp1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SongDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        TextView titleView = findViewById(R.id.songTitle);
        TextView artistView = findViewById(R.id.songArtist);
        TextView lyricsView = findViewById(R.id.songLyrics);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");
        String lyrics = intent.getStringExtra("lyrics");

        titleView.setText(title);
        artistView.setText(artist);
        lyricsView.setText(lyrics);
    }
}




