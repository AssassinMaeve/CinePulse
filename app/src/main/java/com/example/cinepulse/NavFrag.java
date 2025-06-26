package com.example.cinepulse;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.cinepulse.fragments.HomeFragment;
import com.example.cinepulse.fragments.SearchFragment;
import com.example.cinepulse.fragments.SettingsFragment;
import com.example.cinepulse.fragments.WatchlistFragment;
import com.example.cinepulse.fragments.PopularTrailerFragment;
import com.example.cinepulse.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NavFrag extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navfrag);

        // ✅ Set the theme based on user preference
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            SharedPreferences prefs = getSharedPreferences("UserThemePrefs", MODE_PRIVATE);
            boolean isDark = prefs.getBoolean(user.getUid() + "_isDarkMode", false);
            AppCompatDelegate.setDefaultNightMode(
                    isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        }

        bottomNav = findViewById(R.id.bottomNavigation);

        // ✅ Load correct fragment based on intent extra
        String openFragment = getIntent().getStringExtra("openFragment");
        if ("settings".equals(openFragment)) {
            loadFragment(new SettingsFragment());
            bottomNav.setSelectedItemId(R.id.nav_profile);
        } else {
            loadFragment(new HomeFragment());
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

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
