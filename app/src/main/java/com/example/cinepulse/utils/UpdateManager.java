package com.example.cinepulse.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.cinepulse.BuildConfig;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class UpdateManager {

    private static final String TAG = "UpdateManager";
    private static final String KEY_MIN_VERSION_CODE = "min_version_code";
    private static final String KEY_UPDATE_URL = "update_url";

    public static void checkForUpdate(Activity activity) {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        
        // Define default values
        Map<String, Object> defaults = new HashMap<>();
        defaults.put(KEY_MIN_VERSION_CODE, (long) BuildConfig.VERSION_CODE);
        defaults.put(KEY_UPDATE_URL, "https://github.com/AssassinMaeve/CinePulse/releases");

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600) // Fetch every hour
                .build();

        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(defaults);

        remoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long minVersionCode = remoteConfig.getLong(KEY_MIN_VERSION_CODE);
                String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);
                
                Log.d(TAG, "Min version: " + minVersionCode + ", Current: " + BuildConfig.VERSION_CODE);

                if (BuildConfig.VERSION_CODE < minVersionCode) {
                    showUpdateDialog(activity, updateUrl);
                }
            } else {
                Log.w(TAG, "Remote Config fetch failed");
            }
        });
    }

    private static void showUpdateDialog(Activity activity, String updateUrl) {
        if (activity.isFinishing()) return;

        new MaterialAlertDialogBuilder(activity)
                .setTitle("Update Available")
                .setMessage("A new version of CinePulse is available. Please update to continue using the app.")
                .setCancelable(false)
                .setPositiveButton("Update Now", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
                    activity.startActivity(intent);
                })
                .show();
    }
}
