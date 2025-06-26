package com.example.cinepulse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cinepulse.fragments.HomeFragment;
import com.example.cinepulse.fragments.SearchFragment;
import com.example.cinepulse.fragments.WatchlistFragment;
import com.example.cinepulse.fragments.PopularTrailerFragment;
import com.example.cinepulse.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavFrag extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navfrag); // Make sure this XML has fragment_container & bottomNavigation

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // ✅ Set default selected item
        bottomNav.setSelectedItemId(R.id.nav_home);

        // ✅ Manually load the HomeFragment on first launch
        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_watchlist) {
                selectedFragment = new WatchlistFragment();
            } else if (itemId == R.id.nav_populartrailer) {
                selectedFragment = new PopularTrailerFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(@NonNull Fragment fragment) {
        if (isFinishing() || isDestroyed()) return;

        if (!getSupportFragmentManager().isStateSaved()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss();
        }
    }

}
