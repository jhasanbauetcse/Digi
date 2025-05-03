package com.abunayem.digibin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class pickuprequest extends AppCompatActivity {

    // Declare the views (EditText, AutoCompleteTextView, etc.)
    private EditText editTextPhone, editTextAddress, editTextName;
    private TextInputLayout locationInputLayout, wasteInputLayout;
    private AutoCompleteTextView locationAutoCompleteTextView, wasteAutoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickuprequest);  // Set the layout

        // Initialize the views
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextText3);
        editTextName = findViewById(R.id.editTextText3);
        locationInputLayout = findViewById(R.id.locationInputLayout);
        locationAutoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        wasteInputLayout = findViewById(R.id.locationInputLayout2);
        wasteAutoCompleteTextView = findViewById(R.id.autoCompleteTextView2);

        // Create a sample list for the schedule dropdown (you can modify this list)
        String[] locations = new String[]{"06:00 AM", "10:00 AM", "02:00 PM", "06:00 PM", "08:00 PM"};

        // Create an ArrayAdapter using the sample locations
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);

        // Set the adapter to the AutoCompleteTextView for schedule
        locationAutoCompleteTextView.setAdapter(locationAdapter);

        // Create a list of waste types for the dropdown
        String[] wasteTypes = new String[]{"Plastic", "Paper", "Glass", "Metal", "Organic"};

        // Create an ArrayAdapter using the waste types
        ArrayAdapter<String> wasteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, wasteTypes);

        // Set the adapter to the AutoCompleteTextView for waste type
        wasteAutoCompleteTextView.setAdapter(wasteAdapter);

        // Find the "Proceed" button and set its click listener
        findViewById(R.id.button20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleProceedButtonClick();
            }
        });
    }

    private void handleProceedButtonClick() {
        // Get the values entered by the user
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String location = locationAutoCompleteTextView.getText().toString().trim();
        String wasteType = wasteAutoCompleteTextView.getText().toString().trim();

        // Perform validation
        if (phone.isEmpty()) {
            editTextPhone.setError("Phone number is required");
            return;
        }
        if (address.isEmpty()) {
            editTextAddress.setError("Address is required");
            return;
        }
        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            return;
        }
        if (location.isEmpty()) {
            locationAutoCompleteTextView.setError("Location is required");
            return;
        }
        if (wasteType.isEmpty()) {
            wasteAutoCompleteTextView.setError("Waste type is required");
            return;
        }

        // Display a success message (You can replace this with real processing logic)
        Toast.makeText(this, "Pickup Request Submitted", Toast.LENGTH_SHORT).show();

        // Optionally, start a new activity after submission
        Intent intent = new Intent(pickuprequest.this, congratulation.class);  // Example: ConfirmationActivity
        startActivity(intent);
    }
}
