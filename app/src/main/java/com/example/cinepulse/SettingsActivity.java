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

import java.util.Map;

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
                if (!newEmail.isEmpty()) updateEmail(user, newEmail, oldPassword);
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
        String uid = user.getUid();

        // Check if the new username already exists
        db.collection("users").document(newUsername).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Username already taken
                        Toast.makeText(this, "Username already taken. Please choose another.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Update username in Firestore
                        db.collection("users")
                                .whereEqualTo("uid", uid)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        String currentUsername = querySnapshot.getDocuments().get(0).getId();
                                        Map<String, Object> userData = querySnapshot.getDocuments().get(0).getData();
                                        userData.put("username", newUsername);

                                        db.collection("users").document(newUsername)
                                                .set(userData)
                                                .addOnSuccessListener(aVoid -> {
                                                    db.collection("users").document(currentUsername)
                                                            .delete()
                                                            .addOnSuccessListener(aVoid2 -> {
                                                                Toast.makeText(this, "Username updated", Toast.LENGTH_SHORT).show();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(this, "Failed to delete old username", Toast.LENGTH_SHORT).show();
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(this, "Failed to create new username", Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error fetching user info", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to check username availability", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEmail(FirebaseUser user, String newEmail, String oldPassword) {
        if (oldPassword.isEmpty()) {
            Toast.makeText(this, "Please enter your current password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate the user with the old password
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // If reauthentication is successful, proceed to update email
                    user.updateEmail(newEmail)
                            .addOnSuccessListener(aVoid1 -> {
                                // Send verification email
                                user.sendEmailVerification()
                                        .addOnSuccessListener(aVoid2 -> {
                                            Toast.makeText(this, "Email updated successfully. Please verify your new email.", Toast.LENGTH_SHORT).show();

                                            // Optional: Log out the user after email change (this forces re-login after verification)
                                            FirebaseAuth.getInstance().signOut();

                                            // Redirect the user to the home page or login screen
                                            Intent intent = new Intent(SettingsActivity.this, Home.class); // Adjust to your HomeActivity
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Email update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // If reauthentication fails, notify the user
                    Toast.makeText(this, "Reauthentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void updatePassword(FirebaseUser user, String oldPass, String newPass) {
        if (!isPasswordStrong(newPass)) {
            Toast.makeText(this, "Weak password. It must contain:\n• At least 8 characters\n• 1 uppercase letter\n• 1 lowercase letter\n• 1 number\n• 1 special character", Toast.LENGTH_LONG).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
        user.reauthenticate(credential).addOnSuccessListener(aVoid -> {
            user.updatePassword(newPass)
                    .addOnSuccessListener(task -> {
                        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Password update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Reauthentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private boolean isPasswordStrong(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[0-9].*")) return false;
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        return true;
    }
}
