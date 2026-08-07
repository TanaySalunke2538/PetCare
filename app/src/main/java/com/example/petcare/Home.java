package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Home extends AppCompatActivity {

    private CardView cardProfile, cardCalendar, cardReminder;
    private ImageButton btnHelpline;
    private Button btnHelp;
    private TextView txtWelcome;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        txtWelcome = findViewById(R.id.txtWelcome);

        username = getIntent().getStringExtra("username");

        if (username == null)
            username = "";

        txtWelcome.setText("Welcome, " + username + "!");

        cardProfile = findViewById(R.id.cardProfile);
        cardCalendar = findViewById(R.id.cardCalendar);
        cardReminder = findViewById(R.id.cardReminder);
        btnHelpline = findViewById(R.id.btnHelpline);
        btnHelp = findViewById(R.id.btnHelp);

        cardProfile.setOnClickListener(v -> {

            Intent intent = new Intent(Home.this, Profilepage.class);
            intent.putExtra("username", username);
            startActivity(intent);

        });

        cardCalendar.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, Calendar.class));
        });

        cardReminder.setOnClickListener(v ->
                startActivity(new Intent(Home.this, Memories.class)));

        btnHelpline.setOnClickListener(v ->
                startActivity(new Intent(Home.this, Helpline.class)));

        btnHelp.setOnClickListener(v ->
                startActivity(new Intent(Home.this, Helpline.class)));
    }
}