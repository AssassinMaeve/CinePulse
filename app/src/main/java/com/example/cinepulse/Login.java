package com.example.cinepulse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
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

public class Login extends AppCompatActivity {

    private GestureDetector gestureDetector;

    EditText username;
    EditText password;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (!user.isEmpty() && !pass.isEmpty()) {
                    loginButton.setEnabled(true);
                    loginButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary_button));
                } else {
                    loginButton.setEnabled(false);
                    loginButton.setBackgroundTintList(getResources().getColorStateList(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        // 🔐 Firebase Login Logic
        loginButton.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            db.collection("users")
                    .whereEqualTo("username", user)  // Query by username field
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Retrieve the email from the first document in the query result
                            String email = queryDocumentSnapshots.getDocuments().get(0).getString("email");

                            assert email != null;
                            mAuth.signInWithEmailAndPassword(email, pass)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                            if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                                                // Proceed to the Home activity if email is verified
                                                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Login.this, Home.class));
                                                finish();
                                            } else {
                                                // If the email is not verified
                                                Toast.makeText(Login.this, "Please verify your email address before logging in.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(Login.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Login.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        TextView forgotPassword = findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setTitle("Reset Password");

            // Create a vertical layout container
            LinearLayout container = new LinearLayout(Login.this);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setPadding(50, 40, 50, 10); // Equal padding to match the dialog title

            final EditText input = new EditText(Login.this);
            input.setHint("Enter your registered email");
            input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            input.setBackground(ContextCompat.getDrawable(Login.this, R.drawable.edittext_border));
            input.setTextColor(Color.WHITE);
            input.setHintTextColor(Color.LTGRAY);

            // Set layout parameters with top margin for spacing
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 20, 0, 10); // Top and bottom margin for better spacing
            input.setLayoutParams(params);

            container.addView(input);
            builder.setView(container);

            builder.setPositiveButton("Send", null); // Override later
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    // Gesture Detector Class for Swipe
    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Swipe Right Detected - Go Back to MainActivity
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
            return false;
        }
    }


}
