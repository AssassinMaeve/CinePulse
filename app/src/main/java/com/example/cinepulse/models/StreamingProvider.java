package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

public class StreamingProvider {
    @SerializedName("provider_id")
    private int providerId;

    @SerializedName("provider_name")
    private String providerName;

    @SerializedName("logo_path")
    private String logoPath;

    // Getters
    public int getProviderId() {
        return providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getLogoPath() {
        return logoPath;
    }
}

