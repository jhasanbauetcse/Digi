package com.abunayem.digibin.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.abunayem.digibin.devloper;
import com.abunayem.digibin.loginActivity;
import com.abunayem.digibin.shareapp;
import com.abunayem.digibin.support;
import com.abunayem.digibin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView profileName, profileEmail;
    private Button buttonHome, buttonSupport, buttonDeveloper, buttonShare, buttonLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize the TextViews
        profileName = view.findViewById(R.id.profilrName1);
        profileEmail = view.findViewById(R.id.profilrEmail2);

        // Initialize the buttons
        buttonHome = view.findViewById(R.id.button78);
        buttonSupport = view.findViewById(R.id.button796);
        buttonDeveloper = view.findViewById(R.id.button4);
        buttonShare = view.findViewById(R.id.button5);
        buttonLogout = view.findViewById(R.id.button8);

        // Load current user profile
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            loadUserProfile(userId);
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Button click listeners
        buttonDeveloper.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), devloper.class);
            startActivity(intent);
        });

        buttonSupport.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), support.class);
            startActivity(intent);
        });

        buttonShare.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), shareapp.class);
            startActivity(intent);
        });

        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), loginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        // You can implement buttonHome if needed here

        return view;
    }

    private void loadUserProfile(String userId) {
        DocumentReference userRef = firestore.collection("customers").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");

                if (name != null && email != null) {
                    profileName.setText(name);
                    profileEmail.setText(email);
                } else {
                    Toast.makeText(getActivity(), "Profile data not found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show());
    }
}
