package com.citycollege.musicexplorer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.citycollege.musicexplorer.R;
import com.citycollege.musicexplorer.model.Track;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private final List<Track> tracks = new ArrayList<>();

    public void submitList(List<Track> newTracks) {
        tracks.clear();
        tracks.addAll(newTracks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.nameTextView.setText(track.name);
        holder.playcountChip.setText(track.playcount == null || track.playcount.isEmpty() ? "Top track" : track.playcount + " plays");
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final Chip playcountChip;

        TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            playcountChip = itemView.findViewById(R.id.playcountChip);
        }
    }
}
