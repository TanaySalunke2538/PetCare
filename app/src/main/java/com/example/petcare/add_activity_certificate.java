package com.example.petcare;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class add_activity_certificate extends AppCompatActivity {

    // =========================================================
    // UI COMPONENTS
    // =========================================================

    private TextView btnBack;

    private LinearLayout uploadContainer;

    private ImageView imgCertificate;

    private Button btnCamera;
    private Button btnGallery;
    private Button btnSaveCertificate;

    private EditText etCertificateName;
    private EditText etNote;


    // =========================================================
    // IMAGE DATA
    // =========================================================

    private Uri selectedImageUri;

    private Bitmap cameraBitmap;


    // =========================================================
    // LOCAL STORAGE
    // =========================================================

    private SharedPreferences preferences;


    // =========================================================
    // GALLERY LAUNCHER
    // =========================================================

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri != null) {

                            selectedImageUri = uri;

                            // Clear camera image
                            cameraBitmap = null;

                            // Show selected image
                            imgCertificate.setImageURI(uri);
                            imgCertificate.setVisibility(View.VISIBLE);

                            // Hide upload card
                            uploadContainer.setVisibility(View.GONE);
                        }
                    }
            );


    // =========================================================
    // CAMERA LAUNCHER
    // =========================================================

    private final ActivityResultLauncher<Void> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicturePreview(),
                    bitmap -> {

                        if (bitmap != null) {

                            cameraBitmap = bitmap;

                            // Clear gallery image
                            selectedImageUri = null;

                            // Show camera image
                            imgCertificate.setImageBitmap(bitmap);
                            imgCertificate.setVisibility(View.VISIBLE);

                            // Hide upload card
                            uploadContainer.setVisibility(View.GONE);
                        }
                    }
            );


    // =========================================================
    // CAMERA PERMISSION
    // =========================================================

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {

                        if (granted) {

                            openCamera();

                        } else {

                            Toast.makeText(
                                    add_activity_certificate.this,
                                    "Camera permission is required",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_activity_certificate);


        // =====================================================
        // LOCAL STORAGE INITIALIZATION
        // =====================================================

        preferences = getSharedPreferences(
                "PetCareHealthDocuments",
                Context.MODE_PRIVATE
        );


        // =====================================================
        // FIND VIEWS
        // =====================================================

        btnBack = findViewById(R.id.btnBack);

        uploadContainer = findViewById(R.id.uploadContainer);

        imgCertificate = findViewById(R.id.imgCertificate);

        btnCamera = findViewById(R.id.btnCamera);

        btnGallery = findViewById(R.id.btnGallery);

        btnSaveCertificate =
                findViewById(R.id.btnSaveCertificate);

        etCertificateName =
                findViewById(R.id.etCertificateName);

        etNote =
                findViewById(R.id.etNote);


        // =====================================================
        // BACK BUTTON
        // =====================================================

        btnBack.setOnClickListener(v -> finish());


        // =====================================================
        // UPLOAD CARD
        // =====================================================

        uploadContainer.setOnClickListener(v -> openGallery());


        // =====================================================
        // GALLERY BUTTON
        // =====================================================

        btnGallery.setOnClickListener(v -> openGallery());


        // =====================================================
        // CAMERA BUTTON
        // =====================================================

        btnCamera.setOnClickListener(v -> checkCameraPermission());


        // =====================================================
        // SAVE BUTTON
        // =====================================================

        btnSaveCertificate.setOnClickListener(v -> saveCertificate());
    }


    // =========================================================
    // OPEN GALLERY
    // =========================================================

    private void openGallery() {

        galleryLauncher.launch("image/*");
    }


    // =========================================================
    // CHECK CAMERA PERMISSION
    // =========================================================

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {

            openCamera();

        } else {

            cameraPermissionLauncher.launch(
                    Manifest.permission.CAMERA
            );
        }
    }


    // =========================================================
    // OPEN CAMERA
    // =========================================================

    private void openCamera() {

        cameraLauncher.launch(null);
    }


    // =========================================================
    // SAVE CERTIFICATE LOCALLY
    // =========================================================

    private void saveCertificate() {

        String certificateName =
                etCertificateName
                        .getText()
                        .toString()
                        .trim();

        String note =
                etNote
                        .getText()
                        .toString()
                        .trim();


        // =====================================================
        // CHECK IMAGE
        // =====================================================

        if (selectedImageUri == null && cameraBitmap == null) {

            Toast.makeText(
                    this,
                    "Please upload a certificate first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =====================================================
        // CHECK CERTIFICATE NAME
        // =====================================================

        if (certificateName.isEmpty()) {

            etCertificateName.setError(
                    "Enter certificate name"
            );

            etCertificateName.requestFocus();

            return;
        }


        // =====================================================
        // CHECK NOTE
        // =====================================================

        if (note.isEmpty()) {

            etNote.setError(
                    "Add a note about this certificate"
            );

            etNote.requestFocus();

            return;
        }


        // =====================================================
        // IMAGE PATH
        // =====================================================

        String imagePath = "";


        // =====================================================
        // GALLERY IMAGE
        // =====================================================

        if (selectedImageUri != null) {

            imagePath = selectedImageUri.toString();
        }


        // =====================================================
        // CAMERA IMAGE
        // =====================================================

        else if (cameraBitmap != null) {

            imagePath = saveCameraImage(cameraBitmap);

            if (imagePath.isEmpty()) {

                Toast.makeText(
                        this,
                        "Unable to save photo",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }
        }


        // =====================================================
        // SAVE DETAILS LOCALLY
        // =====================================================

        preferences.edit()
                .putString(
                        "certificate_name",
                        certificateName
                )
                .putString(
                        "certificate_note",
                        note
                )
                .putString(
                        "certificate_image",
                        imagePath
                )
                .putLong(
                        "certificate_time",
                        System.currentTimeMillis()
                )
                .putBoolean(
                        "certificate_exists",
                        true
                )
                .apply();


        // =====================================================
        // SUCCESS MESSAGE
        // =====================================================

        Toast.makeText(
                this,
                "Certificate saved successfully",
                Toast.LENGTH_SHORT
        ).show();


        // =====================================================
        // RETURN TO HEALTH
        // =====================================================

        finish();
    }


    // =========================================================
    // SAVE CAMERA IMAGE TO LOCAL STORAGE
    // =========================================================

    private String saveCameraImage(Bitmap bitmap) {

        File file = new File(
                getFilesDir(),
                "pet_certificate_" +
                        System.currentTimeMillis() +
                        ".jpg"
        );


        try {

            FileOutputStream outputStream =
                    new FileOutputStream(file);


            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    outputStream
            );


            outputStream.flush();

            outputStream.close();


            return file.getAbsolutePath();

        } catch (IOException e) {

            e.printStackTrace();

            return "";
        }
    }
}