package com.example.petcare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Profilepage extends AppCompatActivity {

    private TextInputEditText etPetName, etDOB, etOwnerName, etPhone, etEmail;
    private Button btnSaveProfile;

    private FirebaseFirestore db;

    // Username received from Login/Home
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilepage);

        etPetName = findViewById(R.id.etPetName);
        etDOB = findViewById(R.id.etDOB);
        etOwnerName = findViewById(R.id.etOwnerName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);

        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        db = FirebaseFirestore.getInstance();

        // Receive username from Home page
        username = getIntent().getStringExtra("username");

        if (username == null)
            username = "Guest";

        loadProfile();

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {

        String petName = etPetName.getText().toString().trim();
        String dob = etDOB.getText().toString().trim();
        String owner = etOwnerName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (petName.isEmpty() ||
                dob.isEmpty() ||
                owner.isEmpty() ||
                phone.isEmpty() ||
                email.isEmpty()) {

            Toast.makeText(this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> profile = new HashMap<>();

        profile.put("petName", petName);
        profile.put("dob", dob);
        profile.put("ownerName", owner);
        profile.put("phone", phone);
        profile.put("email", email);

        db.collection("users")
                .document(username)
                .collection("profile")
                .document("details")
                .set(profile)
                .addOnSuccessListener(unused ->

                        Toast.makeText(Profilepage.this,
                                "Profile Saved Successfully",
                                Toast.LENGTH_SHORT).show())

                .addOnFailureListener(e ->

                        Toast.makeText(Profilepage.this,
                                e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    private void loadProfile() {

        db.collection("users")
                .document(username)
                .collection("profile")
                .document("details")
                .get()

                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        etPetName.setText(documentSnapshot.getString("petName"));
                        etDOB.setText(documentSnapshot.getString("dob"));
                        etOwnerName.setText(documentSnapshot.getString("ownerName"));
                        etPhone.setText(documentSnapshot.getString("phone"));
                        etEmail.setText(documentSnapshot.getString("email"));

                    }

                });

    }
}