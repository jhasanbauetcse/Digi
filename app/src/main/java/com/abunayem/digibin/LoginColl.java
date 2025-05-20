package com.abunayem.digibin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;

public class LoginColl extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore with offline persistence
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        // Check if user is already logged in
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            verifyCollector(currentUser.getUid());
            return; // Skip the rest of onCreate if user is already logged in
        }

        setContentView(R.layout.activity_coll_login);

        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextTextPassword);

        findViewById(R.id.loginButton).setOnClickListener(v -> loginUser());
        findViewById(R.id.textView4).setOnClickListener(v ->
                startActivity(new Intent(LoginColl.this, SignupColl.class)));
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginColl.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        verifyCollector(mAuth.getCurrentUser().getUid());
                    } else {
                        Toast.makeText(LoginColl.this,
                                "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyCollector(String uid) {
        // First try to get from cache
        firestore.collection("collector").document(uid)
                .get(Source.CACHE)
                .addOnCompleteListener(cacheTask -> {
                    if (cacheTask.isSuccessful() && cacheTask.getResult().exists()) {
                        proceedToMainActivity();
                    } else {
                        // If not in cache or cache failed, try server
                        firestore.collection("collector").document(uid).get()
                                .addOnCompleteListener(serverTask -> {
                                    if (serverTask.isSuccessful() && serverTask.getResult().exists()) {
                                        proceedToMainActivity();
                                    } else {
                                        mAuth.signOut();
                                        Toast.makeText(LoginColl.this,
                                                "Access restricted to collectors only",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
    }

    private void proceedToMainActivity() {
        startActivity(new Intent(LoginColl.this, CollMainActivity.class));
        finish();
    }
}