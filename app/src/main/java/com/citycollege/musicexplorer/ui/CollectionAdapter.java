package com.citycollege.musicexplorer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.citycollege.musicexplorer.R;
import com.citycollege.musicexplorer.data.FavoriteArtist;
import com.citycollege.musicexplorer.model.Artist;
import com.citycollege.musicexplorer.util.ImageLoader;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {
    public interface CollectionListener {
        void onOpen(FavoriteArtist artist);
        void onRemove(FavoriteArtist artist);
    }

    private final CollectionListener listener;
    private final List<FavoriteArtist> artists = new ArrayList<>();

    public CollectionAdapter(CollectionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FavoriteArtist> newArtists) {
        artists.clear();
        artists.addAll(newArtists);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        FavoriteArtist artist = artists.get(position);
        holder.nameTextView.setText(artist.name);
        holder.subtitleTextView.setText("Saved artist");
        holder.removeButton.setVisibility(View.VISIBLE);
        ImageLoader.load(holder.artistImageView, artist.imageUrl);
        holder.itemView.setOnClickListener(v -> listener.onOpen(artist));
        holder.removeButton.setOnClickListener(v -> listener.onRemove(artist));
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public static Artist toArtist(FavoriteArtist favoriteArtist) {
        return new Artist(favoriteArtist.name, favoriteArtist.imageUrl, favoriteArtist.listeners, favoriteArtist.profileUrl, "");
    }

    static class CollectionViewHolder extends RecyclerView.ViewHolder {
        final android.widget.ImageView artistImageView;
        final TextView nameTextView;
        final TextView subtitleTextView;
        final MaterialButton removeButton;

        CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImageView = itemView.findViewById(R.id.artistImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            subtitleTextView = itemView.findViewById(R.id.subtitleTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
