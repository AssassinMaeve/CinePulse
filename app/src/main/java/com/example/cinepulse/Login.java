package com.example.cinepulse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/** @noinspection ALL*/
public class Login extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            startActivity(new Intent(Login.this, NavFrag.class));
            finish(); // Close login activity
            return;
        }

        setContentView(R.layout.activity_login);

        // Initialize UI elements AFTER setContentView
        username = findViewById(R.id.username);
        password = findViewById(R.id.Password);
        loginButton = findViewById(R.id.loginbutton);

        // Fullscreen immersive
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

        TextView signuplink = findViewById(R.id.signup);
        signuplink.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });

        // Enable login button only when both fields are filled
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @SuppressLint("UseCompatLoadingForColorStateLists")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                loginButton.setEnabled(!user.isEmpty() && !pass.isEmpty());
                loginButton.setBackgroundTintList(
                        getResources().getColorStateList(user.isEmpty() || pass.isEmpty() ? R.color.grey : R.color.primary_button)
                );
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        // 🔐 Firebase Login Logic
        loginButton.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            // Ensure credentials are valid before making a request
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(Login.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Use Firebase Authentication to sign in with email directly
            db.collection("users")
                    .whereEqualTo("username", user)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String email = queryDocumentSnapshots.getDocuments().get(0).getString("email");

                            if (email != null) {
                                mAuth.signInWithEmailAndPassword(email, pass)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                                                    Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Login.this, NavFrag.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(Login.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(Login.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // Handle password reset logic
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setTitle("Reset Password");

            LinearLayout container = new LinearLayout(Login.this);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setPadding(50, 40, 50, 10);

            final EditText input = new EditText(Login.this);
            input.setHint("Enter your registered email");
            input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            input.setBackground(ContextCompat.getDrawable(Login.this, R.drawable.edittext_border));
            input.setTextColor(Color.WHITE);
            input.setHintTextColor(Color.LTGRAY);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 20, 0, 10);
            input.setLayoutParams(params);

            container.addView(input);
            builder.setView(container);

            builder.setPositiveButton("Send", null);
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dlg -> {
                Button sendButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                sendButton.setOnClickListener(view -> {
                    String email = input.getText().toString().trim();

                    if (email.isEmpty()) {
                        input.setError("Email is required");
                        return;
                    }

                    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    boolean emailExists = task.getResult().getSignInMethods() != null &&
                                            !task.getResult().getSignInMethods().isEmpty();

                                    if (emailExists) {
                                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                                .addOnCompleteListener(sendTask -> {
                                                    if (sendTask.isSuccessful()) {
                                                        Toast.makeText(Login.this,
                                                                "Reset link sent to your email.", Toast.LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                    } else {
                                                        Toast.makeText(Login.this,
                                                                "Failed to send reset email.", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    } else {
                                        input.setError("No account found with this email");
                                    }
                                } else {
                                    Toast.makeText(Login.this,
                                            "Error checking email. Try again.", Toast.LENGTH_LONG).show();
                                }
                            });
                });
            });

            dialog.show();
        });
    }
}
