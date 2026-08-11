package com.example.petcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private DatabaseReference reminderRef;
    private FirebaseFirestore db;

    private CardView cardProfile;
    private CardView cardCalendar;
    private CardView cardReminder;
    private CardView cardUpcomingReminders;

    private Button btnbot;

    private TextView txtWelcome;

    private ListView homeReminderList;

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


        // =====================================================
        // PET PROFILE
        // NO ANIMATION
        // =====================================================

        txtPetNameHome =
                findViewById(R.id.txtPetNameHome);

        txtPetAgeHome =
                findViewById(R.id.txtPetAgeHome);

        imgPetHome =
                findViewById(R.id.imgPetHome);


        // =====================================================
        // CHAT WITH ALEX
        // NO ANIMATION
        // =====================================================

        btnbot =
                findViewById(R.id.btnbot);


        // =====================================================
        // GET USERNAME
        // =====================================================

        username =
                getIntent().getStringExtra("username");

        if (username == null) {
            username = "";
        }


        // =====================================================
        // FIRESTORE
        // =====================================================

        db =
                FirebaseFirestore.getInstance();


        // =====================================================
        // WELCOME
        // =====================================================

        txtWelcome =
                findViewById(R.id.txtWelcome);

        txtWelcome.setText(
                "Welcome, " + username + "!"
        );


        // =====================================================
        // CHAT WITH ALEX
        // NO ANIMATION
        // =====================================================

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


        // =====================================================
        // LOAD PET PROFILE
        // =====================================================

        loadPetProfile();

        loadPetPhoto();


        // =====================================================
        // HOME CARDS
        // =====================================================

        cardProfile =
                findViewById(R.id.cardProfile);

        cardCalendar =
                findViewById(R.id.cardCalendar);

        cardReminder =
                findViewById(R.id.cardReminder);

        cardUpcomingReminders =
                findViewById(R.id.cardUpcomingReminders);


        // =====================================================
        // FIREBASE REMINDER REFERENCE
        // =====================================================

        reminderRef =
                FirebaseDatabase
                        .getInstance()
                        .getReference("Users")
                        .child(username)
                        .child("reminders");


        // =====================================================
        // REMINDER LIST
        // =====================================================

        homeReminderList =
                findViewById(R.id.homeReminderList);

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


        // =====================================================
        // LOAD REMINDERS
        // =====================================================

        loadUpcomingReminders();


        // =====================================================
        // START ANIMATION
        // =====================================================

        startHomeAnimations();


        // =====================================================
        // PROFILE
        // =====================================================

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


        // =====================================================
        // CALENDAR
        // =====================================================

        cardCalendar.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Home.this,
                            com.example.petcare.Calendar.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });


        // =====================================================
        // MEMORIES
        // =====================================================

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
    // HOME PAGE ANIMATIONS
    // =====================================================

    private void startHomeAnimations() {

        DecelerateInterpolator interpolator =
                new DecelerateInterpolator(1.5f);


        // =====================================================
        // INITIAL POSITIONS
        // =====================================================

        // Profile
        cardProfile.setAlpha(0f);
        cardProfile.setTranslationY(100f);


        // Calendar
        cardCalendar.setAlpha(0f);
        cardCalendar.setTranslationY(100f);


        // Memories
        cardReminder.setAlpha(0f);
        cardReminder.setTranslationY(100f);


        // Upcoming Reminders
        cardUpcomingReminders.setAlpha(0f);
        cardUpcomingReminders.setTranslationY(100f);


        // =====================================================
        // PROFILE
        // =====================================================

        cardProfile.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(150)
                .setDuration(550)
                .setInterpolator(interpolator)
                .start();


        // =====================================================
        // CALENDAR
        // =====================================================

        cardCalendar.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(300)
                .setDuration(550)
                .setInterpolator(interpolator)
                .start();


        // =====================================================
        // MEMORIES
        // =====================================================

        cardReminder.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(450)
                .setDuration(550)
                .setInterpolator(interpolator)
                .start();


        // =====================================================
        // UPCOMING REMINDERS
        // =====================================================

        cardUpcomingReminders.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(600)
                .setDuration(550)
                .setInterpolator(interpolator)
                .start();


        // =====================================================
        // NOT ANIMATED:
        //
        // cardPetSummary
        // btnbot
        // =====================================================
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


    // =====================================================
    // LOAD PET PROFILE
    // =====================================================

    private void loadPetProfile() {

        db.collection("users")
                .document(username)
                .collection("profile")
                .document("details")
                .get()

                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        txtPetNameHome.setText(
                                "Your Pet"
                        );

                        txtPetAgeHome.setText(
                                "Add profile"
                        );

                        return;
                    }


                    // PET NAME

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


                    // PET DOB

                    String dob =
                            document.getString(
                                    "dob"
                            );


                    if (dob != null &&
                            !dob.isEmpty()) {

                        String age =
                                calculateAge(dob);

                        txtPetAgeHome.setText(
                                age
                        );

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

        if (dob == null ||
                dob.trim().isEmpty()) {

            return "Age unknown";
        }


        try {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            sdf.setLenient(false);


            Date birthDate =
                    sdf.parse(dob);


            if (birthDate == null) {

                return "Age unknown";
            }


            java.util.Calendar birth =
                    java.util.Calendar.getInstance();

            birth.setTime(birthDate);


            java.util.Calendar today =
                    java.util.Calendar.getInstance();


            int years =
                    today.get(
                            java.util.Calendar.YEAR
                    )
                            -
                            birth.get(
                                    java.util.Calendar.YEAR
                            );


            int months =
                    today.get(
                            java.util.Calendar.MONTH
                    )
                            -
                            birth.get(
                                    java.util.Calendar.MONTH
                            );


            int days =
                    today.get(
                            java.util.Calendar.DAY_OF_MONTH
                    )
                            -
                            birth.get(
                                    java.util.Calendar.DAY_OF_MONTH
                            );


            if (days < 0) {
                months--;
            }


            if (months < 0) {

                years--;
                months += 12;
            }


            return years +
                    " yrs " +
                    months +
                    " months";


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


        String photoPath =
                preferences.getString(
                        "profilePhoto_" + username,
                        null
                );


        if (photoPath == null ||
                photoPath.isEmpty()) {

            imgPetHome.setImageResource(
                    R.drawable.pet_logo
            );

            return;
        }


        File photoFile =
                new File(photoPath);


        if (!photoFile.exists()) {

            imgPetHome.setImageResource(
                    R.drawable.pet_logo
            );

            return;
        }


        Bitmap bitmap =
                BitmapFactory.decodeFile(
                        photoFile.getAbsolutePath()
                );


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
    // REFRESH PET DATA
    // =====================================================

    @Override
    protected void onResume() {

        super.onResume();


        if (username != null &&
                !username.isEmpty()) {

            loadPetPhoto();

            loadPetProfile();
        }
    }
}