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

    EditText username, password, confirmPassword, email;
    Button signUpButton;
    TextView loginLink;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Immersive fullscreen
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

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind Views
        username = findViewById(R.id.usernameSignup);
        password = findViewById(R.id.passwordSignup);
        confirmPassword = findViewById(R.id.confirmPasswordSignup); // <-- New field
        email = findViewById(R.id.emailAddress);
        signUpButton = findViewById(R.id.signUp);
        loginLink = findViewById(R.id.alreadymember);

        // Enable button only when all fields filled
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
            public void afterTextChanged(Editable s) {
            }
        };

        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        confirmPassword.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(SignUp.this, Login.class));
        });

        signUpButton.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            String userConfirmPassword = confirmPassword.getText().toString().trim();

            if (!userPassword.equals(userConfirmPassword)) {
                Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPasswordStrong(userPassword)) {
                Toast.makeText(SignUp.this, "Password is too weak. It must contain at least one lowercase, one uppercase, one number, one special character, and be at least 8 characters long.", Toast.LENGTH_LONG).show();
                return;
            }

            db.collection("users")
                    .document(user)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(SignUp.this, "Username already taken", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // User is created successfully
                                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                            if (firebaseUser != null) {
                                                // Send email verification
                                                firebaseUser.sendEmailVerification()
                                                        .addOnSuccessListener(aVoid -> {
                                                            // Email verification sent
                                                            Toast.makeText(SignUp.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                                            // Optionally log out the user after sending the verification email
                                                            mAuth.signOut();
                                                            // Redirect to login page after verification email is sent
                                                            startActivity(new Intent(SignUp.this, Login.class));
                                                            finish(); // Close SignUp activity
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Failed to send verification email
                                                            Toast.makeText(SignUp.this, "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            }

                                            // Save user info to Firestore
                                            String uid = firebaseUser.getUid();
                                            Map<String, Object> userMap = new HashMap<>();
                                            userMap.put("username", user);
                                            userMap.put("email", userEmail);
                                            userMap.put("uid", uid);

                                            db.collection("users").document(user)
                                                    .set(userMap)
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Save user info successful
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(SignUp.this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toast.makeText(SignUp.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUp.this, "Failed to check username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
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
