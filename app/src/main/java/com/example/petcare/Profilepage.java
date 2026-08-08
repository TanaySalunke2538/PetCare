package com.example.petcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Profilepage extends AppCompatActivity {

    private TextInputEditText etPetName;
    private TextInputEditText etDOB;
    private TextInputEditText etOwnerName;
    private TextInputEditText etPhone;
    private TextInputEditText etEmail;

    private Button btnSaveProfile;
    private Button btnChangePhoto;

    private ImageButton btnBack;

    private ImageView imgProfile;

    private FirebaseFirestore db;

    private String username;


    // =====================================================
    // GALLERY LAUNCHER
    // =====================================================

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri != null) {

                            savePhotoPermanently(uri);

                        }

                    });


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.profilepage);


        // =================================================
        // FIND VIEWS
        // =================================================

        imgProfile = findViewById(R.id.imgProfile);

        btnChangePhoto =
                findViewById(R.id.btnChangePhoto);

        btnSaveProfile =
                findViewById(R.id.btnSaveProfile);

        btnBack =
                findViewById(R.id.btnBack);


        etPetName =
                findViewById(R.id.etPetName);

        etDOB =
                findViewById(R.id.etDOB);

        etOwnerName =
                findViewById(R.id.etOwnerName);

        etPhone =
                findViewById(R.id.etPhone);

        etEmail =
                findViewById(R.id.etEmail);


        // =================================================
        // FIRESTORE
        // =================================================

        db = FirebaseFirestore.getInstance();


        // =================================================
        // GET USERNAME
        // =================================================

        username =
                getIntent().getStringExtra("username");


        if (username == null || username.isEmpty()) {

            username = "Guest";

        }


        // =================================================
        // LOAD EXISTING DATA
        // =================================================

        loadProfile();

        loadSavedPhoto();


        // =================================================
        // CHANGE PHOTO
        // =================================================

        btnChangePhoto.setOnClickListener(v -> {

            galleryLauncher.launch("image/*");

        });


        // =================================================
        // CREATE PROFILE
        // =================================================

        btnSaveProfile.setOnClickListener(v -> {

            saveProfile();

        });


        // =================================================
        // BACK BUTTON
        // =================================================

        btnBack.setOnClickListener(v -> {

            finish();

        });

    }


    // =====================================================
    // SAVE PHOTO PERMANENTLY
    // =====================================================

    private void savePhotoPermanently(Uri uri) {

        try {

            // Open selected image
            InputStream inputStream =
                    getContentResolver()
                            .openInputStream(uri);


            if (inputStream == null) {

                Toast.makeText(
                        this,
                        "Could not open image",
                        Toast.LENGTH_SHORT
                ).show();

                return;

            }


            // Convert image to Bitmap
            Bitmap bitmap =
                    BitmapFactory.decodeStream(
                            inputStream
                    );


            inputStream.close();


            if (bitmap == null) {

                Toast.makeText(
                        this,
                        "Could not load image",
                        Toast.LENGTH_SHORT
                ).show();

                return;

            }


            // =================================================
            // CREATE PERMANENT FILE
            // =================================================

            File photoFile =
                    new File(
                            getFilesDir(),
                            "pet_photo_" +
                                    username +
                                    ".jpg"
                    );


            FileOutputStream outputStream =
                    new FileOutputStream(
                            photoFile
                    );


            // Save image as JPEG
            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    outputStream
            );


            outputStream.flush();

            outputStream.close();


            // =================================================
            // SHOW IMAGE
            // =================================================

            imgProfile.setImageBitmap(bitmap);


            // =================================================
            // SAVE FILE PATH
            // =================================================

            SharedPreferences preferences =
                    getSharedPreferences(
                            "PetProfile",
                            MODE_PRIVATE
                    );


            preferences.edit()
                    .putString(
                            "profilePhoto_" + username,
                            photoFile.getAbsolutePath()
                    )
                    .apply();


            Toast.makeText(
                    this,
                    "Photo updated",
                    Toast.LENGTH_SHORT
            ).show();


        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error loading photo: " +
                            e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

        }

    }


    // =====================================================
    // LOAD SAVED PHOTO
    // =====================================================

    private void loadSavedPhoto() {

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


        // =================================================
        // NO PHOTO
        // =================================================

        if (photoPath == null ||
                photoPath.isEmpty()) {

            imgProfile.setImageResource(
                    R.drawable.pet_logo
            );

            return;

        }


        // =================================================
        // CHECK FILE
        // =================================================

        File photoFile =
                new File(photoPath);


        if (!photoFile.exists()) {

            imgProfile.setImageResource(
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


        if (bitmap != null) {

            imgProfile.setImageBitmap(
                    bitmap
            );

        } else {

            imgProfile.setImageResource(
                    R.drawable.pet_logo
            );

        }

    }


    // =====================================================
    // SAVE PROFILE
    // =====================================================

    private void saveProfile() {

        String petName =
                etPetName.getText()
                        .toString()
                        .trim();


        String dob =
                etDOB.getText()
                        .toString()
                        .trim();


        String owner =
                etOwnerName.getText()
                        .toString()
                        .trim();


        String phone =
                etPhone.getText()
                        .toString()
                        .trim();


        String email =
                etEmail.getText()
                        .toString()
                        .trim();


        // =================================================
        // VALIDATION
        // =================================================

        if (petName.isEmpty() ||
                dob.isEmpty() ||
                owner.isEmpty() ||
                phone.isEmpty() ||
                email.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }


        // =================================================
        // CREATE FIRESTORE MAP
        // =================================================

        Map<String, Object> profile =
                new HashMap<>();


        profile.put(
                "petName",
                petName
        );


        profile.put(
                "dob",
                dob
        );


        profile.put(
                "ownerName",
                owner
        );


        profile.put(
                "phone",
                phone
        );


        profile.put(
                "email",
                email
        );


        // =================================================
        // SAVE TO FIRESTORE
        // =================================================

        db.collection("users")
                .document(username)
                .collection("profile")
                .document("details")
                .set(profile)


                // =================================================
                // SUCCESS
                // =================================================

                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            Profilepage.this,
                            "Profile Created Successfully",
                            Toast.LENGTH_SHORT
                    ).show();


                    // =============================================
                    // GO TO HOME
                    // =============================================

                    Intent intent =
                            new Intent(
                                    Profilepage.this,
                                    Home.class
                            );


                    intent.putExtra(
                            "username",
                            username
                    );


                    startActivity(intent);


                    finish();

                })


                // =================================================
                // FAILURE
                // =================================================

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            Profilepage.this,
                            "Error: " +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });

    }


    // =====================================================
    // LOAD PROFILE FROM FIRESTORE
    // =====================================================

    private void loadProfile() {

        db.collection("users")
                .document(username)
                .collection("profile")
                .document("details")
                .get()


                .addOnSuccessListener(
                        documentSnapshot -> {


                            if (!documentSnapshot.exists()) {

                                return;

                            }


                            // =================================
                            // PET NAME
                            // =================================

                            String petName =
                                    documentSnapshot.getString(
                                            "petName"
                                    );


                            if (petName != null) {

                                etPetName.setText(
                                        petName
                                );

                            }


                            // =================================
                            // DOB
                            // =================================

                            String dob =
                                    documentSnapshot.getString(
                                            "dob"
                                    );


                            if (dob != null) {

                                etDOB.setText(
                                        dob
                                );

                            }


                            // =================================
                            // OWNER
                            // =================================

                            String owner =
                                    documentSnapshot.getString(
                                            "ownerName"
                                    );


                            if (owner != null) {

                                etOwnerName.setText(
                                        owner
                                );

                            }


                            // =================================
                            // PHONE
                            // =================================

                            String phone =
                                    documentSnapshot.getString(
                                            "phone"
                                    );


                            if (phone != null) {

                                etPhone.setText(
                                        phone
                                );

                            }


                            // =================================
                            // EMAIL
                            // =================================

                            String email =
                                    documentSnapshot.getString(
                                            "email"
                                    );


                            if (email != null) {

                                etEmail.setText(
                                        email
                                );

                            }

                        })


                .addOnFailureListener(e -> {

                    Toast.makeText(
                            Profilepage.this,
                            "Could not load profile",
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }

}