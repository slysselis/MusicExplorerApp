package com.citycollege.musicexplorer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.citycollege.musicexplorer.R;
import com.citycollege.musicexplorer.model.Album;
import com.citycollege.musicexplorer.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private final List<Album> albums = new ArrayList<>();

    public void submitList(List<Album> newAlbums) {
        albums.clear();
        albums.addAll(newAlbums);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.nameTextView.setText(album.name);
        ImageLoader.load(holder.albumImageView, album.imageUrl);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        final ImageView albumImageView;
        final TextView nameTextView;

        AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImageView = itemView.findViewById(R.id.albumImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
}
