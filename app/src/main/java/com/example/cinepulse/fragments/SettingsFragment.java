package com.example.cinepulse.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.cinepulse.NavFrag;
import com.example.cinepulse.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class SettingsFragment extends Fragment {

    private EditText editUsername, editEmail, editOldPassword, editNewPassword;
    private Button buttonToggleTheme;
    private boolean isDarkMode = false;
    private SharedPreferences userPrefs;
    private String uid = null;
    private boolean isToggling = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        editUsername = view.findViewById(R.id.editUsername);
        editEmail = view.findViewById(R.id.editEmail);
        editOldPassword = view.findViewById(R.id.editOldPassword);
        editNewPassword = view.findViewById(R.id.editNewPassword);
        Button buttonSaveChanges = view.findViewById(R.id.buttonSaveChanges);
        buttonToggleTheme = view.findViewById(R.id.buttonToggleTheme);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            userPrefs = requireContext().getSharedPreferences("UserThemePrefs", requireContext().MODE_PRIVATE);
            isDarkMode = userPrefs.getBoolean(uid + "_isDarkMode", false);
            // Do not apply theme here again
        }

        setLogoBasedOnTheme(view);

        buttonSaveChanges.setOnClickListener(v -> saveUserChanges());
        buttonToggleTheme.setOnClickListener(v -> toggleTheme());

        return view;
    }

    private void toggleTheme() {
        if (isToggling || uid == null || userPrefs == null) return;
        isToggling = true;

        isDarkMode = !isDarkMode;
        userPrefs.edit().putBoolean(uid + "_isDarkMode", isDarkMode).apply();

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (getContext() == null) return; // Don't proceed if the fragment is detached

            Intent intent = new Intent(getContext(), NavFrag.class);
            intent.putExtra("openFragment", "settings");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            if (getActivity() != null) getActivity().finish();
        }, 300);

    }




    private void setLogoBasedOnTheme(View view) {
        ImageView logo = view.findViewById(R.id.imageView2);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        logo.setImageResource(nightModeFlags == Configuration.UI_MODE_NIGHT_YES
                ? R.drawable.cinepulsewhitelogo : R.drawable.cinepulseblacklogo);
    }

    private void saveUserChanges() {
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
    }

    private void updateUsername(FirebaseUser user, String newUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = user.getUid();

        db.collection("users").document(newUsername).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(getContext(), "Username already taken. Please choose another.", Toast.LENGTH_SHORT).show();
                    } else {
                        db.collection("users").whereEqualTo("uid", uid).get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        String currentUsername = querySnapshot.getDocuments().get(0).getId();
                                        Map<String, Object> userData = querySnapshot.getDocuments().get(0).getData();
                                        assert userData != null;
                                        userData.put("username", newUsername);

                                        db.collection("users").document(newUsername).set(userData)
                                                .addOnSuccessListener(aVoid -> db.collection("users").document(currentUsername)
                                                        .delete()
                                                        .addOnSuccessListener(aVoid2 -> Toast.makeText(getContext(), "Username updated", Toast.LENGTH_SHORT).show())
                                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete old username", Toast.LENGTH_SHORT).show()))
                                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create new username", Toast.LENGTH_SHORT).show());
                                    } else {
                                        Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching user info", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to check username availability", Toast.LENGTH_SHORT).show());
    }

    private void updateEmail(FirebaseUser user, String newEmail, String oldPassword) {
        if (oldPassword.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your current password", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), oldPassword);

        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> user.updateEmail(newEmail)
                        .addOnSuccessListener(aVoid1 -> user.sendEmailVerification()
                                .addOnSuccessListener(aVoid2 -> {
                                    Toast.makeText(getContext(), "Email updated. Please verify your new email.", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getActivity(), NavFrag.class));
                                    requireActivity().finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Email update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Reauthentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updatePassword(FirebaseUser user, String oldPass, String newPass) {
        if (!isPasswordStrong(newPass)) {
            Toast.makeText(getContext(), "Weak password. Use at least 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special character.", Toast.LENGTH_LONG).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), oldPass);

        user.reauthenticate(credential).addOnSuccessListener(aVoid -> user.updatePassword(newPass)
                        .addOnSuccessListener(task -> Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Password update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Reauthentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8
                && password.matches(".*[a-z].*")
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}
