package com.example.tp1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity {


    private TextView textView;
    private ImageView playView, pauseView, leftView, rightView, adoreView, NON_adoreView;


    private boolean isPlaying = false;
    private int currentSongIndex = 0;
    private int[] songList = { R.raw.dakir, R.raw.song };
    private String[] songTitles = { "dakir.mp3", "song.mp3" };


    private MusicService musicService;
    private boolean isBound = false;
    private SongDatabase db;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;


            updateUIFromService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

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


        db = Room.databaseBuilder(getApplicationContext(), SongDatabase.class, "song_database")
                .allowMainThreadQueries()
                .build();


        initializeViews();


        startAndBindMusicService();


        setupListeners();


        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }


        updateAdoreIcons();
    }

    private void initializeViews() {
        pauseView = findViewById(R.id.pauseView);
        playView = findViewById(R.id.playView);
        leftView = findViewById(R.id.left);
        rightView = findViewById(R.id.right);
        adoreView = findViewById(R.id.adore);
        NON_adoreView = findViewById(R.id.non_adore);
        textView = findViewById(R.id.textView);
    }


    private void startAndBindMusicService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setupListeners() {

        playView.setOnClickListener(v -> startMusic());


        pauseView.setOnClickListener(v -> pauseMusic());


        leftView.setOnClickListener(v -> playNextSong());


        rightView.setOnClickListener(v -> playPreviousSong());


        adoreView.setOnClickListener(v -> removeFromFavorites());
        NON_adoreView.setOnClickListener(v -> addToFavorites());


        View.OnLongClickListener openFavoritesListener = v -> {
            Intent intent = new Intent(MainActivity.this, Activity_favoris.class);
            startActivity(intent);
            return true;
        };
        adoreView.setOnLongClickListener(openFavoritesListener);
        NON_adoreView.setOnLongClickListener(openFavoritesListener);
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        isPlaying = savedInstanceState.getBoolean("isPlaying", false);
        currentSongIndex = savedInstanceState.getInt("currentSongIndex", 0);

        if (isBound) {
            if (isPlaying) {
                textView.setText("Lecture : " + songTitles[currentSongIndex]);
                playView.setVisibility(View.INVISIBLE);
                pauseView.setVisibility(View.VISIBLE);
                leftView.setVisibility(View.VISIBLE);
                rightView.setVisibility(View.VISIBLE);
            } else {
                textView.setText("Pause : " + songTitles[currentSongIndex]);
                playView.setVisibility(View.VISIBLE);
                pauseView.setVisibility(View.INVISIBLE);
                leftView.setVisibility(View.INVISIBLE);
                rightView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPlaying", isPlaying);
        outState.putInt("currentSongIndex", currentSongIndex);
    }

    private void updateUIFromService() {
        if (isBound) {
            isPlaying = musicService.isPlaying();
            currentSongIndex = musicService.getCurrentSongIndex();

            if (isPlaying) {
                textView.setText("Lecture : " + songTitles[currentSongIndex]);
                playView.setVisibility(View.INVISIBLE);
                pauseView.setVisibility(View.VISIBLE);
                leftView.setVisibility(View.VISIBLE);
                rightView.setVisibility(View.VISIBLE);
            } else {
                textView.setText("Pause : " + songTitles[currentSongIndex]);
                playView.setVisibility(View.VISIBLE);
                pauseView.setVisibility(View.INVISIBLE);
                leftView.setVisibility(View.INVISIBLE);
                rightView.setVisibility(View.INVISIBLE);
            }
            updateAdoreIcons();
        }
    }

    private void startMusic() {
        if (isBound) {
            musicService.startMusic();
            isPlaying = true;
            textView.setText("Lecture : " + songTitles[currentSongIndex]);
            playView.setVisibility(View.INVISIBLE);
            pauseView.setVisibility(View.VISIBLE);
            leftView.setVisibility(View.VISIBLE);
            rightView.setVisibility(View.VISIBLE);
        }
    }

    private void pauseMusic() {
        if (isBound) {
            musicService.pauseMusic();
            isPlaying = false;
            textView.setText("Pause : " + songTitles[currentSongIndex]);
            pauseView.setVisibility(View.INVISIBLE);
            playView.setVisibility(View.VISIBLE);
            leftView.setVisibility(View.INVISIBLE);
            rightView.setVisibility(View.INVISIBLE);
        }
    }

    private void playNextSong() {
        if (currentSongIndex < songList.length - 1) {
            currentSongIndex++;
            playSelectedSong();
        } else {
            Toast.makeText(this, "Aucune chanson suivante", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            playSelectedSong();
        } else {
            Toast.makeText(this, "Aucune chanson précédente", Toast.LENGTH_SHORT).show();
        }
    }

    private void playSelectedSong() {
        if (isBound) {
            musicService.playSong(currentSongIndex);
            isPlaying = true;
            textView.setText("Lecture : " + songTitles[currentSongIndex]);
            playView.setVisibility(View.INVISIBLE);
            pauseView.setVisibility(View.VISIBLE);
            leftView.setVisibility(View.VISIBLE);
            rightView.setVisibility(View.VISIBLE);
            updateAdoreIcons();
        }
    }

    private void addToFavorites() {
        String title = songTitles[currentSongIndex];
        Song existing = db.songDao().getSongByTitle(title);
        if (existing == null) {
            Song song = new Song(title, "Artiste inconnu", "Paroles inconnues");
            db.songDao().insert(song);
            Toast.makeText(this, "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
            updateAdoreIcons();
        }
    }

    private void removeFromFavorites() {
        String title = songTitles[currentSongIndex];
        Song song = db.songDao().getSongByTitle(title);
        if (song != null) {
            db.songDao().delete(song);
            Toast.makeText(this, "Supprimé des favoris", Toast.LENGTH_SHORT).show();
            updateAdoreIcons();
        }
    }

    private void updateAdoreIcons() {
        String currentTitle = songTitles[currentSongIndex];
        Song song = db.songDao().getSongByTitle(currentTitle);

        if (song != null) {
            // Chanson dans les favoris
            adoreView.setVisibility(View.VISIBLE);
            NON_adoreView.setVisibility(View.INVISIBLE);
        } else {
            // Chanson non dans les favoris
            NON_adoreView.setVisibility(View.VISIBLE);
            adoreView.setVisibility(View.INVISIBLE);
        }
    }

    //À la reprise de l'activité, synchronise l'interface avec le service
    @Override
    protected void onResume() {
        super.onResume();
        if (isBound) {
            updateUIFromService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Détacher le service mais ne pas l'arrêter pour qu'il continue en arrière-plan
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }

        // Fermer la base de données
        if (db != null) {
            db.close();
        }
    }
}