package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(Start.this, Landingpage.class));
            finish();
        }, 1500);
    }
}