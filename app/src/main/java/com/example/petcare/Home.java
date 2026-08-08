package com.example.petcare;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends AppCompatActivity {

    private DatabaseReference reminderRef;
    private FirebaseFirestore db;
    private CardView cardProfile;
    private CardView cardCalendar;
    private CardView cardReminder;
    private Button btnbot;
    private TextView txtWelcome;

    private ListView homeReminderList;
    private TextView txtUpcomingReminder;
    private ArrayList<String> reminderList;
    private ArrayAdapter<String> reminderAdapter;
    private TextView txtPetNameHome;
    private TextView txtPetAgeHome;
    private ImageView imgPetHome;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);
        txtPetNameHome =
                findViewById(R.id.txtPetNameHome);

        txtPetAgeHome =
                findViewById(R.id.txtPetAgeHome);

        imgPetHome =
                findViewById(R.id.imgPetHome);

        btnbot =
                findViewById(R.id.btnbot);
        cardProfile =
                findViewById(R.id.cardProfile);
        // Get username
        username = getIntent().getStringExtra("username");

        if (username == null) {
            username = "";
        }

        db = FirebaseFirestore.getInstance();
        // Welcome text
        txtWelcome = findViewById(R.id.txtWelcome);
        txtWelcome.setText("Welcome, " + username + "!");

        // AI Bot button
        btnbot.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Home.this, ChatbotActivity.class);

            intent.putExtra("username", username);

            startActivity(intent);
        });
        loadPetProfile();

        loadPetPhoto();

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
                    new Intent(
                            Home.this,
                            com.example.petcare.Calendar.class
                    );

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

    private void loadPetProfile() {

        db.collection("users")
                .document(username)
                .collection("profile")
                .document("details")
                .get()

                .addOnSuccessListener(document -> {

                    // =====================================
                    // PROFILE DOES NOT EXIST
                    // =====================================

                    if (!document.exists()) {

                        txtPetNameHome.setText(
                                "Your Pet"
                        );

                        txtPetAgeHome.setText(
                                "Add profile"
                        );

                        return;
                    }


                    // =====================================
                    // PET NAME
                    // =====================================

                    String petName =
                            document.getString(
                                    "petName"
                            );


                    if (petName != null &&
                            !petName.isEmpty()) {

                        txtPetNameHome.setText(
                                petName
                        );

                    } else {

                        txtPetNameHome.setText(
                                "Your Pet"
                        );

                    }


                    // =====================================
                    // PET DOB
                    // =====================================

                    String dob =
                            document.getString(
                                    "dob"
                            );



                    if (dob != null && !dob.isEmpty()) {

                        String age = calculateAge(dob);

                        txtPetAgeHome.setText(age);

                    } else {

                        txtPetAgeHome.setText(
                                "Age unknown"
                        );

                    }


                })

                .addOnFailureListener(e -> {

                    txtPetNameHome.setText(
                            "Your Pet"
                    );

                    txtPetAgeHome.setText(
                            "Age unavailable"
                    );

                });

    }


    // =====================================================
    // CALCULATE PET AGE
    // =====================================================

    private String calculateAge(String dob) {

        if (dob == null || dob.trim().isEmpty()) {
            return "Age unknown";
        }

        try {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            sdf.setLenient(false);

            Date birthDate = sdf.parse(dob);

            if (birthDate == null) {
                return "Age unknown";
            }

            java.util.Calendar birth = java.util.Calendar.getInstance();
            birth.setTime(birthDate);

            java.util.Calendar today = java.util.Calendar.getInstance();

            int years =
                    today.get(java.util.Calendar.YEAR)
                            - birth.get(java.util.Calendar.YEAR);

            int months =
                    today.get(java.util.Calendar.MONTH)
                            - birth.get(java.util.Calendar.MONTH);

            int days =
                    today.get(java.util.Calendar.DAY_OF_MONTH)
                            - birth.get(java.util.Calendar.DAY_OF_MONTH);

            // Birthday has not occurred this month
            if (days < 0) {
                months--;
            }

            // Adjust months when necessary
            if (months < 0) {
                years--;
                months += 12;
            }

            return years + " yrs " + months + " months";

        } catch (ParseException e) {

            return "Age unknown";
        }
    }



    // =====================================================
    // LOAD PET PHOTO
    // =====================================================

    private void loadPetPhoto() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "PetProfile",
                        MODE_PRIVATE
                );


        // =====================================
        // GET SAVED FILE PATH
        // =====================================

        String photoPath =
                preferences.getString(
                        "profilePhoto_" + username,
                        null
                );


        // =====================================
        // NO PHOTO SAVED
        // =====================================

        if (photoPath == null ||
                photoPath.isEmpty()) {

            imgPetHome.setImageResource(
                    R.drawable.pet_logo
            );

            return;

        }


        // =====================================
        // CREATE FILE OBJECT
        // =====================================

        File photoFile =
                new File(photoPath);


        // =====================================
        // FILE DOES NOT EXIST
        // =====================================

        if (!photoFile.exists()) {

            imgPetHome.setImageResource(
                    R.drawable.pet_logo
            );

            return;

        }


        // =====================================
        // LOAD BITMAP
        // =====================================

        Bitmap bitmap =
                BitmapFactory.decodeFile(
                        photoFile.getAbsolutePath()
                );


        // =====================================
        // DISPLAY PHOTO
        // =====================================

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
    private void loadUpcomingReminder() {

        reminderRef.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            DataSnapshot snapshot) {

                        // =================================
                        // NO REMINDERS
                        // =================================

                        if (!snapshot.exists()) {

                            txtUpcomingReminder.setText(
                                    "No upcoming reminders"
                            );

                            return;

                        }


                        // =================================
                        // FIND REMINDER
                        // =================================

                        for (DataSnapshot data :
                                snapshot.getChildren()) {

                            Reminder reminder =
                                    data.getValue(
                                            Reminder.class
                                    );


                            if (reminder != null) {

                                txtUpcomingReminder.setText(
                                        reminder.getTitle()
                                                + "\n"
                                                + reminder.getDate()
                                                + "  "
                                                + reminder.getTime()
                                );

                                break;

                            }

                        }

                    }


                    @Override
                    public void onCancelled(
                            DatabaseError error) {

                        txtUpcomingReminder.setText(
                                "No upcoming reminders"
                        );

                    }

                }
        );

    }


    // =====================================================
    // REFRESH PET DATA WHEN RETURNING TO HOME
    // =====================================================

    @Override
    protected void onResume() {

        super.onResume();


        // Reload photo and profile whenever
        // we return to Home

        if (username != null &&
                !username.isEmpty()) {

            loadPetPhoto();

            loadPetProfile();

        }

    }


}