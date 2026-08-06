package com.example.petcare;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    private TextView txtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        txtUsername = findViewById(R.id.txtUsername);

        // Get username from Login page
        String username = getIntent().getStringExtra("username");

        if (username != null && !username.isEmpty()) {
            txtUsername.setText(username + " 👋");
        } else {
            txtUsername.setText("Guest 👋");
        }

    }
}