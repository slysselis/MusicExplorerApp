package com.citycollege.musicexplorer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.citycollege.musicexplorer.MainActivity;
import com.citycollege.musicexplorer.R;
import com.citycollege.musicexplorer.data.FavoriteRepository;
import com.citycollege.musicexplorer.model.Artist;
import com.citycollege.musicexplorer.model.ArtistDetail;
import com.citycollege.musicexplorer.network.ApiCallback;
import com.citycollege.musicexplorer.network.LastFmApiClient;
import com.citycollege.musicexplorer.network.NetworkUtils;
import com.citycollege.musicexplorer.util.ImageLoader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class ArtistDetailFragment extends Fragment {
    private static final String ARG_ARTIST_NAME = "artist_name";

    private final LastFmApiClient apiClient = new LastFmApiClient();
    private FavoriteRepository repository;
    private String artistName;
    private Artist currentArtist;
    private View contentScroll;
    private View stateContainer;
    private CircularProgressIndicator progressIndicator;
    private TextView stateTitleTextView;
    private TextView stateMessageTextView;
    private MaterialButton retryButton;
    private android.widget.ImageView artistImageView;
    private TextView nameTextView;
    private TextView listenersTextView;
    private TextView bioTextView;
    private MaterialButton saveButton;
    private TrackAdapter trackAdapter;
    private AlbumAdapter albumAdapter;
    private ArtistAdapter similarAdapter;

    public static ArtistDetailFragment newInstance(String artistName) {
        Bundle args = new Bundle();
        args.putString(ARG_ARTIST_NAME, artistName);
        ArtistDetailFragment fragment = new ArtistDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artist_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        artistName = requireArguments().getString(ARG_ARTIST_NAME);
        repository = new FavoriteRepository(requireContext());
        contentScroll = view.findViewById(R.id.contentScroll);
        stateContainer = view.findViewById(R.id.stateContainer);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        stateTitleTextView = view.findViewById(R.id.stateTitleTextView);
        stateMessageTextView = view.findViewById(R.id.stateMessageTextView);
        retryButton = view.findViewById(R.id.retryButton);
        artistImageView = view.findViewById(R.id.artistImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        listenersTextView = view.findViewById(R.id.listenersTextView);
        bioTextView = view.findViewById(R.id.bioTextView);
        saveButton = view.findViewById(R.id.saveButton);

        trackAdapter = new TrackAdapter();
        albumAdapter = new AlbumAdapter();
        similarAdapter = new ArtistAdapter(artist -> ((MainActivity) requireActivity()).openArtistDetail(artist.name));
        setupList(view.findViewById(R.id.tracksRecyclerView), trackAdapter);
        setupList(view.findViewById(R.id.albumsRecyclerView), albumAdapter);
        setupList(view.findViewById(R.id.similarRecyclerView), similarAdapter);

        retryButton.setOnClickListener(v -> loadArtist());
        saveButton.setOnClickListener(v -> saveCurrentArtist());
        view.findViewById(R.id.shareButton).setOnClickListener(v -> shareCurrentArtist());

        loadArtist();
    }

    private void setupList(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadArtist() {
        if (!NetworkUtils.isOnline(requireContext())) {
            showError("You are offline", "Connect to the internet to load artist details. Saved artists remain available in My Collection.");
            return;
        }
        showLoading();
        apiClient.getArtistDetail(artistName, new ApiCallback<ArtistDetail>() {
            @Override
            public void onSuccess(ArtistDetail result) {
                render(result);
            }

            @Override
            public void onError(String message) {
                showError("Could not load artist", message);
            }
        });
    }

    private void render(ArtistDetail detail) {
        currentArtist = detail.artist;
        nameTextView.setText(detail.artist.name);
        listenersTextView.setText(detail.artist.listeners == null || detail.artist.listeners.isEmpty()
                ? "Listener count unavailable"
                : detail.artist.listeners + " listeners");
        bioTextView.setText(detail.artist.biography == null || detail.artist.biography.isEmpty()
                ? "Biography unavailable from Last.fm."
                : detail.artist.biography);
        ImageLoader.load(artistImageView, detail.artist.imageUrl);
        trackAdapter.submitList(detail.topTracks);
        albumAdapter.submitList(detail.topAlbums);
        similarAdapter.submitList(detail.similarArtists);
        repository.exists(detail.artist.name, exists -> requireActivity().runOnUiThread(() -> updateSaveButton(exists)));
        showContent();
    }

    private void saveCurrentArtist() {
        if (currentArtist == null) {
            return;
        }
        repository.save(currentArtist, () -> requireActivity().runOnUiThread(() -> {
            updateSaveButton(true);
            Toast.makeText(requireContext(), "Saved to collection", Toast.LENGTH_SHORT).show();
        }));
    }

    private void shareCurrentArtist() {
        if (currentArtist == null) {
            return;
        }
        String link = currentArtist.url == null || currentArtist.url.isEmpty()
                ? "https://www.last.fm/music/" + currentArtist.name.replace(" ", "+")
                : currentArtist.url;
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, currentArtist.name + " on Last.fm: " + link);
        startActivity(Intent.createChooser(sendIntent, "Share artist"));
    }

    private void updateSaveButton(boolean saved) {
        saveButton.setEnabled(!saved);
        saveButton.setText(saved ? "Saved" : "Save to Collection");
    }

    private void showLoading() {
        contentScroll.setVisibility(View.INVISIBLE);
        stateContainer.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.VISIBLE);
        stateTitleTextView.setVisibility(View.VISIBLE);
        stateMessageTextView.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        stateTitleTextView.setText("Loading artist");
    }

    private void showContent() {
        stateContainer.setVisibility(View.GONE);
        progressIndicator.setVisibility(View.GONE);
        contentScroll.setVisibility(View.VISIBLE);
    }

    private void showError(String title, String message) {
        contentScroll.setVisibility(View.INVISIBLE);
        stateContainer.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.GONE);
        stateTitleTextView.setVisibility(View.VISIBLE);
        stateMessageTextView.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
        stateTitleTextView.setText(title);
        stateMessageTextView.setText(message);
    }
}
