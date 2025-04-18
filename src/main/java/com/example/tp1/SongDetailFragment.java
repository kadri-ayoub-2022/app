package com.example.tp1;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



public class SongDetailFragment extends Fragment {

    public static SongDetailFragment newInstance(Song song) {
        SongDetailFragment fragment = new SongDetailFragment();
        Bundle args = new Bundle();
        args.putString("title", song.getTitle());
        args.putString("artist", song.getArtist());
        args.putString("lyrics", song.getLyrics());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_detail, container, false);
        TextView title = view.findViewById(R.id.songTitle);
        TextView artist = view.findViewById(R.id.songArtist);
        TextView lyrics = view.findViewById(R.id.songLyrics);

        if (getArguments() != null) {
            title.setText(getArguments().getString("title"));
            artist.setText(getArguments().getString("artist"));
            lyrics.setText(getArguments().getString("lyrics"));
        }

        return view;
    }
}
