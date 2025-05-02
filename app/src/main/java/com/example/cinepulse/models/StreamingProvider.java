package com.example.cinepulse.models;

import com.google.gson.annotations.SerializedName;

public class StreamingProvider {

    @SerializedName("provider_name")
    private final String providerName;

    @SerializedName("logo_path")
    private final String logoPath;

    public StreamingProvider(String providerName, String logoPath) {
        this.providerName = providerName;
        this.logoPath = logoPath;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getLogoPath() {
        return logoPath;
    }
}

