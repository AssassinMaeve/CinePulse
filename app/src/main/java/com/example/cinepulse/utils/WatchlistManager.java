package com.example.cinepulse.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.cinepulse.models.WatchlistItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WatchlistManager {

    private static final String PREF_NAME = "cinepulse_watchlist";
    private static final String WATCHLIST_KEY = "watchlist_items";
    private static final String TAG = "WatchlistManager"; // For logging

    // Modified to return boolean for success/failure
    public static boolean addToWatchlist(Context context, WatchlistItem item) {
        try {
            List<WatchlistItem> currentList = getWatchlist(context);

            // Check if item already exists (using isInWatchlist for better reliability)
            if (!isInWatchlist(context, item.getId())) {
                currentList.add(item);
                saveWatchlist(context, currentList);
                Log.d(TAG, "Item added to watchlist: " + item.getId());
                return true;
            } else {
                Log.d(TAG, "Item already in watchlist: " + item.getId());
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding to watchlist", e);
            return false;
        }
    }

    // Modified to return boolean for success/failure
    public static void removeFromWatchlist(Context context, int itemId) {
        try {
            List<WatchlistItem> currentList = getWatchlist(context);
            int initialSize = currentList.size();
            currentList.removeIf(item -> item.getId() == itemId);

            if (currentList.size() != initialSize) {
                saveWatchlist(context, currentList);
                Log.d(TAG, "Item removed from watchlist: " + itemId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error removing from watchlist", e);
        }
    }

    public static List<WatchlistItem> getWatchlist(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(WATCHLIST_KEY, null);

        if (json == null) {
            return new ArrayList<>();
        }

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<WatchlistItem>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing watchlist", e);
            return new ArrayList<>();
        }
    }

    private static void saveWatchlist(Context context, List<WatchlistItem> list) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            Gson gson = new Gson();
            String json = gson.toJson(list);
            editor.putString(WATCHLIST_KEY, json);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving watchlist", e);
        }
    }

    public static boolean isInWatchlist(Context context, int itemId) {
        List<WatchlistItem> currentList = getWatchlist(context);
        for (WatchlistItem item : currentList) {
            if (item.getId() == itemId) {
                return true;
            }
        }
        return false;
    }

    public static void clearWatchlist(Context context) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            preferences.edit().remove(WATCHLIST_KEY).apply();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing watchlist", e);
        }
    }
}