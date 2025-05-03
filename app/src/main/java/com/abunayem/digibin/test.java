package com.abunayem.digibin;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class test extends AppCompatActivity {

    private TextView textView18;
    private ImageView imageView12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        // Initialize views
        textView18 = findViewById(R.id.textView18);
        imageView12 = findViewById(R.id.imageView12);

        // Example: You can set text or image dynamically if needed
        textView18.setText("Please scan your fingerprint");

        // If you want to change image dynamically (optional)
        // imageView12.setImageResource(R.drawable.new_image);
    }
}