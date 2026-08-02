package com.example.petcare;   // Change if your package name is different

import android.content.Intent;
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

public class Createacc extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword, etConfirmPassword;
    private Button btnCreateAccount;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createacc);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        db = FirebaseFirestore.getInstance();

        btnCreateAccount.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            etUsername.setError("Enter Username");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter Password");
            etPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirm Password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username already exists
        db.collection("users")
                .document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        Toast.makeText(Createacc.this,
                                "Username already exists",
                                Toast.LENGTH_SHORT).show();

                    } else {

                        Map<String, Object> user = new HashMap<>();
                        user.put("username", username);
                        user.put("password", password);

                        db.collection("users")
                                .document(username)
                                .set(user)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(Createacc.this,
                                            "Account Created Successfully",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Createacc.this, Landingpage.class);
                                    startActivity(intent);
                                    finish();

                                })
                                .addOnFailureListener(e ->

                                        Toast.makeText(Createacc.this,
                                                "Failed: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show());

                    }

                })
                .addOnFailureListener(e ->

                        Toast.makeText(Createacc.this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}