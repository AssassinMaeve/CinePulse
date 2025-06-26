package com.example.cinepulse.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.cinepulse.Login;
import com.example.cinepulse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView textUsername;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView imageProfile = view.findViewById(R.id.imageProfile);
        textUsername = view.findViewById(R.id.textUsername);
        TextView textEmail = view.findViewById(R.id.textEmail);
        Button buttonLogout = view.findViewById(R.id.buttonLogout);
        Button buttonSetting = view.findViewById(R.id.buttonSettings);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            textEmail.setText(user.getEmail());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereEqualTo("uid", user.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String username = document.getString("username"); // 👈 safer than getId()
                            textUsername.setText(username != null ? username : "User");
                        } else {
                            textUsername.setText("User");
                        }
                    })
                    .addOnFailureListener(e -> {
                        textUsername.setText("User");
                        Log.e("ProfileFragment", "Error fetching username", e);
                    });

            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(requireContext()).load(photoUrl).into(imageProfile);
            } else {
                imageProfile.setImageResource(R.drawable.profile_user);
            }
        }

        buttonSetting.setOnClickListener(v -> {
            SettingsFragment settingsFragment = new SettingsFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, settingsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireContext(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}
