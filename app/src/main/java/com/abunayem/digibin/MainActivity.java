package com.abunayem.digibin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavController navController;
    TextView userNameTextView;
    TextView locationTextView;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("customer");

        // Initialize views
        userNameTextView = findViewById(R.id.textView12);  // TextView for user name
        locationTextView = findViewById(R.id.textView13);  // TextView for location

        // Retrieve the location passed from ChooseLocationActivity
        String selectedLocation = getIntent().getStringExtra("selectedLocation");
        if (selectedLocation != null) {
            locationTextView.setText(selectedLocation);
        } else {
            locationTextView.setText("Location");
        }

        // Check if user is signed in
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Retrieve user's name from Firebase Database
            myRef.child(userId).child("name").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String userName = task.getResult().getValue(String.class);
                    if (userName != null) {
                        userNameTextView.setText(userName);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to retrieve name.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            userNameTextView.setText("customer not signed in");
        }

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Find NavController using NavHostFragment
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Set up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}
