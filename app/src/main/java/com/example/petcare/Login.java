package com.example.petcare; // Change if your package name is different

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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

        db.collection("users")
                .document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(Login.this,
                                "User not found",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String storedPassword = documentSnapshot.getString("password");

                    if (storedPassword != null && storedPassword.equals(password)) {

                        Toast.makeText(Login.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Login.this, Home.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(Login.this,
                                "Incorrect Password",
                                Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e ->

                        Toast.makeText(Login.this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}