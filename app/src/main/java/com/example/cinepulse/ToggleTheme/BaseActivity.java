// BaseActivity.java
package com.example.cinepulse.ToggleTheme;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate and setContentView
        SharedPreferences prefs = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO

        );
        super.onCreate(savedInstanceState);
    }

    @Override
    public void recreate() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.recreate();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
