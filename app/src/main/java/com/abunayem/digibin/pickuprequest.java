package com.abunayem.digibin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.textfield.TextInputLayout;

public class pickuprequest extends AppCompatActivity {

    private EditText editTextPhone, editTextAddress;
    private TextInputLayout scheduleInputLayout, wasteInputLayout;
    private AutoCompleteTextView scheduleAutoCompleteTextView, wasteAutoCompleteTextView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickuprequest);  // Set the layout

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize the views
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextText3);
        scheduleInputLayout = findViewById(R.id.locationInputLayout);  // Renamed to scheduleInputLayout
        scheduleAutoCompleteTextView = findViewById(R.id.autoCompleteTextView);  // Renamed to scheduleAutoCompleteTextView
        wasteInputLayout = findViewById(R.id.locationInputLayout2);
        wasteAutoCompleteTextView = findViewById(R.id.autoCompleteTextView2);

        // Create a sample list for the schedule dropdown
        String[] schedules = new String[]{"06:00 AM", "10:00 AM", "02:00 PM", "06:00 PM", "08:00 PM"};

        // Create an ArrayAdapter using the sample schedules
        ArrayAdapter<String> scheduleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, schedules);

        // Set the adapter to the AutoCompleteTextView for schedule
        scheduleAutoCompleteTextView.setAdapter(scheduleAdapter);

        // Create a list of waste types for the dropdown
        String[] wasteTypes = new String[]{"Plastic", "Paper", "Glass", "Metal", "Organic"};

        // Create an ArrayAdapter using the waste types
        ArrayAdapter<String> wasteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, wasteTypes);

        // Set the adapter to the AutoCompleteTextView for waste type
        wasteAutoCompleteTextView.setAdapter(wasteAdapter);

        // Find the "Proceed" button and set its click listener
        findViewById(R.id.button_submit).setOnClickListener(v -> handleProceedButtonClick());
    }

    private void handleProceedButtonClick() {
        // Get the values entered by the user
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String schedule = scheduleAutoCompleteTextView.getText().toString().trim();
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
        if (schedule.isEmpty()) {
            scheduleAutoCompleteTextView.setError("Schedule is required");
            return;
        }
        if (wasteType.isEmpty()) {
            wasteAutoCompleteTextView.setError("Waste type is required");
            return;
        }

        // Get the current user's UID and email
        String userId = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();

        // Create a map to store pickup request data
        PickupRequest pickupRequest = new PickupRequest(phone, address, schedule, wasteType, email);

        // Save pickup request data to Firestore under a unique pickupRequestId for each request
        firestore.collection("customers")
                .document(userId) // User's document
                .collection("pickupRequests") // Subcollection of pickup requests
                .add(pickupRequest)  // Save to user's collection
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the ID of the newly created request document
                        String requestId = task.getResult().getId();

                        // Now save the same request data in a global 'pickupRequests' collection
                        firestore.collection("pickupRequests")
                                .document(requestId) // Use the same requestId for global collection
                                .set(pickupRequest)  // Save to global pickupRequests collection
                                .addOnCompleteListener(globalTask -> {
                                    if (globalTask.isSuccessful()) {
                                        // Pass the requestId to Status_Customer
                                        Intent intent = new Intent(pickuprequest.this, congratulation.class);
                                        intent.putExtra("REQUEST_ID", requestId);  // Send the unique ID to the next activity
                                        startActivity(intent);
                                        finish(); // Close the activity after submission
                                    } else {
                                        Toast.makeText(pickuprequest.this, "Failed to save globally. Try again!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(pickuprequest.this, "Failed to submit request. Try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Define the PickupRequest class with the schedule field instead of location
    public static class PickupRequest {
        private String phone;
        private String address;
        private String schedule;
        private String wasteType;
        private String email;
        private long timestamp;  // Add a timestamp field

        public PickupRequest(String phone, String address, String schedule, String wasteType, String email) {
            this.phone = phone;
            this.address = address;
            this.schedule = schedule;
            this.wasteType = wasteType;
            this.email = email;
            this.timestamp = System.currentTimeMillis();  // Set current time as timestamp
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }

        public String getSchedule() {
            return schedule;
        }

        public String getWasteType() {
            return wasteType;
        }

        public String getEmail() {
            return email;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
