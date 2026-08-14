package com.example.petcare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Calendar extends AppCompatActivity {

    // =========================================================
    // VIEWS
    // =========================================================

    CalendarView calendarView;

    Button btnAddReminder;

    TextView btnBack;

    LinearLayout listReminders;


    // =========================================================
    // USERNAME
    // =========================================================

    private String username;


    // =========================================================
    // FIREBASE
    // =========================================================

    DatabaseReference databaseReference;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.calendar);


        // =====================================================
        // GET USERNAME
        // =====================================================

        username =
                getIntent().getStringExtra("username");


        if (username == null || username.isEmpty()) {

            Toast.makeText(
                    this,
                    "Username not received",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        // =====================================================
        // FIND VIEWS
        // =====================================================

        btnBack =
                findViewById(R.id.btnBack);

        calendarView =
                findViewById(R.id.calendarView);

        listReminders =
                findViewById(R.id.listReminders);

        btnAddReminder =
                findViewById(R.id.btnAddReminder);


        // =====================================================
        // FIREBASE DATABASE REFERENCE
        // =====================================================

        databaseReference =
                FirebaseDatabase
                        .getInstance()
                        .getReference("Users")
                        .child(username)
                        .child("reminders");


        // =====================================================
        // BACK BUTTON
        // =====================================================

        btnBack.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Calendar.this,
                            Home.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);

            finish();
        });


        // =====================================================
        // CALENDAR DATE CHANGE
        // =====================================================

        calendarView.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {

                    String selectedDate =
                            dayOfMonth + "/" +
                                    (month + 1) + "/" +
                                    year;

                    loadReminders(selectedDate);
                }
        );


        // =====================================================
        // LOAD TODAY'S REMINDERS
        // =====================================================

        java.util.Calendar today =
                java.util.Calendar.getInstance();


        int day =
                today.get(
                        java.util.Calendar.DAY_OF_MONTH
                );


        int month =
                today.get(
                        java.util.Calendar.MONTH
                ) + 1;


        int year =
                today.get(
                        java.util.Calendar.YEAR
                );


        String todayDate =
                day + "/" +
                        month + "/" +
                        year;


        loadReminders(todayDate);


        // =====================================================
        // ADD REMINDER BUTTON
        // =====================================================

        btnAddReminder.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Calendar.this,
                            AddReminderActivity.class
                    );


            intent.putExtra(
                    "username",
                    username
            );


            startActivity(intent);
        });
    }


    // =========================================================
    // LOAD REMINDERS
    // =========================================================

    private void loadReminders(
            String selectedDate) {


        // =====================================================
        // CLEAR OLD REMINDERS
        // =====================================================

        listReminders.removeAllViews();


        // =====================================================
        // READ FIREBASE
        // =====================================================

        databaseReference
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    DataSnapshot snapshot) {


                                boolean foundReminder =
                                        false;


                                // =================================================
                                // LOOP THROUGH REMINDERS
                                // =================================================

                                for (DataSnapshot data :
                                        snapshot.getChildren()) {


                                    Reminder reminder =
                                            data.getValue(
                                                    Reminder.class
                                            );


                                    if (reminder == null) {

                                        continue;
                                    }


                                    // =================================================
                                    // CHECK DATE
                                    // =================================================

                                    if (reminder.getDate() != null &&
                                            reminder.getDate()
                                                    .equals(selectedDate)) {


                                        foundReminder =
                                                true;


                                        // =============================================
                                        // GET TITLE
                                        // =============================================

                                        String title =
                                                reminder.getTitle();


                                        if (title == null ||
                                                title.isEmpty()) {

                                            title =
                                                    "Reminder";
                                        }


                                        // =============================================
                                        // GET TIME
                                        // =============================================

                                        String time =
                                                reminder.getTime();


                                        // =============================================
                                        // CREATE DISPLAY TEXT
                                        // =============================================

                                        String reminderText;


                                        if (time == null ||
                                                time.isEmpty()) {

                                            reminderText =
                                                    title;

                                        } else {

                                            reminderText =
                                                    title +
                                                            " - " +
                                                            time;
                                        }


                                        // =============================================
                                        // GET EXACT FIREBASE REFERENCE
                                        // =============================================

                                        DatabaseReference reminderReference =
                                                data.getRef();


                                        // =============================================
                                        // ADD ROW
                                        // =============================================

                                        addReminderRow(
                                                reminderText,
                                                reminderReference
                                        );
                                    }
                                }


                                // =====================================================
                                // NO REMINDERS
                                // =====================================================

                                if (!foundReminder) {

                                    showNoReminders();
                                }
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


    // =========================================================
    // ADD REMINDER ROW
    // =========================================================

    private void addReminderRow(
            String reminderText,
            DatabaseReference reminderReference) {


        // =====================================================
        // INFLATE ITEM REMINDER XML
        // =====================================================

        View reminderView =
                LayoutInflater
                        .from(Calendar.this)
                        .inflate(
                                R.layout.item_reminder,
                                listReminders,
                                false
                        );


        // =====================================================
        // FIND TEXT VIEW
        // =====================================================

        TextView txtReminder =
                reminderView.findViewById(
                        R.id.txtReminder
                );


        // =====================================================
        // FIND DELETE BUTTON
        // =====================================================

        ImageButton btnDelete =
                reminderView.findViewById(
                        R.id.btnDelete
                );


        // =====================================================
        // SET REMINDER TEXT
        // =====================================================

        txtReminder.setText(
                reminderText
        );


        // =====================================================
        // DELETE BUTTON
        // =====================================================

        btnDelete.setOnClickListener(v -> {

            deleteReminder(
                    reminderReference
            );
        });


        // =====================================================
        // ADD ROW TO SCROLLABLE LIST
        // =====================================================

        listReminders.addView(
                reminderView
        );
    }


    // =========================================================
    // SHOW NO REMINDERS
    // =========================================================

    private void showNoReminders() {


        TextView noReminder =
                new TextView(
                        Calendar.this
                );


        noReminder.setText(
                "No reminders"
        );


        noReminder.setTextSize(
                14
        );


        noReminder.setTextColor(
                Color.DKGRAY
        );


        noReminder.setPadding(
                10,
                12,
                10,
                12
        );


        listReminders.addView(
                noReminder
        );
    }


    // =========================================================
    // DELETE REMINDER
    // =========================================================

    private void deleteReminder(
            DatabaseReference reminderReference) {


        // =====================================================
        // CHECK REFERENCE
        // =====================================================

        if (reminderReference == null) {

            Toast.makeText(
                    Calendar.this,
                    "Reminder not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =====================================================
        // DELETE FROM FIREBASE
        // =====================================================

        reminderReference
                .removeValue()
                .addOnSuccessListener(unused -> {


                    // =============================================
                    // SUCCESS MESSAGE
                    // =============================================

                    Toast.makeText(
                            Calendar.this,
                            "Reminder deleted",
                            Toast.LENGTH_SHORT
                    ).show();


                    // =============================================
                    // GET CURRENT CALENDAR DATE
                    // =============================================

                    java.util.Calendar selectedCalendar =
                            java.util.Calendar.getInstance();


                    selectedCalendar.setTimeInMillis(
                            calendarView.getDate()
                    );


                    int day =
                            selectedCalendar.get(
                                    java.util.Calendar.DAY_OF_MONTH
                            );


                    int month =
                            selectedCalendar.get(
                                    java.util.Calendar.MONTH
                            ) + 1;


                    int year =
                            selectedCalendar.get(
                                    java.util.Calendar.YEAR
                            );


                    String selectedDate =
                            day + "/" +
                                    month + "/" +
                                    year;


                    // =============================================
                    // RELOAD REMINDERS
                    // =============================================

                    loadReminders(
                            selectedDate
                    );
                })


                .addOnFailureListener(e -> {


                    // =============================================
                    // DELETE FAILED
                    // =============================================

                    Toast.makeText(
                            Calendar.this,
                            "Failed to delete reminder",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}