package com.example.cinepulse.ToggleTheme;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * BaseActivity that applies the saved theme (light/dark mode)
 * before any activity UI is created.
 * All activities that need theme switching should extend this class.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme preference before super.onCreate and setContentView
        SharedPreferences prefs = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);

        // Apply dark or light theme
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
    }

    /**
     * Recreate activity with a fade animation for smooth theme transition.
     */
    @Override
    public void recreate() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.recreate();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
