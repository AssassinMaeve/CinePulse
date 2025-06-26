package com.example.cinepulse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    // Declare UI elements
    private EditText editUsername, editEmail, editOldPassword, editNewPassword;
    private Button buttonToggleTheme;
    private boolean isDarkMode = false; // Flag for theme mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI elements
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editOldPassword = findViewById(R.id.editOldPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        Button buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonToggleTheme = findViewById(R.id.buttonToggleTheme);

        // Fetch and apply the saved theme mode
        SharedPreferences prefs = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        isDarkMode = prefs.getBoolean("isDarkMode", false);
        setAppTheme(isDarkMode);

        // Set the logo according to the current theme
        setLogoBasedOnTheme();

        // Save user changes
        buttonSaveChanges.setOnClickListener(v -> saveUserChanges());

        // Toggle between Dark and Light modes
        buttonToggleTheme.setOnClickListener(v -> toggleTheme(prefs));
    }

    /**
     * Set the app theme based on user preference
     * @param isDarkMode true for dark mode, false for light mode
     */
    private void setAppTheme(boolean isDarkMode) {
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    /**
     * Set the app logo depending on the theme
     */
    private void setLogoBasedOnTheme() {
        ImageView logo = findViewById(R.id.imageView2);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        logo.setImageResource(nightModeFlags == Configuration.UI_MODE_NIGHT_YES
                ? R.drawable.cinepulsewhitelogo : R.drawable.cinepulseblacklogo);
    }

    /**
     * Handles saving changes to the user profile
     */
    private void saveUserChanges() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String newUsername = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();
            String oldPassword = editOldPassword.getText().toString();
            String newPassword = editNewPassword.getText().toString();

            // Update username, email, or password if fields are not empty
            if (!newUsername.isEmpty()) updateUsername(user, newUsername);
            if (!newEmail.isEmpty()) updateEmail(user, newEmail, oldPassword);
            if (!oldPassword.isEmpty() && !newPassword.isEmpty())
                updatePassword(user, oldPassword, newPassword);
        }
    }

    /**
     * Toggle between Dark and Light modes and save preference
     * @param prefs SharedPreferences to save the mode
     */
    private void toggleTheme(SharedPreferences prefs) {
        isDarkMode = !isDarkMode;
        setAppTheme(isDarkMode);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isDarkMode", isDarkMode);
        editor.apply();

        // Recreate the activity to apply theme change
        buttonToggleTheme.postDelayed(this::recreate, 100);
    }

    /**
     * Update the user's username in Firestore
     * @param user FirebaseUser instance
     * @param newUsername New username to set
     */
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
                        db.collection("users").whereEqualTo("uid", uid).get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        String currentUsername = querySnapshot.getDocuments().get(0).getId();
                                        Map<String, Object> userData = querySnapshot.getDocuments().get(0).getData();
                                        assert userData != null;
                                        userData.put("username", newUsername);

                                        // Save the updated username and delete the old one
                                        db.collection("users").document(newUsername).set(userData)
                                                .addOnSuccessListener(aVoid -> db.collection("users").document(currentUsername)
                                                        .delete()
                                                        .addOnSuccessListener(aVoid2 -> Toast.makeText(this, "Username updated", Toast.LENGTH_SHORT).show())
                                                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete old username", Toast.LENGTH_SHORT).show()))
                                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to create new username", Toast.LENGTH_SHORT).show());
                                    } else {
                                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching user info", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to check username availability", Toast.LENGTH_SHORT).show());
    }

    /**
     * Update the user's email after re-authenticating
     * @param user FirebaseUser instance
     * @param newEmail New email address
     * @param oldPassword User's current password for re-authentication
     */
    private void updateEmail(FirebaseUser user, String newEmail, String oldPassword) {
        if (oldPassword.isEmpty()) {
            Toast.makeText(this, "Please enter your current password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate the user with the old password
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), oldPassword);

        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // Update email after successful re-authentication
                    user.updateEmail(newEmail)
                            .addOnSuccessListener(aVoid1 -> {
                                // Send verification email
                                user.sendEmailVerification()
                                        .addOnSuccessListener(aVoid2 -> {
                                            Toast.makeText(this, "Email updated successfully. Please verify your new email.", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();

                                            // Redirect user to login screen
                                            Intent intent = new Intent(SettingsActivity.this, NavFrag.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Email update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Reauthentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Update the user's password after re-authenticating
     * @param user FirebaseUser instance
     * @param oldPass User's current password
     * @param newPass New password to set
     */
    private void updatePassword(FirebaseUser user, String oldPass, String newPass) {
        if (!isPasswordStrong(newPass)) {
            Toast.makeText(this, "Weak password. Ensure it has:\n• At least 8 characters\n• 1 uppercase letter\n• 1 lowercase letter\n• 1 number\n• 1 special character", Toast.LENGTH_LONG).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), oldPass);
        user.reauthenticate(credential).addOnSuccessListener(aVoid -> user.updatePassword(newPass)
                .addOnSuccessListener(task -> Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Password update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show())).addOnFailureListener(e -> Toast.makeText(this, "Reauthentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Validate password strength based on specific rules
     * @param password The password to validate
     * @return true if password meets the criteria, false otherwise
     */
    private boolean isPasswordStrong(String password) {
        return password.length() >= 8
                && password.matches(".*[a-z].*")
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}
