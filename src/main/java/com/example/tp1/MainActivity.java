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
import androidx.room.Room;

import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private int currentPosition = 0;
    private TextView textView;

    private SongDatabase db;

    private int[] songList = {
            R.raw.dakir,
            R.raw.song
    };

    private String[] songTitles = {
            "dakir.mp3",
            "song.mp3"
    };

    private int currentSongIndex = 0;



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

        db = Room.databaseBuilder(getApplicationContext(), SongDatabase.class, "song_database")
                .allowMainThreadQueries()
                .build();

        ImageView pauseView = findViewById(R.id.pauseView);
        ImageView playView = findViewById(R.id.playView);
        ImageView leftView = findViewById(R.id.left);
        ImageView rightView = findViewById(R.id.right);
        ImageView adoreView = findViewById(R.id.adore);
        ImageView NON_adoreView = findViewById(R.id.non_adore);
        textView = findViewById(R.id.textView);

        mediaPlayer = MediaPlayer.create(this, songList[currentSongIndex]);

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
            removeFromFavorites();
            updateAdoreIcons();
        });

        NON_adoreView.setOnClickListener(v -> {
            addToFavorites();
            updateAdoreIcons();
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
                textView.setText("Lecture " +songTitles[currentSongIndex]);
                playView.setVisibility(View.INVISIBLE);
                pauseView.setVisibility(View.VISIBLE);
                leftView.setVisibility(View.VISIBLE);
                rightView.setVisibility(View.VISIBLE);
            } else {
                textView.setText("Pause : "+songTitles[currentSongIndex]);
                playView.setVisibility(View.VISIBLE);
                pauseView.setVisibility(View.INVISIBLE);
                leftView.setVisibility(View.INVISIBLE);
                rightView.setVisibility(View.INVISIBLE);
            }
            
        }

        adoreView.setOnLongClickListener(longClickListener);
        NON_adoreView.setOnLongClickListener(longClickListener);

        leftView.setOnClickListener(v -> {
            if (currentSongIndex < songList.length - 1) {
                currentSongIndex++;
                playSong(currentSongIndex, playView, pauseView, leftView, rightView);
            } else {
                Toast.makeText(this, "Aucune chanson suivante", Toast.LENGTH_SHORT).show();
            }
        });

        rightView.setOnClickListener(v -> {
            if (currentSongIndex > 0) {
                currentSongIndex--;
                playSong(currentSongIndex, playView, pauseView, leftView, rightView);
            } else {
                Toast.makeText(this, "Aucune chanson précédente", Toast.LENGTH_SHORT).show();
            }
        });
        updateAdoreIcons();
    }

    private void startMusic(ImageView playView, ImageView pauseView, ImageView leftView, ImageView rightView) {
        if (!isPlaying) {
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
            isPlaying = true;
            textView.setText("Lecture :"+songTitles[currentSongIndex] );
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
            textView.setText("Pause :"+songTitles[currentSongIndex]);
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

    private void addToFavorites() {
        String title = songTitles[currentSongIndex];
        Song existing = db.songDao().getSongByTitle(title);
        if (existing == null) {
            Song song = new Song(title, "Artiste inconnu", "Paroles inconnues");
            db.songDao().insert(song);
            Toast.makeText(this, "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
        }
    }


    private void removeFromFavorites() {
        String title = songTitles[currentSongIndex];
        Song song = db.songDao().getSongByTitle(title);
        if (song != null) {
            db.songDao().delete(song);
            Toast.makeText(this, "Supprimé des favoris", Toast.LENGTH_SHORT).show();
        }
    }


    private void playSong(int index, ImageView playView, ImageView pauseView, ImageView leftView, ImageView rightView) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        currentSongIndex = index;
        mediaPlayer = MediaPlayer.create(this, songList[currentSongIndex]);
        updateAdoreIcons();
        mediaPlayer.start();
        isPlaying = true;

        textView.setText("Lecture : " + songTitles[currentSongIndex]);

        playView.setVisibility(View.INVISIBLE);
        pauseView.setVisibility(View.VISIBLE);
        leftView.setVisibility(View.VISIBLE);
        rightView.setVisibility(View.VISIBLE);

        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            textView.setText("Fin de lecture");
            playView.setVisibility(View.VISIBLE);
            pauseView.setVisibility(View.INVISIBLE);
            leftView.setVisibility(View.INVISIBLE);
            rightView.setVisibility(View.INVISIBLE);
        });
    }

    private void updateAdoreIcons() {
        ImageView adoreView = findViewById(R.id.adore);
        ImageView NON_adoreView = findViewById(R.id.non_adore);

        String currentTitle = songTitles[currentSongIndex];
        Song song = db.songDao().getSongByTitle(currentTitle);

        if (song != null) {
            // Song is in favorites
            adoreView.setVisibility(View.VISIBLE);
            NON_adoreView.setVisibility(View.INVISIBLE);
        } else {
            // Song is not in favorites
            NON_adoreView.setVisibility(View.VISIBLE);
            adoreView.setVisibility(View.INVISIBLE);
        }
    }




}
