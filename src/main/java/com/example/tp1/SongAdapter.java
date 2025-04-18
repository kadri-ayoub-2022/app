package com.example.tp1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    // hadi interface pour gerer les clicks sur items
    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    private final List<Song> songs;
    private final OnItemClickListener listener;

    private final OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(Song song, int position);
    }


    // bach twli implémenté dans l’activité ou le fragment
    public SongAdapter(List<Song> songs, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        this.songs = songs;
        this.listener = clickListener;
        this.longClickListener = longClickListener;
    }


    // hadi tkhali la vue ta3 la liste tban
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SongViewHolder(view);
    }

    // associe une chanson a la vue
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        ((TextView) holder.itemView).setText(song.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(song));
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(song, position);
                return true;
            }
            return false;
        });


    }

    @Override
    public int getItemCount() {
        return songs.size();
    }



    // la vue d’un seul élément
    static class SongViewHolder extends RecyclerView.ViewHolder {
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
