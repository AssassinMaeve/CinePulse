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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    EditText username, password, email;
    Button signUpButton;
    TextView loginLink;

    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Enable immersive fullscreen
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

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // Bind Views
        username = findViewById(R.id.usernameSignup);
        password = findViewById(R.id.passwordSignup);
        email = findViewById(R.id.emailAddress);
        signUpButton = findViewById(R.id.signUp);
        loginLink = findViewById(R.id.alreadymember); // make sure this ID exists in your XML

        // Enable signup button only when all fields are filled
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean allFilled = !username.getText().toString().trim().isEmpty()
                        && !password.getText().toString().trim().isEmpty()
                        && !email.getText().toString().trim().isEmpty();

                signUpButton.setEnabled(allFilled);
                signUpButton.setBackgroundTintList(getResources().getColorStateList(
                        allFilled ? R.color.primary_button : R.color.grey
                ));
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);

        // Redirect to Login Activity
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        });

        // Sign up logic
        signUpButton.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Store username → email in Firestore
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", user);
                            userMap.put("email", userEmail);

                            db.collection("users")
                                    .document(user) // use username as document ID
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUp.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUp.this, Login.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignUp.this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(SignUp.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
