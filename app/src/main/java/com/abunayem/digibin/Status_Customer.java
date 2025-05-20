package com.abunayem.digibin;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class Status_Customer extends AppCompatActivity {

    private LinearLayout linearLayoutContainer;
    private int boxCount = 1;
    private ArrayList<String> requestIds;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        requestIds = new ArrayList<>();
        linearLayoutContainer = findViewById(R.id.linearLayoutContainer);

        fetchPickupRequests();
    }

    private void fetchPickupRequests() {
        String userId = mAuth.getCurrentUser().getUid();

        firestore.collection("customers")
                .document(userId)
                .collection("pickupRequests")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String requestId = document.getId();
                        String status = document.getString("status");
                        addPickupRequestBox(requestId, status);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    private void addPickupRequestBox(String requestId, String status) {
        RelativeLayout requestBox = new RelativeLayout(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(140)); // increased height
        params.setMargins(dpToPx(16), 0, dpToPx(16), dpToPx(16));
        requestBox.setLayoutParams(params);
        requestBox.setBackgroundResource(R.drawable.box_border_green);
        requestBox.setPadding(16, 16, 16, 16);

        // Request ID Text
        TextView requestText = new TextView(this);
        requestText.setText(boxCount + ". RequestID: " +"\n" + requestId);
        requestText.setTextSize(18f);
        requestText.setTextColor(getResources().getColor(android.R.color.black));
        requestBox.addView(requestText);

        // Status Text
        TextView statusText = new TextView(this);
        statusText.setText(status != null ? status : "Pending");
        statusText.setTextSize(16f);

        RelativeLayout.LayoutParams statusParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        statusParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        statusText.setLayoutParams(statusParams);

        if ("Pending".equals(status)) {
            statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if ("approved".equals(status)) {
            statusText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else if ("assigned".equals(status)) {
            statusText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        } else if ("Collected".equals(status)) {
            statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            statusText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        requestBox.addView(statusText);

        // If collected, fetch value/weight from wasteCollections
        if ("Collected".equals(status)) {
            firestore.collection("wasteCollections")
                    .whereEqualTo("request_id", requestId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            Long totalValue = document.getLong("total_value");
                            Long totalWeight = document.getLong("total_weight");

                            TextView valueText = new TextView(this);
                            valueText.setText("\n" + "Total Value: " + (totalValue != null ? totalValue : 0) + " BDT");
                            valueText.setTextSize(16f);
                            valueText.setTextColor(getResources().getColor(android.R.color.black));

                            TextView weightText = new TextView(this);
                            weightText.setText("\n"+"Total Weight: " + (totalWeight != null ? totalWeight : 0) + " kg");
                            weightText.setTextSize(16f);
                            weightText.setTextColor(getResources().getColor(android.R.color.black));

                            RelativeLayout.LayoutParams valueParams = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            valueParams.topMargin = dpToPx(40);
                            valueText.setLayoutParams(valueParams);

                            RelativeLayout.LayoutParams weightParams = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            weightParams.topMargin = dpToPx(65);
                            weightText.setLayoutParams(weightParams);

                            requestBox.addView(valueText);
                            requestBox.addView(weightText);
                        }
                    });
        }

        boxCount++;
        linearLayoutContainer.addView(requestBox);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}
