package com.abunayem.digibin.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.abunayem.digibin.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class pricelistFragment extends Fragment {

    private FirebaseFirestore db;
    private ListenerRegistration priceListener;

    // TextViews for all price displays
    private TextView priceTextBook, priceNewspaper, priceCartonBox, priceSoftPlastics,
            priceHardPlastics, priceFiber, priceCPU, priceMonitor,
            priceTablet, priceSteel, priceTin, priceAluminium;

    public pricelistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pricelist, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize all TextView fields
        priceTextBook = view.findViewById(R.id.textView271);
        priceNewspaper = view.findViewById(R.id.textView90); // Note: Duplicate ID in XML, needs fixing
        priceCartonBox = view.findViewById(R.id.textView2857);
        priceSoftPlastics = view.findViewById(R.id.t85extV85iew27);
        priceHardPlastics = view.findViewById(R.id.t85ex85tV85iew27);
        priceFiber = view.findViewById(R.id.t85ex85tV85i85ew27);
        priceCPU = view.findViewById(R.id.t8585extV85iew27);
        priceMonitor = view.findViewById(R.id.t85ex858tV85iew27);
        priceTablet = view.findViewById(R.id.t85ex858tV8855iew27);
        priceSteel = view.findViewById(R.id.t85ex858tV885855iew27);
        priceTin = view.findViewById(R.id.t85ex858tV88585855iew27);
        priceAluminium = view.findViewById(R.id.t85ex9885855iew27);

        // Example references for click listeners
        ImageView imageViewPaper = view.findViewById(R.id.imageView35);
        ImageView imageViewPlastic = view.findViewById(R.id.imageView29);

        // Example click listeners (keep your existing functionality)
        imageViewPaper.setOnClickListener(v -> {
            priceTextBook.setText("Text Book\n৳15/Kg (Clicked)");
        });

        imageViewPlastic.setOnClickListener(v -> {
            priceSoftPlastics.setText("Soft Plastics\n৳8/Kg (Clicked)");
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set up Firestore listener when fragment becomes visible
        setupFirestoreListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove Firestore listener when fragment is no longer visible
        if (priceListener != null) {
            priceListener.remove();
        }
    }

    private void setupFirestoreListener() {
        priceListener = db.collection("Pricelist").document("current_prices")
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        // Handle errors
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        updatePricesFromFirestore(documentSnapshot);
                    }
                });
    }

    private void updatePricesFromFirestore(@NonNull DocumentSnapshot document) {
        // Update all price TextViews with data from Firestore
        updatePriceView(priceTextBook, "textbook", "Text Book\n৳", document);
        updatePriceView(priceNewspaper, "newspaper", "News Paper\n৳", document);
        updatePriceView(priceCartonBox, "carton_box", "Carton Box\n৳", document);
        updatePriceView(priceSoftPlastics, "soft_plastics", "Soft Plastics\n৳", document);
        updatePriceView(priceHardPlastics, "hard_plastics", "Hard Plastics\n৳", document);
        updatePriceView(priceFiber, "fiber", "Fiber\n৳", document);
        updatePriceView(priceCPU, "cpu", "CPU\n৳", document);
        updatePriceView(priceMonitor, "monitor", "Monitor\n৳", document);
        updatePriceView(priceTablet, "tablet", "Tablet\n৳", document);
        updatePriceView(priceSteel, "steel", "Steel\n৳", document);
        updatePriceView(priceTin, "tin", "Tin\n৳", document);
        updatePriceView(priceAluminium, "aluminium", "Aluminium\n৳", document);

    }

    private void updatePriceView(TextView textView, String fieldName, String prefix, DocumentSnapshot document) {
        String price = document.getString(fieldName);
        if (price != null && !price.isEmpty()) {
            textView.setText(prefix + price + "/Kg");
        } else {
            // Default value if price is not set
            textView.setText(prefix + "0/Kg");
        }
    }
}