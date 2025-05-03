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

import com.abunayem.digibin.loginActivity; // Import your LoginActivity
import com.abunayem.digibin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView profileName, profileEmail;
    private Button buttonHome, buttonSupport, buttonDeveloper, buttonShare, buttonLogout;
    private FirebaseAuth mAuth;
    private DatabaseReference customerRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        customerRef = database.getReference("customer");

        // Initialize the TextViews
        profileName = view.findViewById(R.id.profilrName1);
        profileEmail = view.findViewById(R.id.profilrEmail2);

        // Initialize the buttons
        buttonHome = view.findViewById(R.id.button78);
        buttonSupport = view.findViewById(R.id.button796);
        buttonDeveloper = view.findViewById(R.id.button4);
        buttonShare = view.findViewById(R.id.button5);
        buttonLogout = view.findViewById(R.id.button8);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is logged in, retrieve profile data from Firebase
            String userId = currentUser.getUid();
            loadUserProfile(userId);
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Handle button actions (Optional)
        buttonLogout.setOnClickListener(v -> {
            // Log the user out
            mAuth.signOut();

            // Redirect to login activity
            Intent intent = new Intent(getActivity(), loginActivity.class);
            startActivity(intent);
            getActivity().finish(); // Close the current activity to prevent the user from returning
        });

        return view;
    }

    private void loadUserProfile(String userId) {
        customerRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Retrieve name and email from Firebase
                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);

                if (name != null && email != null) {
                    profileName.setText(name);
                    profileEmail.setText(email);
                } else {
                    Toast.makeText(getActivity(), "Profile data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
