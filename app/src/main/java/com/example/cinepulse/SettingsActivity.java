package com.example.cinepulse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {

    private EditText editUsername, editEmail, editOldPassword, editNewPassword;
    private Button buttonSaveChanges, buttonToggleTheme;
    private boolean isDarkMode = false; // Flag for theme mode


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editOldPassword = findViewById(R.id.editOldPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonToggleTheme = findViewById(R.id.buttonToggleTheme);


        SharedPreferences prefs = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        isDarkMode = prefs.getBoolean("isDarkMode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );


        ImageView logo = findViewById(R.id.imageView2);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            logo.setImageResource(R.drawable.cinepulsewhitelogo);
        } else {
            logo.setImageResource(R.drawable.cinepulseblacklogo);
        }



        buttonSaveChanges.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String newUsername = editUsername.getText().toString().trim();
                String newEmail = editEmail.getText().toString().trim();
                String oldPassword = editOldPassword.getText().toString();
                String newPassword = editNewPassword.getText().toString();

                if (!newUsername.isEmpty()) updateUsername(user, newUsername);
                if (!newEmail.isEmpty()) updateEmail(user, newEmail);
                if (!oldPassword.isEmpty() && !newPassword.isEmpty())
                    updatePassword(user, oldPassword, newPassword);
            }
        });

        buttonToggleTheme.setOnClickListener(v -> {
            isDarkMode = !isDarkMode;
            AppCompatDelegate.setDefaultNightMode(
                    isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isDarkMode", isDarkMode);
            editor.apply();
            buttonToggleTheme.postDelayed(() -> recreate(), 100);
        });

    }

    private void updateUsername(FirebaseUser user, String newUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid())
                .update("username", newUsername)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Username updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("Settings", "Username update failed", e));
    }

    private void updateEmail(FirebaseUser user, String newEmail) {
        user.updateEmail(newEmail)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Email updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("Settings", "Email update failed", e));
    }

    private void updatePassword(FirebaseUser user, String oldPass, String newPass) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
        user.reauthenticate(credential).addOnSuccessListener(aVoid -> {
            user.updatePassword(newPass)
                    .addOnSuccessListener(task -> Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Password update failed", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Toast.makeText(this, "Reauthentication failed", Toast.LENGTH_SHORT).show());
    }
}
