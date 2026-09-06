package com.citycollege.musicexplorer;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.citycollege.musicexplorer.ui.ArtistDetailFragment;
import com.citycollege.musicexplorer.ui.BrowseFragment;
import com.citycollege.musicexplorer.ui.CollectionFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topAppBar = findViewById(R.id.topAppBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        topAppBar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        bottomNavigation.setOnItemSelectedListener(this::onBottomNavSelected);

        if (savedInstanceState == null) {
            showBrowse();
        }
    }

    private boolean onBottomNavSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_browse) {
            showBrowse();
            return true;
        }
        if (item.getItemId() == R.id.nav_collection) {
            showCollection();
            return true;
        }
        return false;
    }

    public void showBrowse() {
        topAppBar.setTitle("Music Explorer");
        topAppBar.setNavigationIcon(null);
        bottomNavigation.setVisibility(View.VISIBLE);
        replace(new BrowseFragment(), false);
    }

    public void showCollection() {
        topAppBar.setTitle("My Collection");
        topAppBar.setNavigationIcon(null);
        bottomNavigation.setVisibility(View.VISIBLE);
        replace(new CollectionFragment(), false);
    }

    public void openArtistDetail(String artistName) {
        topAppBar.setTitle("Artist Detail");
        topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
        bottomNavigation.setVisibility(View.GONE);
        replace(ArtistDetailFragment.newInstance(artistName), true);
    }

    private void replace(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        } else {
            getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            topAppBar.setNavigationIcon(null);
            bottomNavigation.setVisibility(View.VISIBLE);
        }
    }
}
