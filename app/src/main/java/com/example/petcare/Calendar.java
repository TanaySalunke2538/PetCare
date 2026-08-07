package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Calendar extends AppCompatActivity {

    CalendarView calendarView;
    Button btnAddReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        calendarView = findViewById(R.id.calendarView);
        btnAddReminder = findViewById(R.id.btnAddReminder);

        calendarView.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {

                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;

                    Toast.makeText(
                            Calendar.this,
                            "Selected: " + date,
                            Toast.LENGTH_SHORT
                    ).show();
                });

        btnAddReminder.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Calendar.this,
                            AddReminderActivity.class);

            startActivity(intent);

        });

    }
}