package com.citycollege.musicexplorer.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.citycollege.musicexplorer.MainActivity;
import com.citycollege.musicexplorer.R;
import com.citycollege.musicexplorer.data.FavoriteArtist;
import com.citycollege.musicexplorer.data.FavoriteRepository;

import java.util.List;

public class CollectionFragment extends Fragment {
    private FavoriteRepository repository;
    private CollectionAdapter adapter;
    private View emptyCollectionView;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        repository = new FavoriteRepository(requireContext());
        recyclerView = view.findViewById(R.id.collectionRecyclerView);
        emptyCollectionView = view.findViewById(R.id.emptyCollectionView);
        adapter = new CollectionAdapter(new CollectionAdapter.CollectionListener() {
            @Override
            public void onOpen(FavoriteArtist artist) {
                ((MainActivity) requireActivity()).openArtistDetail(artist.name);
            }

            @Override
            public void onRemove(FavoriteArtist artist) {
                repository.delete(artist, CollectionFragment.this::loadFavorites);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        loadFavorites();
    }

    private void loadFavorites() {
        repository.getAll(artists -> requireActivity().runOnUiThread(() -> render(artists)));
    }

    private void render(List<FavoriteArtist> artists) {
        adapter.submitList(artists);
        boolean empty = artists.isEmpty();
        emptyCollectionView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
