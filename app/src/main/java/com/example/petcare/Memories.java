package com.example.petcare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Memories extends AppCompatActivity {

    private Button btnCamera, btnGallery;
    private GridView gridMemories;
    private TextView txtEmpty;

    private ArrayList<Bitmap> memories;
    private MemoryAdapter adapter;

    // Camera Launcher
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::onActivityResult);

    // Gallery Launcher
    ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null) {

                            try {

                                Uri uri = result.getData().getData();

                                Bitmap bitmap =
                                        MediaStore.Images.Media.getBitmap(
                                                getContentResolver(),
                                                uri);

                                File file = new File(
                                        getExternalFilesDir(
                                                Environment.DIRECTORY_PICTURES),
                                        "IMG_" + System.currentTimeMillis() + ".jpg");

                                FileOutputStream out =
                                        new FileOutputStream(file);

                                bitmap.compress(
                                        Bitmap.CompressFormat.JPEG,
                                        100,
                                        out);

                                out.flush();
                                out.close();

                                loadSavedImages();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.memories);

        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        gridMemories = findViewById(R.id.gridMemories);
        txtEmpty = findViewById(R.id.txtEmpty);

        memories = new ArrayList<>();

        adapter = new MemoryAdapter(this, memories);
        loadSavedImages();
        gridMemories.setAdapter(adapter);

        // Camera Permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    101);
        }

        btnCamera.setOnClickListener(v -> openCamera());

        btnGallery.setOnClickListener(v -> openGallery());

    }

    private Uri imageUri;

    private void openCamera() {

        try {

            File photoFile = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "IMG_" + System.currentTimeMillis() + ".jpg");

            imageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    photoFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            cameraLauncher.launch(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openGallery() {

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        galleryLauncher.launch(intent);

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults);

        if (requestCode == 101) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void loadSavedImages() {

        File folder =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (folder == null)
            return;

        File[] files = folder.listFiles();

        if (files == null)
            return;

        memories.clear();

        for (File file : files) {

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            if (bitmap != null)
                memories.add(bitmap);

        }

        adapter.notifyDataSetChanged();

        txtEmpty.setVisibility(
                memories.isEmpty() ? View.VISIBLE : View.GONE);

    }
    private void onActivityResult(ActivityResult result) {

        if (result.getResultCode() == RESULT_OK) {

            try {

                Bitmap bitmap =
                        MediaStore.Images.Media.getBitmap(
                                getContentResolver(),
                                imageUri);

                loadSavedImages();
                txtEmpty.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}