package com.example.tp1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Activity_favoris extends AppCompatActivity implements SongAdapter.OnItemClickListener {

    private boolean isTwoPane = false;
    private static final String SONG_LIST_KEY = "song_list";
    private ArrayList<Song> favoriteSongs;
    private SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris2);

        // Vérifie si le fragment est présent (mode paysage) bach nkhdmo b fragment
        if (findViewById(R.id.fragment_container) != null) {
            isTwoPane = true;
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // en cas de recreation ta3 view  ghadi tjib la chanson mn sauvgarde
        if (savedInstanceState != null) {
            favoriteSongs = (ArrayList<Song>) savedInstanceState.getSerializable(SONG_LIST_KEY);
        } else {
            favoriteSongs = new ArrayList<>(getFakeFavoriteSongs());
        }

        // Création de l'adapter avec gestion du long clic
        adapter = new SongAdapter(favoriteSongs, this, (song, position) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Supprimer \"" + song.getTitle() + "\" des favoris ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        favoriteSongs.remove(position);
                        adapter.notifyItemRemoved(position);
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Song song) {
        if (isTwoPane) {
            // Afficher dans le fragment
            SongDetailFragment fragment = SongDetailFragment.newInstance(song);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            // Lancer nouvelle activité
            Intent intent = new Intent(this, SongDetailActivity.class);
            intent.putExtra("title", song.getTitle());
            intent.putExtra("artist", song.getArtist());
            intent.putExtra("lyrics", song.getLyrics());
            startActivity(intent);
        }
    }

    private List<Song> getFakeFavoriteSongs() {
        List<Song> songs = new ArrayList<>();
        songs.add(new Song("Imagine", "John Lennon", "Imagine all the people..."));
        songs.add(new Song("Shape of You", "Ed Sheeran", "I'm in love with the shape of you..."));
        songs.add(new Song("Hallelujah", "Leonard Cohen", "I heard there was a secret chord..."));
        return songs;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SONG_LIST_KEY, favoriteSongs);
    }
}



