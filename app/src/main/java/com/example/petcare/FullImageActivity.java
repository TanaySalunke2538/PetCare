package com.example.petcare;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class FullImageActivity extends AppCompatActivity {

    private ImageView imgFull;
    private ImageButton btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_image);


        imgFull = findViewById(R.id.imgFull);
        btnBack = findViewById(R.id.btnBack);


        btnBack.setOnClickListener(v -> finish());


        String imagePath =
                getIntent().getStringExtra("imagePath");


        if (imagePath != null) {

            File file = new File(imagePath);


            if (file.exists()) {

                imgFull.setImageURI(
                        android.net.Uri.fromFile(file)
                );
            }
        }
    }
}