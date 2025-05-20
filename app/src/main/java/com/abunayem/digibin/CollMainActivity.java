package com.abunayem.digibin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class CollMainActivity extends AppCompatActivity {

    private ImageView profileIcon, logoutIcon;
    private ImageView collectionRequestIcon, collectionBox, profileImage;
    private TextView collectionRequestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coll_main);

        // Check network status
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this,
                    "Offline mode - some features may be limited",
                    Toast.LENGTH_LONG).show();
        }

        // Initialize views
        profileIcon = findViewById(R.id.imageView103);
        logoutIcon = findViewById(R.id.imageView1055);
        collectionRequestIcon = findViewById(R.id.imageView1053);
        collectionRequestText = findViewById(R.id.textView26858);
        collectionBox = findViewById(R.id.imageView93);
        profileImage = findViewById(R.id.imageView95); // Add this line for profile image

        // Set click listeners
        profileIcon.setOnClickListener(v -> {
            startActivity(new Intent(this, collprofile.class));
        });

        // Add click listener for profile image (imageView95)
        profileImage.setOnClickListener(v -> {
            startActivity(new Intent(this, collprofile.class));
        });

        logoutIcon.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginColl.class));
            finish();
        });

        // Common click listener for collection request elements
        View.OnClickListener collectionRequestListener = v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                startActivity(new Intent(this, Status_Collector.class));
            } else {
                Toast.makeText(this,
                        "This feature requires internet connection",
                        Toast.LENGTH_LONG).show();
            }
        };

        collectionRequestIcon.setOnClickListener(collectionRequestListener);
        collectionRequestText.setOnClickListener(collectionRequestListener);
        collectionBox.setOnClickListener(collectionRequestListener);
    }
}