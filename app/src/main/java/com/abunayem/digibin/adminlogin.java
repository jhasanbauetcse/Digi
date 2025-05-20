package com.abunayem.digibin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;

public class adminlogin extends AppCompatActivity {

    private EditText emailField, passwordField;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);

        // Initialize Firestore with offline persistence
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        mAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.emailEditText);
        passwordField = findViewById(R.id.passwordEditText);

        findViewById(R.id.loginBtn).setOnClickListener(v -> loginAdmin());
      //  findViewById(R.id.signUpTextView).setOnClickListener(v ->
      //          startActivity(new Intent(adminlogin.this, adminsignup.class))
     //   );


        if (mAuth.getCurrentUser() != null) {
            verifyAdminRole(mAuth.getCurrentUser().getUid());
        }
    }

    private void loginAdmin() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Admin email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Admin password is required");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                verifyAdminRole(mAuth.getCurrentUser().getUid());
            } else {
                Toast.makeText(adminlogin.this,
                        "Admin login failed: " + task.getException().getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void verifyAdminRole(String adminId) {
        // Check network status
        boolean isOnline = NetworkUtils.isNetworkAvailable(this);
        Source source = isOnline ? Source.SERVER : Source.CACHE;

        if (!isOnline) {
            Toast.makeText(this, "Offline mode - using cached data", Toast.LENGTH_SHORT).show();
        }

        firestore.collection("Admins").document(adminId).get(source)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && "admin".equals(document.getString("role"))) {
                            startActivity(new Intent(adminlogin.this, adminmain.class));
                            finish();
                        } else {
                            // If cache failed but we're online, try server directly
                            if (source == Source.CACHE && isOnline) {
                                verifyAdminRole(adminId); // Retry with server source
                                return;
                            }
                            mAuth.signOut();
                            Toast.makeText(adminlogin.this,
                                    "Access restricted to administrators only",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(adminlogin.this,
                                "Error verifying admin credentials",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}