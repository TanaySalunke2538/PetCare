package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private DatabaseReference reminderRef;

    private CardView cardProfile;
    private CardView cardCalendar;
    private CardView cardReminder;

    private Button btnHelp;
    private TextView txtWelcome;

    private ListView homeReminderList;

    private ArrayList<String> reminderList;
    private ArrayAdapter<String> reminderAdapter;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        // Get username
        username = getIntent().getStringExtra("username");

        if (username == null) {
            username = "";
        }

        // Welcome text
        txtWelcome = findViewById(R.id.txtWelcome);
        txtWelcome.setText("Welcome, " + username + "!");

        // AI Bot button
        Button btnbot = findViewById(R.id.btnbot);

        btnbot.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Home.this, ChatbotActivity.class);

            intent.putExtra("username", username);

            startActivity(intent);
        });

        // Cards
        cardProfile = findViewById(R.id.cardProfile);
        cardCalendar = findViewById(R.id.cardCalendar);
        cardReminder = findViewById(R.id.cardReminder);

        // Firebase reference
        reminderRef = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(username)
                .child("reminders");

        // Home reminder ListView
        homeReminderList =
                findViewById(R.id.homeReminderList);

        reminderList = new ArrayList<>();

        reminderAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_reminder,
                R.id.txtReminder,
                reminderList
        );

        homeReminderList.setAdapter(reminderAdapter);

        // Load reminders
        loadUpcomingReminders();

        // Profile
        cardProfile.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Home.this, Profilepage.class);

            intent.putExtra("username", username);

            startActivity(intent);
        });

        // Calendar
        cardCalendar.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Home.this, Calendar.class);

            intent.putExtra("username", username);

            startActivity(intent);
        });

        // Memories
        cardReminder.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Home.this, Memories.class);

            intent.putExtra("username", username);

            startActivity(intent);
        });
    }

    private void loadUpcomingReminders() {

        reminderRef.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            DataSnapshot snapshot) {

                        reminderList.clear();

                        if (!snapshot.exists()) {

                            reminderList.add(
                                    "No upcoming reminders"
                            );

                            reminderAdapter.notifyDataSetChanged();

                            return;
                        }

                        for (DataSnapshot data :
                                snapshot.getChildren()) {

                            Reminder reminder =
                                    data.getValue(Reminder.class);

                            if (reminder != null) {

                                String reminderText =
                                        reminder.getTitle()
                                                + " - "
                                                + reminder.getDate()
                                                + "  "
                                                + reminder.getTime();

                                reminderList.add(
                                        reminderText
                                );
                            }
                        }

                        if (reminderList.isEmpty()) {

                            reminderList.add(
                                    "No upcoming reminders"
                            );
                        }

                        reminderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(
                            DatabaseError error) {

                        reminderList.clear();

                        reminderList.add(
                                "Unable to load reminders"
                        );

                        reminderAdapter.notifyDataSetChanged();
                    }
                }
        );
    }
}