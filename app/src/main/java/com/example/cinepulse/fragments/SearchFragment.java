package com.example.cinepulse.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinepulse.BuildConfig;
import com.example.cinepulse.R;
import com.example.cinepulse.adapters.MultiSearchAdapter;
import com.example.cinepulse.models.MediaItem;
import com.example.cinepulse.models.MultiSearchResponse;
import com.example.cinepulse.network.RetroFitClient;
import com.example.cinepulse.network.TMDbApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerSearchResults;
    private MultiSearchAdapter searchAdapter;
    private EditText editTextSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        editTextSearch = view.findViewById(R.id.editTextSearch);
        recyclerSearchResults = view.findViewById(R.id.recyclerSearchResults);

        recyclerSearchResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerSearchResults.setHasFixedSize(true);

        // Listen for text input
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                if (query.length() > 2) {
                    searchMovies(query.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void searchMovies(String query) {
        TMDbApiService apiService = RetroFitClient.getApiService();
        String apiKey = BuildConfig.TMDB_API_KEY;

        Call<MultiSearchResponse> call = apiService.searchMulti(apiKey, query);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MultiSearchResponse> call, @NonNull Response<MultiSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MediaItem> items = response.body().getResults();
                    List<MediaItem> filteredItems = filterSearchResults(items);

                    searchAdapter = new MultiSearchAdapter((AppCompatActivity) requireContext(), filteredItems);
                    recyclerSearchResults.setAdapter(searchAdapter);
                } else {
                    Toast.makeText(requireContext(), "No results found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MultiSearchResponse> call, @NonNull Throwable t) {
                Log.e("Search", "API failure: " + t.getMessage());
                Toast.makeText(requireContext(), "Failed to fetch search results.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<MediaItem> filterSearchResults(List<MediaItem> items) {
        List<MediaItem> filtered = new ArrayList<>();
        for (MediaItem item : items) {
            if ("movie".equals(item.getMediaType()) || "tv".equals(item.getMediaType())) {
                filtered.add(item);
            }
        }
        return filtered;
    }
}
