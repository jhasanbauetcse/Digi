package com.abunayem.digibin.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.abunayem.digibin.R;
import com.abunayem.digibin.pickuprequest;  // Make sure this imports the correct class for pickuprequest

public class HomeFragment extends Fragment {

    private Button pickupRequestButton;

    // onCreateView method signature should match the Fragment lifecycle method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);  // Use the correct layout file here

        // Initialize the Pickup Request button
        pickupRequestButton = rootView.findViewById(R.id.button34); // This is the ID of the "Pickups Request" button

        // Set a click listener for the Pickup Request button
        pickupRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the PickupRequest activity
                Intent intent = new Intent(getActivity(), pickuprequest.class);  // Redirect to pickuprequest.java
                startActivity(intent);
            }
        });

        return rootView; // Return the root view of the fragment
    }
}
