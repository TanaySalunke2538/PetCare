package com.example.petcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class health extends AppCompatActivity {

    // =========================================================
    // UI COMPONENTS
    // =========================================================

    private TextView btnBack;
    private TextView tvDocumentCount;

    private LinearLayout emptyState;
    private LinearLayout documentsContainer;


    // =========================================================
    // LOCAL STORAGE
    // =========================================================

    private SharedPreferences preferences;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Load Health screen
        setContentView(R.layout.health);


        // =====================================================
        // FIND VIEWS
        // =====================================================

        btnBack = findViewById(R.id.btnBack);

        tvDocumentCount =
                findViewById(R.id.tvDocumentCount);

        emptyState =
                findViewById(R.id.emptyState);

        documentsContainer =
                findViewById(R.id.documentsContainer);


        // =====================================================
        // BACK BUTTON
        // =====================================================

        btnBack.setOnClickListener(v -> {

            finish();

        });


        // =====================================================
        // ADD CERTIFICATE BUTTON
        // =====================================================

        findViewById(R.id.btnAddCertificate)
                .setOnClickListener(v -> {

                    android.content.Intent intent =
                            new android.content.Intent(
                                    health.this,
                                    add_activity_certificate.class
                            );

                    startActivity(intent);

                });


        // =====================================================
        // LOCAL STORAGE
        // =====================================================

        preferences = getSharedPreferences(
                "PetCareHealthDocuments",
                Context.MODE_PRIVATE
        );


        // =====================================================
        // LOAD DOCUMENTS
        // =====================================================

        loadCertificate();
    }


    // =========================================================
    // ON RESUME
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        // Reload certificate when coming back
        // from Add Certificate page

        if (preferences != null) {

            loadCertificate();
        }
    }


    // =========================================================
    // LOAD CERTIFICATE
    // =========================================================

    private void loadCertificate() {

        boolean certificateExists =
                preferences.getBoolean(
                        "certificate_exists",
                        false
                );


        // =====================================================
        // NO CERTIFICATE
        // =====================================================

        if (!certificateExists) {

            emptyState.setVisibility(View.VISIBLE);

            documentsContainer.setVisibility(View.GONE);

            tvDocumentCount.setText(
                    "0 documents"
            );

            return;
        }


        // =====================================================
        // CERTIFICATE EXISTS
        // =====================================================

        emptyState.setVisibility(View.GONE);

        documentsContainer.setVisibility(View.VISIBLE);

        tvDocumentCount.setText(
                "1 document"
        );


        // Remove previous card
        documentsContainer.removeAllViews();


        // Create certificate card
        createCertificateCard();
    }


    // =========================================================
    // CREATE CERTIFICATE CARD
    // =========================================================

    private void createCertificateCard() {

        // =====================================================
        // GET SAVED DATA
        // =====================================================

        String certificateName =
                preferences.getString(
                        "certificate_name",
                        "Health Certificate"
                );

        String note =
                preferences.getString(
                        "certificate_note",
                        ""
                );

        String imagePath =
                preferences.getString(
                        "certificate_image",
                        ""
                );


        // =====================================================
        // MAIN CARD
        // =====================================================

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(14),
                dp(14),
                dp(14),
                dp(14)
        );

        card.setBackgroundResource(
                R.drawable.button_light
        );

        card.setElevation(
                dp(3)
        );


        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                dp(4),
                0,
                dp(12)
        );

        card.setLayoutParams(cardParams);


        // =====================================================
        // IMAGE
        // =====================================================

        ImageView imageView =
                new ImageView(this);

        imageView.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        imageView.setBackgroundResource(
                R.drawable.button_light
        );

        imageView.setPadding(
                dp(5),
                dp(5),
                dp(5),
                dp(5)
        );


        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(190)
                );

        imageView.setLayoutParams(
                imageParams
        );


        // =====================================================
        // LOAD IMAGE
        // =====================================================

        if (imagePath != null &&
                !imagePath.isEmpty()) {

            try {

                // Gallery image
                if (imagePath.startsWith("content://") ||
                        imagePath.startsWith("file://")) {

                    imageView.setImageURI(
                            Uri.parse(imagePath)
                    );

                }

                // Camera image saved locally
                else {

                    File imageFile =
                            new File(imagePath);

                    if (imageFile.exists()) {

                        imageView.setImageURI(
                                Uri.fromFile(imageFile)
                        );

                    } else {

                        imageView.setImageResource(
                                android.R.drawable.ic_menu_gallery
                        );
                    }
                }

            } catch (Exception e) {

                imageView.setImageResource(
                        android.R.drawable.ic_menu_gallery
                );
            }

        } else {

            imageView.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }


        card.addView(imageView);


        // =====================================================
        // CERTIFICATE NAME
        // =====================================================

        TextView nameText =
                new TextView(this);

        nameText.setText(
                certificateName
        );

        nameText.setTextColor(
                Color.parseColor("#222222")
        );

        nameText.setTextSize(
                18
        );

        nameText.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        nameText.setPadding(
                4,
                dp(14),
                4,
                0
        );


        card.addView(nameText);


        // =====================================================
        // NOTE
        // =====================================================

        if (note != null &&
                !note.isEmpty()) {

            TextView noteText =
                    new TextView(this);

            noteText.setText(
                    note
            );

            noteText.setTextColor(
                    Color.parseColor("#777777")
            );

            noteText.setTextSize(
                    13
            );

            noteText.setPadding(
                    4,
                    dp(7),
                    4,
                    0
            );

            noteText.setLineSpacing(
                    dp(1),
                    1.0f
            );

            card.addView(noteText);
        }


        // =====================================================
        // DOCUMENT LABEL
        // =====================================================

        TextView label =
                new TextView(this);

        label.setText(
                "Health Document"
        );

        label.setTextColor(
                Color.parseColor("#F47C20")
        );

        label.setTextSize(
                12
        );

        label.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        label.setPadding(
                4,
                dp(10),
                4,
                0
        );


        card.addView(label);


        // =====================================================
        // ADD CARD TO CONTAINER
        // =====================================================

        documentsContainer.addView(
                card
        );
    }


    // =========================================================
    // DP CONVERSION
    // =========================================================

    private int dp(int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}