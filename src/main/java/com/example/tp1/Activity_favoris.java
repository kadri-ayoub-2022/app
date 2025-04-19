package com.example.tp1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;


public class Activity_favoris extends AppCompatActivity implements SongAdapter.OnItemClickListener {

    private boolean isTwoPane = false;
    private static final String SONG_LIST_KEY = "song_list";
    private ArrayList<Song> favoriteSongs;
    private SongAdapter adapter;

    private SongDatabase db;


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

        db = Room.databaseBuilder(getApplicationContext(), SongDatabase.class, "song_database")
                .allowMainThreadQueries()
                .build();

        favoriteSongs = new ArrayList<>(db.songDao().getAllSongs());


        // Création de l'adapter avec gestion du long clic
        adapter = new SongAdapter(favoriteSongs, this, (song, position) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Supprimer \"" + song.getTitle() + "\" des favoris ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        Song songToRemove = favoriteSongs.get(position);
                        db.songDao().delete(songToRemove);
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


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SONG_LIST_KEY, favoriteSongs);
    }
}



