package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Calendar extends AppCompatActivity {

    CalendarView calendarView;
    Button btnAddReminder;
    TextView btnBack;

    private String username;

    ListView listReminders;

    ArrayList<String> reminderList;
    ArrayAdapter<String> adapter;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calendar);

        // Get username
        username = getIntent().getStringExtra("username");

        if (username == null || username.isEmpty()) {

            Toast.makeText(
                    this,
                    "Username not received",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // Find views
        btnBack = findViewById(R.id.btnBack);
        calendarView = findViewById(R.id.calendarView);
        listReminders = findViewById(R.id.listReminders);
        btnAddReminder = findViewById(R.id.btnAddReminder);


        // ================= BACK BUTTON =================

        btnBack.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Calendar.this, Home.class);

            intent.putExtra("username", username);

            startActivity(intent);

            finish();
        });


        // ================= REMINDER LIST =================

        reminderList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                R.layout.item_reminder,
                R.id.txtReminder,
                reminderList
        );

        listReminders.setAdapter(adapter);


        // ================= FIREBASE =================

        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(username)
                .child("reminders");


        // ================= CALENDAR =================

        calendarView.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {

                    String date =
                            dayOfMonth + "/" +
                                    (month + 1) + "/" +
                                    year;

                    loadReminders(date);
                }
        );


        // ================= ADD REMINDER =================

        btnAddReminder.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Calendar.this,
                    AddReminderActivity.class
            );

            intent.putExtra("username", username);

            startActivity(intent);
        });
    }


    // ================= LOAD REMINDERS =================

    private void loadReminders(String selectedDate) {

        reminderList.clear();

        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            DataSnapshot snapshot) {

                        for (DataSnapshot data :
                                snapshot.getChildren()) {

                            Reminder reminder =
                                    data.getValue(Reminder.class);

                            if (reminder != null &&
                                    reminder.getDate() != null &&
                                    reminder.getDate()
                                            .equals(selectedDate)) {

                                reminderList.add(
                                        reminder.getTitle()
                                                + " - "
                                                + reminder.getTime()
                                );
                            }
                        }


                        // No reminders
                        if (reminderList.isEmpty()) {

                            reminderList.add(
                                    "No reminders"
                            );
                        }


                        adapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(
                            DatabaseError error) {

                        Toast.makeText(
                                Calendar.this,
                                "Failed to load reminders",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
}