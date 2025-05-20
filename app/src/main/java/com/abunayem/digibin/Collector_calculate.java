package com.abunayem.digibin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Collector_calculate extends AppCompatActivity {

    private EditText paperWeight, paperPrice;
    private EditText plasticWeight, plasticPrice;
    private EditText metalsWeight, metalsPrice;
    private EditText motorWeight, motorPrice;
    private EditText eWasteWeight, eWastePrice;
    private EditText otherWasteWeight, otherWastePrice;
    private EditText organicWeight, organicPrice;

    private Button proceedButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collector_calculate);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize all EditText fields
        initializeViews();

        proceedButton = findViewById(R.id.button_collect);
        proceedButton.setOnClickListener(v -> submitWasteData());
    }

    private void initializeViews() {
        // Paper
        paperWeight = findViewById(R.id.editTextText852);
        paperPrice = findViewById(R.id.editTextText287);

        // Plastic
        plasticWeight = findViewById(R.id.editText85Text852);
        plasticPrice = findViewById(R.id.editTextText2tg87);

        // Metals
        metalsWeight = findViewById(R.id.editTex85t85Text852);
        metalsPrice = findViewById(R.id.edit85TextText2tg87);

        // Motor
        motorWeight = findViewById(R.id.e7Text852);
        motorPrice = findViewById(R.id.e85xtText2tg87);

        // E-Waste
        eWasteWeight = findViewById(R.id.e7Tex85t852);
        eWastePrice = findViewById(R.id.e8585xtText2tg87);

        // Other Waste
        otherWasteWeight = findViewById(R.id.e7Te7t852);
        otherWastePrice = findViewById(R.id.e8xtText2tg87);

        // Organic
        organicWeight = findViewById(R.id.e8747t852);
        organicPrice = findViewById(R.id.e8x8g87);
    }

    private void submitWasteData() {
        // Get request ID and customer email from intent
        String requestId = getIntent().getStringExtra("REQUEST_ID");
        String customerEmail = getIntent().getStringExtra("CUSTOMER_EMAIL");
        String collectorId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (requestId == null || requestId.isEmpty()) {
            Toast.makeText(this, "Request ID is missing.", Toast.LENGTH_LONG).show();
            return;
        }
        if (collectorId == null) {
            Toast.makeText(this, "Collector ID is missing. Please ensure you are logged in.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a map to store all waste data
        Map<String, Object> wasteData = new HashMap<>();

        // Add each waste type with weight and calculated value
        addWasteTypeToMap(wasteData, "paper", paperWeight, paperPrice, 15);
        addWasteTypeToMap(wasteData, "plastic", plasticWeight, plasticPrice, 12);
        addWasteTypeToMap(wasteData, "metals", metalsWeight, metalsPrice, 80);
        addWasteTypeToMap(wasteData, "motor", motorWeight, motorPrice, 90);
        addWasteTypeToMap(wasteData, "e_waste", eWasteWeight, eWastePrice, 100);
        addWasteTypeToMap(wasteData, "other_waste", otherWasteWeight, otherWastePrice, 0);
        addWasteTypeToMap(wasteData, "organic", organicWeight, organicPrice, 60);

        // Calculate total weight and value
        double totalWeight = calculateTotal(wasteData, "weight");
        double totalValue = calculateTotal(wasteData, "value");

        // Calculate points (5 points per 1 value unit)
        int pointsEarned = (int) (totalValue * 1);

        Map<String, Object> collectionRecord = new HashMap<>();
        collectionRecord.put("request_id", requestId);
        collectionRecord.put("collector_id", collectorId);
        collectionRecord.put("collected_items", wasteData);
        collectionRecord.put("total_weight", totalWeight);
        collectionRecord.put("total_value", totalValue);
        collectionRecord.put("collection_timestamp", FieldValue.serverTimestamp());
        collectionRecord.put("status", "Collected");
        collectionRecord.put("points_earned", pointsEarned);

        // Save to Firestore in 'wasteCollections'
        firestore.collection("wasteCollections")
                .add(collectionRecord)
                .addOnSuccessListener(documentReference -> {
                    // Update customer's points
                    updateCustomerPoints(customerEmail, pointsEarned);

                    Toast.makeText(Collector_calculate.this, "Waste collection recorded successfully", Toast.LENGTH_SHORT).show();
                    updateAllRequestStatuses(requestId, customerEmail, collectorId, "Collected");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Collector_calculate.this, "Error recording waste collection: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updateCustomerPoints(String customerEmail, int pointsEarned) {
        if (customerEmail == null || customerEmail.isEmpty()) {
            return;
        }

        firestore.collection("customers")
                .whereEqualTo("email", customerEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String customerId = task.getResult().getDocuments().get(0).getId();

                        // Update points (increment by pointsEarned)
                        firestore.collection("customers")
                                .document(customerId)
                                .update("points", FieldValue.increment(pointsEarned))
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("PointsUpdate", "Points updated successfully");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("PointsUpdate", "Error updating points", e);
                                });
                    }
                });
    }

    private void addWasteTypeToMap(Map<String, Object> map, String type,
                                   EditText weightEdit, EditText priceEdit,
                                   double defaultPriceIfNotSetInUI) {
        try {
            String weightStr = weightEdit.getText().toString();
            String priceStr = priceEdit.getText().toString();

            double weight = weightStr.isEmpty() ? 0 : Double.parseDouble(weightStr);
            double pricePerKg = priceStr.isEmpty() ? defaultPriceIfNotSetInUI : Double.parseDouble(priceStr);

            if (weight > 0) {
                double value = weight * pricePerKg;

                Map<String, Object> wasteTypeDetails = new HashMap<>();
                wasteTypeDetails.put("weight", weight);
                wasteTypeDetails.put("price_per_kg", pricePerKg);
                wasteTypeDetails.put("value", value);

                map.put(type, wasteTypeDetails);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format for " + type, Toast.LENGTH_SHORT).show();
        }
    }

    private double calculateTotal(Map<String, Object> wasteData, String fieldToSum) {
        double total = 0;
        for (Map.Entry<String, Object> entry : wasteData.entrySet()) {
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> typeData = (Map<String, Object>) entry.getValue();
                if (typeData.containsKey(fieldToSum) && typeData.get(fieldToSum) instanceof Number) {
                    total += ((Number) typeData.get(fieldToSum)).doubleValue();
                }
            }
        }
        return total;
    }

    private void updateAllRequestStatuses(String requestId, String customerEmail, String collectorId, String newStatus) {
        // 1. Update the main pickupRequests collection
        firestore.collection("pickupRequests")
                .document(requestId)
                .update("status", newStatus, "collectorId", collectorId, "last_updated", FieldValue.serverTimestamp())
                .addOnSuccessListener(aVoid -> Log.d("StatusUpdate", "Main request status updated"))
                .addOnFailureListener(e -> Log.e("StatusUpdate", "Failed to update main request status", e));

        // 2. Update the customer's pickupRequests subcollection
        if (customerEmail != null && !customerEmail.isEmpty()) {
            firestore.collection("customers").whereEqualTo("email", customerEmail).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            String customerId = task.getResult().getDocuments().get(0).getId();
                            firestore.collection("customers")
                                    .document(customerId)
                                    .collection("pickupRequests")
                                    .document(requestId)
                                    .update("status", newStatus, "last_updated", FieldValue.serverTimestamp())
                                    .addOnSuccessListener(aVoid -> Log.d("StatusUpdate", "Customer request status updated"))
                                    .addOnFailureListener(e -> Log.e("StatusUpdate", "Failed to update customer request status", e));
                        }
                    });
        }

        // 3. Update the collector's assignedRequests subcollection
        if (collectorId != null) {
            firestore.collection("collector")
                    .document(collectorId)
                    .collection("assignedRequests")
                    .document(requestId)
                    .update("status", newStatus, "last_updated", FieldValue.serverTimestamp())
                    .addOnSuccessListener(aVoid -> Log.d("StatusUpdate", "Collector assigned request status updated"))
                    .addOnFailureListener(e -> Log.e("StatusUpdate", "Failed to update collector assigned request", e));
        }
    }
}