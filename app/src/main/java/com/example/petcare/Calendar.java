package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
public class Calendar extends AppCompatActivity {

    CalendarView calendarView;
    Button btnAddReminder;
    private String username;
    ListView listReminders;

    ArrayList<String> reminderList;

    ArrayAdapter<String> adapter;

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Username not received", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        calendarView = findViewById(R.id.calendarView);
        listReminders = findViewById(R.id.listReminders);

        reminderList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                reminderList
        );

        listReminders.setAdapter(adapter);

        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(username)
                .child("reminders");
        btnAddReminder = findViewById(R.id.btnAddReminder);

        calendarView.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {

                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;

                    loadReminders(date);
                });


        btnAddReminder.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Calendar.this,
                    AddReminderActivity.class
            );

            intent.putExtra("username", username);

            startActivity(intent);

        });

    }
    private void loadReminders(String selectedDate) {

        reminderList.clear();

        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot data : snapshot.getChildren()) {

                            Reminder reminder =
                                    data.getValue(Reminder.class);

                            if (reminder != null &&
                                    reminder.getDate().equals(selectedDate)) {

                                reminderList.add(
                                        reminder.getTitle()
                                                + " - "
                                                + reminder.getTime()
                                );

                            }

                        }

                        adapter.notifyDataSetChanged();

                        if(reminderList.isEmpty()){

                            reminderList.add("No reminders");

                            adapter.notifyDataSetChanged();

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }

                });

    }

}