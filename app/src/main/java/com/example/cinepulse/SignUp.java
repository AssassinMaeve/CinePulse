package com.example.cinepulse;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    // UI elements
    EditText username, password, confirmPassword, email;
    Button signUpButton;
    TextView loginLink;

    // Firebase instances
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set immersive fullscreen mode for a clean UI experience
        setImmersiveMode();

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind UI elements
        username = findViewById(R.id.usernameSignup);
        password = findViewById(R.id.passwordSignup);
        confirmPassword = findViewById(R.id.confirmPasswordSignup); // <-- New field
        email = findViewById(R.id.emailAddress);
        signUpButton = findViewById(R.id.signUp);
        loginLink = findViewById(R.id.alreadymember);

        // Enable sign-up button only when all fields are filled
        enableSignUpButtonOnTextChanged();

        // Set login link action
        loginLink.setOnClickListener(v -> startActivity(new Intent(SignUp.this, Login.class)));

        // Set sign-up button action
        signUpButton.setOnClickListener(v -> handleSignUp());
    }

    // Method to handle immersive fullscreen mode
    private void setImmersiveMode() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    // Method to enable the SignUp button only when all fields are filled
    private void enableSignUpButtonOnTextChanged() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean allFilled = !username.getText().toString().trim().isEmpty()
                        && !password.getText().toString().trim().isEmpty()
                        && !confirmPassword.getText().toString().trim().isEmpty()
                        && !email.getText().toString().trim().isEmpty();

                signUpButton.setEnabled(allFilled);
                signUpButton.setBackgroundTintList(getResources().getColorStateList(
                        allFilled ? R.color.primary_button : R.color.grey
                ));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        confirmPassword.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
    }

    // Method to handle user sign-up
    private void handleSignUp() {
        String user = username.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();

        // Check if the passwords match
        if (!userPassword.equals(userConfirmPassword)) {
            Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate the password strength
        if (!isPasswordStrong(userPassword)) {
            Toast.makeText(SignUp.this, "Password is too weak. It must contain at least one lowercase, one uppercase, one number, one special character, and be at least 8 characters long.", Toast.LENGTH_LONG).show();
            return;
        }

        // Check if the username already exists
        db.collection("users")
                .document(user)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Username already taken
                        Toast.makeText(SignUp.this, "Username already taken", Toast.LENGTH_SHORT).show();
                    } else {
                        // Create the user in Firebase Authentication
                        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // User created successfully
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        if (firebaseUser != null) {
                                            // Send email verification
                                            sendEmailVerification(firebaseUser, userEmail);
                                        }

                                        // Save user info in Firestore
                                        saveUserInfoToFirestore(firebaseUser, user, userEmail);
                                    } else {
                                        Toast.makeText(SignUp.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignUp.this, "Failed to check username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Method to send email verification
    private void sendEmailVerification(FirebaseUser firebaseUser, String userEmail) {
        firebaseUser.sendEmailVerification()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignUp.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    startActivity(new Intent(SignUp.this, Login.class)); // Redirect to login page
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignUp.this, "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Method to save user information in Firestore
    private void saveUserInfoToFirestore(FirebaseUser firebaseUser, String user, String userEmail) {
        String uid = firebaseUser.getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", user);
        userMap.put("email", userEmail);
        userMap.put("uid", uid);

        db.collection("users").document(user)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    // User info saved successfully
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignUp.this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Method to check if the password is strong
    private boolean isPasswordStrong(String password) {
        // Password must have:
        // - At least 8 characters
        // - At least one lowercase letter
        // - At least one uppercase letter
        // - At least one digit
        // - At least one special character
        return password.length() >= 8 &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}
