package com.citycollege.musicexplorer.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.citycollege.musicexplorer.MainActivity;
import com.citycollege.musicexplorer.R;
import com.citycollege.musicexplorer.model.Artist;
import com.citycollege.musicexplorer.network.ApiCallback;
import com.citycollege.musicexplorer.network.LastFmApiClient;
import com.citycollege.musicexplorer.network.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class BrowseFragment extends Fragment {
    private final LastFmApiClient apiClient = new LastFmApiClient();
    private ArtistAdapter adapter;
    private TrackAdapter chartTrackAdapter;
    private RecyclerView recyclerView;
    private View contentScroll;
    private View tracksSection;
    private TextView artistsHeading;
    private View stateContainer;
    private CircularProgressIndicator progressIndicator;
    private TextView stateTitleTextView;
    private TextView stateMessageTextView;
    private MaterialButton retryButton;
    private TextInputEditText searchEditText;
    private TextInputLayout searchInputLayout;
    private boolean showingSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_browse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.artistsRecyclerView);
        RecyclerView chartTracksRecyclerView = view.findViewById(R.id.chartTracksRecyclerView);
        contentScroll = view.findViewById(R.id.contentScroll);
        tracksSection = view.findViewById(R.id.tracksSection);
        artistsHeading = view.findViewById(R.id.artistsHeading);
        searchEditText = view.findViewById(R.id.searchEditText);
        searchInputLayout = view.findViewById(R.id.searchInputLayout);
        stateContainer = view.findViewById(R.id.stateContainer);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        stateTitleTextView = view.findViewById(R.id.stateTitleTextView);
        stateMessageTextView = view.findViewById(R.id.stateMessageTextView);
        retryButton = view.findViewById(R.id.retryButton);

        adapter = new ArtistAdapter(artist -> ((MainActivity) requireActivity()).openArtistDetail(artist.name));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        chartTrackAdapter = new TrackAdapter();
        chartTracksRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chartTracksRecyclerView.setAdapter(chartTrackAdapter);

        searchInputLayout.setEndIconOnClickListener(v -> search());
        searchEditText.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
                return true;
            }
            return false;
        });
        view.findViewById(R.id.refreshFab).setOnClickListener(v -> loadTopArtists());
        retryButton.setOnClickListener(v -> {
            if (showingSearch) {
                search();
            } else {
                loadTopArtists();
            }
        });
        loadTopArtists();
    }

    private void loadTopArtists() {
        showingSearch = false;
        if (!NetworkUtils.isOnline(requireContext())) {
            showError("You are offline", "Connect to the internet to load global charts.");
            return;
        }
        showLoading();
        apiClient.getTopArtists(new ApiCallback<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result) {
                adapter.submitList(result);
                tracksSection.setVisibility(View.VISIBLE);
                artistsHeading.setText("Top Artists");
                loadTopTracks();
            }

            @Override
            public void onError(String message) {
                showError("Could not load charts", message);
            }
        });
    }

    private void loadTopTracks() {
        apiClient.getTopChartTracks(new ApiCallback<List<com.citycollege.musicexplorer.model.Track>>() {
            @Override
            public void onSuccess(List<com.citycollege.musicexplorer.model.Track> result) {
                chartTrackAdapter.submitList(result);
                showContent();
            }

            @Override
            public void onError(String message) {
                chartTrackAdapter.submitList(java.util.Collections.emptyList());
                showContent();
            }
        });
    }

    private void search() {
        String query = searchEditText.getText() == null ? "" : searchEditText.getText().toString().trim();
        if (query.isEmpty()) {
            loadTopArtists();
            return;
        }
        showingSearch = true;
        if (!NetworkUtils.isOnline(requireContext())) {
            showError("You are offline", "Connect to the internet to search artists.");
            return;
        }
        showLoading();
        apiClient.searchArtists(query, new ApiCallback<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result) {
                adapter.submitList(result);
                tracksSection.setVisibility(View.GONE);
                artistsHeading.setText("Search Results");
                showContent();
            }

            @Override
            public void onError(String message) {
                showError("Search failed", message);
            }
        });
    }

    private void showLoading() {
        contentScroll.setVisibility(View.INVISIBLE);
        stateContainer.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.VISIBLE);
        stateTitleTextView.setVisibility(View.VISIBLE);
        stateMessageTextView.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        stateTitleTextView.setText("Loading music data");
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
