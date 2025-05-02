package com.example.cinepulse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cinepulse.ToggleTheme.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class Profile extends BaseActivity {

    private TextView textUsername;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView imageProfile = findViewById(R.id.imageProfile);
        textUsername = findViewById(R.id.textUsername);
        TextView textEmail = findViewById(R.id.textEmail);
        Button buttonLogout = findViewById(R.id.buttonLogout);
        Button buttonSetting = findViewById(R.id.buttonSettings);

        // Load user info
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            textEmail.setText(user.getEmail());

            // Check if displayName is available, if not, fetch username (document ID) from Firestore
            if (user.getDisplayName() != null) {
                textUsername.setText(user.getDisplayName());
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .whereEqualTo("uid", user.getUid())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                                String username = document.getId(); // Document ID is username
                                textUsername.setText(username);
                            } else {
                                textUsername.setText("User");
                            }
                        })
                        .addOnFailureListener(e -> {
                            textUsername.setText("User");
                            Log.e("Profile", "Error fetching username", e);
                        });
            }

            // Load profile picture
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl()).into(imageProfile);
            } else {
                imageProfile.setImageResource(R.drawable.profile_user);
            }
        }

        buttonSetting.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        // Logout
        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Profile.this, "Signed out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Profile.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(Profile.this, Search.class));
                return true;
            } else if (itemId == R.id.nav_watchlist) {
                startActivity(new Intent(Profile.this, WatchlistActivity.class));
                return true;
            } else if (itemId == R.id.nav_populartrailer) {
                startActivity(new Intent(Profile.this, PopularTrailerActivity.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent intent = new Intent(Profile.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
