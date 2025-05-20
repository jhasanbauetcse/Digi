package com.abunayem.digibin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    Button customerButton;
    Button collectorButton;
    Button adminButton; // Declare admin button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Initialize buttons
        customerButton = findViewById(R.id.customerButton);
        collectorButton = findViewById(R.id.collectorButton);
        adminButton = findViewById(R.id.adminButton); // Initialize admin button

        // Set OnClickListener for customerButton
        customerButton.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, loginActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener for collectorButton
        collectorButton.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, LoginColl.class);
            startActivity(intent);
        });

        // Set OnClickListener for adminButton
        adminButton.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, adminlogin.class);
            startActivity(intent);
        });
    }
}
