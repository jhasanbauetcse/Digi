package com.abunayem.digibin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Admin_coll_assign extends AppCompatActivity {

    private LinearLayout linearLayoutContainer;
    private int boxCount = 1;
    private FirebaseFirestore firestore;
    private Bundle requestData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collectors);

        firestore = FirebaseFirestore.getInstance();
        linearLayoutContainer = findViewById(R.id.linearLayoutContainer);

        // Get the request data passed from Status_Admin
        requestData = getIntent().getExtras();

        // Fetch collector data
        fetchCollectors();
    }

    private void fetchCollectors() {
        firestore.collection("collector")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String collectorId = document.getId();
                        String name = document.getString("name");
                        String email = document.getString("email");

                        addCollectorBox(collectorId, name, email);
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void addCollectorBox(String collectorId, String name, String email) {
        RelativeLayout collectorBox = new RelativeLayout(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(180));
        params.setMargins(dpToPx(16), 0, dpToPx(16), dpToPx(16));
        collectorBox.setLayoutParams(params);
        collectorBox.setBackgroundResource(R.drawable.box_border_green);
        collectorBox.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

        // Info Text
        String collectorInfo = boxCount + ". Collector ID: " + collectorId
                + "\nName: " + name
                + "\nEmail: " + email;

        TextView collectorText = new TextView(this);
        collectorText.setText(collectorInfo);
        collectorText.setTextSize(16f);
        collectorText.setTextColor(getResources().getColor(android.R.color.black));
        collectorText.setId(View.generateViewId());

        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        collectorText.setLayoutParams(textParams);

        collectorBox.addView(collectorText);

        // Assign Button
        Button assignButton = new Button(this);
        assignButton.setText("Assign");
        assignButton.setTextSize(14f);
        assignButton.setBackgroundResource(R.drawable.box_green);
        assignButton.setTextColor(getResources().getColor(android.R.color.white));
        assignButton.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));

        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonParams.setMargins(0, dpToPx(8), 0, 0);
        assignButton.setLayoutParams(buttonParams);

        assignButton.setOnClickListener(v -> {
            if (requestData != null) {
                assignRequestToCollector(collectorId, requestData);
            } else {
                Toast.makeText(this, "No request data found", Toast.LENGTH_SHORT).show();
            }
        });

        collectorBox.addView(assignButton);
        linearLayoutContainer.addView(collectorBox);
        boxCount++;
    }

    private void assignRequestToCollector(String collectorId, Bundle requestData) {
        // Create a map with the request data
        Map<String, Object> assignmentData = new HashMap<>();
        assignmentData.put("requestId", requestData.getString("REQUEST_ID"));
        assignmentData.put("phone", requestData.getString("PHONE"));
        assignmentData.put("address", requestData.getString("ADDRESS"));
        assignmentData.put("schedule", requestData.getString("SCHEDULE"));
        assignmentData.put("wasteType", requestData.getString("WASTE_TYPE"));
        assignmentData.put("customerEmail", requestData.getString("EMAIL"));
        assignmentData.put("status", "assigned");
        assignmentData.put("timestamp", FieldValue.serverTimestamp());

        // Add the assignment to the collector's document
        firestore.collection("collector").document(collectorId)
                .collection("assignedRequests")
                .document(requestData.getString("REQUEST_ID"))
                .set(assignmentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Request assigned to collector successfully", Toast.LENGTH_SHORT).show();

                    // Update the status in the global pickupRequests collection
                    firestore.collection("pickupRequests")
                            .document(requestData.getString("REQUEST_ID"))
                            .update("status", "assigned", "assignedTo", collectorId)
                            .addOnSuccessListener(aVoid1 -> {


                                // Update the status in the customer's subcollection
                                updateCustomerRequestStatus(requestData.getString("EMAIL"), requestData.getString("REQUEST_ID"), "assigned");
                                finish(); // Close the activity after assignment
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to assign request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCustomerRequestStatus(String customerEmail, String requestId, String status) {
        firestore.collection("customers")
                .whereEqualTo("email", customerEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String customerId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        firestore.collection("customers")
                                .document(customerId)
                                .collection("pickupRequests")
                                .document(requestId)
                                .update("status", status)
                                .addOnSuccessListener(aVoid -> {

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to update customer request status", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to find customer", Toast.LENGTH_SHORT).show();
                });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}