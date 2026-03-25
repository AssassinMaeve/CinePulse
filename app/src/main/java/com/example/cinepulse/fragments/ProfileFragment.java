package com.example.cinepulse.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cinepulse.Login;
import com.example.cinepulse.NavFrag;
import com.example.cinepulse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView textUsername, textEmail;
    private ImageView imageProfile;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String uid = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshProfile);
        imageProfile = view.findViewById(R.id.imageProfile);
        textUsername = view.findViewById(R.id.textUsername);
        textEmail = view.findViewById(R.id.textEmail);
        View cardLogout = view.findViewById(R.id.cardLogout);
        View cardSettings = view.findViewById(R.id.cardSettings);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            uid = user.getUid();
            textEmail.setText(user.getEmail());
            loadUserData(user);
            loadSelectedAvatar();
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.primary_blue);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (user != null) {
                loadUserData(user);
                loadSelectedAvatar();
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        cardLogout.setOnClickListener(v -> logout());
        cardSettings.setOnClickListener(v -> openSettings());

        return view;
    }

    private void loadUserData(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("uid", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String username = document.getString("username");
                        textUsername.setText(username != null ? username : "User");
                    } else {
                        textUsername
                                .setText(user.getDisplayName() != null ? user.getDisplayName() : "Movie Enthusiast");
                    }
                });
    }

    private void loadSelectedAvatar() {
        if (uid != null && isAdded()) {
            SharedPreferences userPrefs = requireContext().getSharedPreferences("UserThemePrefs", Context.MODE_PRIVATE);
            int avatarResId = userPrefs.getInt(uid + "_avatarResId", R.drawable.profile_user);
            imageProfile.setImageResource(avatarResId);
        }
    }

    private void openSettings() {
        Intent intent = new Intent(getActivity(), NavFrag.class);
        intent.putExtra("openFragment", "settings");
        startActivity(intent);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null)
            getActivity().finish();
    }
}
