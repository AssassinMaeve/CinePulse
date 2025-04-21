package com.example.cinepulse.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit client for making API requests to TMDB
 * Manages the Retrofit instance creation and provides API service
 */
public class RetroFitClient {

    // Base URL for all API endpoints
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    // Singleton Retrofit instance
    private static Retrofit retrofit;

    /**
     * Provides the TMDB API service interface implementation
     * Creates Retrofit instance if it doesn't exist (lazy initialization)
     *
     * @return TMDbApiService implementation for making API calls
     */
    public static TMDbApiService getApiService() {
        // Create Retrofit instance only once (Singleton pattern)
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)          // Set the base API URL
                    .addConverterFactory(       // Add Gson converter for JSON parsing
                            GsonConverterFactory.create()
                    )
                    .build();                   // Build the Retrofit instance
        }

        // Create and return implementation of the API endpoints interface
        return retrofit.create(TMDbApiService.class);
    }
}