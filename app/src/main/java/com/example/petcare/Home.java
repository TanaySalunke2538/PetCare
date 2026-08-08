package com.example.petcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

public class Home extends AppCompatActivity {

    // =====================================================
    // FIREBASE
    // =====================================================

    private DatabaseReference reminderRef;


    // =====================================================
    // HOME CARDS
    // =====================================================

    private CardView cardProfile;
    private CardView cardCalendar;
    private CardView cardReminder;


    // =====================================================
    // BUTTONS / TEXT
    // =====================================================

    private Button btnbot;
    private TextView txtWelcome;


    // =====================================================
    // PET PROFILE IMAGE
    // =====================================================

    private ImageView imgPetHome;


    // =====================================================
    // REMINDERS
    // =====================================================

    private ListView homeReminderList;

    private ArrayList<String> reminderList;

    private ArrayAdapter<String> reminderAdapter;


    // =====================================================
    // USERNAME
    // =====================================================

    private String username;


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);


        // =================================================
        // GET USERNAME
        // =================================================

        username =
                getIntent().getStringExtra("username");

        if (username == null ||
                username.isEmpty()) {

            username = "Guest";

        }


        // =================================================
        // WELCOME TEXT
        // =================================================

        txtWelcome =
                findViewById(R.id.txtWelcome);

        txtWelcome.setText(
                "Welcome, " + username + "!"
        );


        // =================================================
        // PET PROFILE IMAGE
        // =================================================

        imgPetHome =
                findViewById(R.id.imgPetHome);

        loadHomeProfilePhoto();


        // =================================================
        // AI BOT BUTTON
        // =================================================

        btnbot =
                findViewById(R.id.btnbot);


        btnbot.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Home.this,
                            ChatbotActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);

        });


        // =================================================
        // CARDS
        // =================================================

        cardProfile =
                findViewById(R.id.cardProfile);

        cardCalendar =
                findViewById(R.id.cardCalendar);

        cardReminder =
                findViewById(R.id.cardReminder);


        // =================================================
        // FIREBASE REMINDER REFERENCE
        // =================================================

        reminderRef =
                FirebaseDatabase
                        .getInstance()
                        .getReference("Users")
                        .child(username)
                        .child("reminders");


        // =================================================
        // HOME REMINDER LIST
        // =================================================

        homeReminderList =
                findViewById(
                        R.id.homeReminderList
                );


        reminderList =
                new ArrayList<>();


        reminderAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.item_reminder,
                        R.id.txtReminder,
                        reminderList
                );


        homeReminderList.setAdapter(
                reminderAdapter
        );


        // =================================================
        // LOAD REMINDERS
        // =================================================

        loadUpcomingReminders();


        // =================================================
        // PROFILE CARD
        // =================================================

        cardProfile.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Home.this,
                            Profilepage.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);

        });


        // =================================================
        // CALENDAR CARD
        // =================================================

        cardCalendar.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Home.this,
                            Calendar.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);

        });


        // =================================================
        // MEMORIES CARD
        // =================================================

        cardReminder.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Home.this,
                            Memories.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);

        });

    }


    // =====================================================
    // LOAD SAVED PROFILE PHOTO
    // =====================================================

    private void loadHomeProfilePhoto() {

        // Make sure ImageView exists

        if (imgPetHome == null) {

            return;

        }


        // =================================================
        // GET SHARED PREFERENCES
        // =================================================

        SharedPreferences preferences =
                getSharedPreferences(
                        "PetProfile",
                        MODE_PRIVATE
                );


        // =================================================
        // GET SAVED PHOTO PATH
        // =================================================

        String photoPath =
                preferences.getString(
                        "profilePhoto_" + username,
                        null
                );


        // =================================================
        // NO PHOTO SAVED
        // =================================================

        if (photoPath == null ||
                photoPath.isEmpty()) {

            imgPetHome.setImageResource(
                    R.drawable.pet_logo
            );

            return;

        }


        // =================================================
        // CREATE FILE FROM SAVED PATH
        // =================================================

        File photoFile =
                new File(photoPath);


        // =================================================
        // CHECK IF FILE EXISTS
        // =================================================

        if (!photoFile.exists()) {

            imgPetHome.setImageResource(
                    R.drawable.pet_logo
            );

            return;

        }


        // =================================================
        // LOAD BITMAP
        // =================================================

        Bitmap bitmap =
                BitmapFactory.decodeFile(
                        photoFile.getAbsolutePath()
                );


        // =================================================
        // DISPLAY PHOTO
        // =================================================

        if (bitmap != null) {

            imgPetHome.setImageBitmap(
                    bitmap
            );

        } else {

            imgPetHome.setImageResource(
                    R.drawable.pet_logo
            );

        }

    }


    // =====================================================
    // LOAD UPCOMING REMINDERS
    // =====================================================

    private void loadUpcomingReminders() {

        reminderRef.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            DataSnapshot snapshot) {

                        reminderList.clear();


                        // =================================
                        // NO REMINDERS
                        // =================================

                        if (!snapshot.exists()) {

                            reminderList.add(
                                    "No upcoming reminders"
                            );

                            reminderAdapter
                                    .notifyDataSetChanged();

                            return;

                        }


                        // =================================
                        // READ REMINDERS
                        // =================================

                        for (DataSnapshot data :
                                snapshot.getChildren()) {

                            Reminder reminder =
                                    data.getValue(
                                            Reminder.class
                                    );


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


                        // =================================
                        // EMPTY LIST
                        // =================================

                        if (reminderList.isEmpty()) {

                            reminderList.add(
                                    "No upcoming reminders"
                            );

                        }


                        reminderAdapter
                                .notifyDataSetChanged();

                    }


                    @Override
                    public void onCancelled(
                            DatabaseError error) {

                        reminderList.clear();


                        reminderList.add(
                                "Unable to load reminders"
                        );


                        reminderAdapter
                                .notifyDataSetChanged();

                    }

                }
        );

    }


    // =====================================================
    // RELOAD PHOTO WHEN RETURNING TO HOME
    // =====================================================

    @Override
    protected void onResume() {

        super.onResume();


        if (imgPetHome != null) {

            loadHomeProfilePhoto();

        }

    }

}