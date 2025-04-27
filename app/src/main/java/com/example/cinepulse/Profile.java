package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends AppCompatActivity {

    private ImageView imageProfile;
    private TextView textUsername, textEmail;
    private Button buttonLogout;
    private Button buttonSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageProfile = findViewById(R.id.imageProfile);
        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonSetting = findViewById(R.id.buttonSettings);

        // Load user info
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            textEmail.setText(user.getEmail());
            textUsername.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");

            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl()).into(imageProfile);
            } else {
                imageProfile.setImageResource(R.drawable.profile_user);
            }
        }

        buttonSetting.setOnClickListener(v ->{
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
