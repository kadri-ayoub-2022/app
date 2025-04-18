package com.example.tp1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private boolean isAdore = false;
    private int currentPosition = 0;
    private TextView textView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPlaying", isPlaying);
        outState.putInt("currentPosition", mediaPlayer.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isPlaying = savedInstanceState.getBoolean("isPlaying");
        currentPosition = savedInstanceState.getInt("currentPosition");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView pauseView = findViewById(R.id.pauseView);
        ImageView playView = findViewById(R.id.playView);
        ImageView leftView = findViewById(R.id.left);
        ImageView rightView = findViewById(R.id.right);
        ImageView adoreView = findViewById(R.id.adore);
        ImageView NON_adoreView = findViewById(R.id.non_adore);
        textView = findViewById(R.id.textView);

        mediaPlayer = MediaPlayer.create(this, R.raw.dakir);

        playView.setOnClickListener(v -> startMusic(playView, pauseView, leftView, rightView));
        pauseView.setOnClickListener(v -> pauseMusic(playView, pauseView, leftView, rightView));

        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            textView.setText("Fin de lecture");
            playView.setVisibility(View.VISIBLE);
            pauseView.setVisibility(View.INVISIBLE);
            leftView.setVisibility(View.INVISIBLE);
            rightView.setVisibility(View.INVISIBLE);
        });

        adoreView.setOnClickListener(v -> {
            isAdore = false;
            adoreView.setVisibility(View.INVISIBLE);
            NON_adoreView.setVisibility(View.VISIBLE);
            removeFromFavorites("music.mp3");
        });

        NON_adoreView.setOnClickListener(v -> {
            isAdore = true;
            NON_adoreView.setVisibility(View.INVISIBLE);
            adoreView.setVisibility(View.VISIBLE);
            addToFavorites("music.mp3");
        });

        View.OnLongClickListener longClickListener = v -> {
//            pauseMusic(playView, pauseView, leftView, rightView);
            Intent intent = new Intent(MainActivity.this, Activity_favoris.class);
            startActivity(intent);
            return true;
        };

        if (savedInstanceState != null) {
            isPlaying = savedInstanceState.getBoolean("isPlaying");
            currentPosition = savedInstanceState.getInt("currentPosition");

            if (isPlaying) {
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
                textView.setText("Lecture : music.mp3");
                playView.setVisibility(View.INVISIBLE);
                pauseView.setVisibility(View.VISIBLE);
                leftView.setVisibility(View.VISIBLE);
                rightView.setVisibility(View.VISIBLE);
            } else {
                textView.setText("Pause : music.mp3");
                playView.setVisibility(View.VISIBLE);
                pauseView.setVisibility(View.INVISIBLE);
                leftView.setVisibility(View.INVISIBLE);
                rightView.setVisibility(View.INVISIBLE);
            }

            if(isAdore){
                adoreView.setVisibility(View.INVISIBLE);
                NON_adoreView.setVisibility(View.VISIBLE);
                removeFromFavorites("music.mp3");
            }else{
                NON_adoreView.setVisibility(View.INVISIBLE);
                adoreView.setVisibility(View.VISIBLE);
                addToFavorites("music.mp3");

            }
        }

        adoreView.setOnLongClickListener(longClickListener);
        NON_adoreView.setOnLongClickListener(longClickListener);
    }

    private void startMusic(ImageView playView, ImageView pauseView, ImageView leftView, ImageView rightView) {
        if (!isPlaying) {
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
            isPlaying = true;
            textView.setText("Lecture : music.mp3");
            playView.setVisibility(View.INVISIBLE);
            pauseView.setVisibility(View.VISIBLE);
            leftView.setVisibility(View.VISIBLE);
            rightView.setVisibility(View.VISIBLE);
        }
    }

    private void pauseMusic(ImageView playView, ImageView pauseView, ImageView leftView, ImageView rightView) {
        if (isPlaying) {
            mediaPlayer.pause();
            currentPosition = mediaPlayer.getCurrentPosition();
            isPlaying = false;
            textView.setText("Pause : music.mp3");
            pauseView.setVisibility(View.INVISIBLE);
            playView.setVisibility(View.VISIBLE);
            leftView.setVisibility(View.INVISIBLE);
            rightView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPlaying) {
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlaying) {
            mediaPlayer.pause();
            currentPosition = mediaPlayer.getCurrentPosition();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void addToFavorites(String songTitle) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> favorites = prefs.getStringSet("favorites", new HashSet<>());
        Set<String> updatedFavorites = new HashSet<>(favorites);
        updatedFavorites.add(songTitle);
        prefs.edit().putStringSet("favorites", updatedFavorites).apply();
    }

    private void removeFromFavorites(String songTitle) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> favorites = prefs.getStringSet("favorites", new HashSet<>());
        Set<String> updatedFavorites = new HashSet<>(favorites);
        updatedFavorites.remove(songTitle);
        prefs.edit().putStringSet("favorites", updatedFavorites).apply();
    }
}
