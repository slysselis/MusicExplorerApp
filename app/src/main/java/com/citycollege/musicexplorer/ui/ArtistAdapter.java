package com.citycollege.musicexplorer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.citycollege.musicexplorer.R;
import com.citycollege.musicexplorer.model.Artist;
import com.citycollege.musicexplorer.util.ImageLoader;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    public interface ArtistClickListener {
        void onArtistClick(Artist artist);
    }

    private final ArtistClickListener listener;
    private final List<Artist> artists = new ArrayList<>();

    public ArtistAdapter(ArtistClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Artist> newArtists) {
        artists.clear();
        artists.addAll(newArtists);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.nameTextView.setText(artist.name);
        String subtitle = artist.listeners == null || artist.listeners.isEmpty()
                ? "Artist"
                : artist.listeners + " listeners";
        holder.subtitleTextView.setText(subtitle);
        holder.removeButton.setVisibility(View.GONE);
        ImageLoader.load(holder.artistImageView, artist.imageUrl);
        holder.itemView.setOnClickListener(v -> listener.onArtistClick(artist));
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {
        final android.widget.ImageView artistImageView;
        final TextView nameTextView;
        final TextView subtitleTextView;
        final MaterialButton removeButton;

        ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImageView = itemView.findViewById(R.id.artistImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            subtitleTextView = itemView.findViewById(R.id.subtitleTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
