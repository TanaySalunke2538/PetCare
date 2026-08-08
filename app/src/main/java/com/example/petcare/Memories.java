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
import android.widget.ImageButton;
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
import java.util.ArrayList;

public class Memories extends AppCompatActivity {

    private Button btnCamera, btnGallery;
    private ImageButton btnBack;
    private GridView gridMemories;
    private TextView txtEmpty;

    private ArrayList<Bitmap> memories;
    private ArrayList<File> imageFiles;

    private MemoryAdapter adapter;

    private Uri imageUri;
    private File cameraPhotoFile;


    // Camera Launcher
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::onCameraResult
            );


    // Gallery Launcher
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null) {

                            try {

                                Uri uri = result.getData().getData();

                                if (uri == null) {
                                    return;
                                }

                                Bitmap bitmap =
                                        MediaStore.Images.Media.getBitmap(
                                                getContentResolver(),
                                                uri
                                        );

                                File folder = getExternalFilesDir(
                                        Environment.DIRECTORY_PICTURES
                                );

                                if (folder == null) {
                                    Toast.makeText(
                                            this,
                                            "Unable to access storage",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    return;
                                }

                                if (!folder.exists()) {
                                    folder.mkdirs();
                                }

                                File file = new File(
                                        folder,
                                        "IMG_" +
                                                System.currentTimeMillis() +
                                                ".jpg"
                                );

                                FileOutputStream out =
                                        new FileOutputStream(file);

                                bitmap.compress(
                                        Bitmap.CompressFormat.JPEG,
                                        100,
                                        out
                                );

                                out.flush();
                                out.close();

                                bitmap.recycle();

                                loadSavedImages();

                                Toast.makeText(
                                        this,
                                        "Photo added to Memories",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } catch (Exception e) {

                                e.printStackTrace();

                                Toast.makeText(
                                        this,
                                        "Unable to add photo",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.memories);


        // Find views

        btnBack = findViewById(R.id.btnBack);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        gridMemories = findViewById(R.id.gridMemories);
        txtEmpty = findViewById(R.id.txtEmpty);


        // Back button

        btnBack.setOnClickListener(v -> finish());


        // Lists

        memories = new ArrayList<>();
        imageFiles = new ArrayList<>();


        // Adapter

        adapter = new MemoryAdapter(
                this,
                memories,
                imageFiles
        );


        gridMemories.setAdapter(adapter);


        // Load saved photos

        loadSavedImages();


        // Camera permission

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    101
            );
        }


        // Camera button

        btnCamera.setOnClickListener(v -> openCamera());


        // Gallery button

        btnGallery.setOnClickListener(v -> openGallery());
    }


    // ---------------------------------------------------
    // OPEN CAMERA
    // ---------------------------------------------------

    private void openCamera() {

        try {

            File folder = getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES
            );

            if (folder == null) {

                Toast.makeText(
                        this,
                        "Unable to access storage",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            if (!folder.exists()) {
                folder.mkdirs();
            }


            cameraPhotoFile = new File(
                    folder,
                    "IMG_" +
                            System.currentTimeMillis() +
                            ".jpg"
            );


            imageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    cameraPhotoFile
            );


            Intent intent =
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    imageUri
            );


            intent.addFlags(
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
            );


            cameraLauncher.launch(intent);


        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Unable to open camera",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ---------------------------------------------------
    // CAMERA RESULT
    // ---------------------------------------------------

    private void onCameraResult(ActivityResult result) {

        if (result.getResultCode() == RESULT_OK) {

            if (cameraPhotoFile != null &&
                    cameraPhotoFile.exists()) {

                loadSavedImages();

                Toast.makeText(
                        this,
                        "Photo added to Memories",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "Photo was not saved",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            // Camera was cancelled.
            // Delete empty file if it was created.

            if (cameraPhotoFile != null &&
                    cameraPhotoFile.exists()) {

                cameraPhotoFile.delete();
            }
        }
    }


    // ---------------------------------------------------
    // OPEN GALLERY
    // ---------------------------------------------------

    private void openGallery() {

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );

        galleryLauncher.launch(intent);
    }


    // ---------------------------------------------------
    // LOAD SAVED IMAGES
    // ---------------------------------------------------

    private void loadSavedImages() {

        File folder = getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
        );


        if (folder == null) {
            return;
        }


        if (!folder.exists()) {
            folder.mkdirs();
        }


        File[] files = folder.listFiles();


        memories.clear();
        imageFiles.clear();


        if (files != null) {

            for (File file : files) {

                if (!file.isFile()) {
                    continue;
                }


                Bitmap bitmap =
                        BitmapFactory.decodeFile(
                                file.getAbsolutePath()
                        );


                if (bitmap != null) {

                    memories.add(bitmap);
                    imageFiles.add(file);
                }
            }
        }


        adapter.notifyDataSetChanged();


        if (memories.isEmpty()) {

            txtEmpty.setVisibility(View.VISIBLE);

        } else {

            txtEmpty.setVisibility(View.GONE);
        }
    }


    // ---------------------------------------------------
    // CAMERA PERMISSION RESULT
    // ---------------------------------------------------

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );


        if (requestCode == 101) {

            if (grantResults.length > 0 &&
                    grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(
                        this,
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}