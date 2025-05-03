package com.abunayem.digibin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private AppCompatButton signUpButton;
    private TextView loginTextView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("customer");

        // Find Views
        nameEditText = findViewById(R.id.editTextText);
        emailEditText = findViewById(R.id.editTextTextEmailAddress2);
        passwordEditText = findViewById(R.id.editTextTextPassword2);
        signUpButton = findViewById(R.id.button);
        loginTextView = findViewById(R.id.textView7);

        // Sign up button click listener
        signUpButton.setOnClickListener(v -> createAccount());

        // Login text click listener
        loginTextView.setOnClickListener(v -> {
            // Navigate to Login activity
            startActivity(new Intent(SignActivity.this, loginActivity.class));
        });
    }

    private void createAccount() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Authentication sign up
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Save user name and email to Firebase Realtime Database
                        String userId = mAuth.getCurrentUser().getUid();
                        myRef.child(userId).child("name").setValue(name); // Save name
                        myRef.child(userId).child("email").setValue(email); // Save email

                        // Navigate to MainActivity and pass the name
                        Intent intent = new Intent(SignActivity.this, loginActivity.class);
                        intent.putExtra("userName", name); // Pass name to MainActivity
                        startActivity(intent);

                        // Show success message and finish this activity
                        Toast.makeText(SignActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(SignActivity.this, "Account creation failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
