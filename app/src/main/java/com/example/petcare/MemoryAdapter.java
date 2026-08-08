package com.example.petcare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MemoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Bitmap> images;
    private ArrayList<java.io.File> imageFiles;


    public MemoryAdapter(
            Context context,
            ArrayList<Bitmap> images,
            ArrayList<java.io.File> imageFiles
    ) {

        this.context = context;
        this.images = images;
        this.imageFiles = imageFiles;
    }


    @Override
    public int getCount() {
        return images.size();
    }


    @Override
    public Object getItem(int position) {
        return images.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {

        if (convertView == null) {

            convertView = LayoutInflater.from(context)
                    .inflate(
                            R.layout.item_memory,
                            parent,
                            false
                    );
        }


        ImageView image =
                convertView.findViewById(
                        R.id.imgMemory
                );


        TextView date =
                convertView.findViewById(
                        R.id.txtDate
                );


        ImageButton btnDelete =
                convertView.findViewById(
                        R.id.btnDelete
                );


        // Set image

        image.setImageBitmap(
                images.get(position)
        );


        // Get actual file date

        java.io.File file =
                imageFiles.get(position);


        String currentDate =
                new SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                ).format(
                        new Date(file.lastModified())
                );


        date.setText(currentDate);


        // ------------------------------------------------
        // OPEN PHOTO
        // ------------------------------------------------

        image.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            FullImageActivity.class
                    );


            intent.putExtra(
                    "imagePath",
                    file.getAbsolutePath()
            );


            context.startActivity(intent);
        });


        // ------------------------------------------------
        // DELETE PHOTO
        // ------------------------------------------------

        btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Delete Memory")
                    .setMessage(
                            "Are you sure you want to delete this photo?"
                    )
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> {

                                if (file.exists()) {

                                    boolean deleted =
                                            file.delete();


                                    if (deleted) {

                                        images.remove(position);
                                        imageFiles.remove(position);

                                        notifyDataSetChanged();


                                        Toast.makeText(
                                                context,
                                                "Memory deleted",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                    } else {

                                        Toast.makeText(
                                                context,
                                                "Unable to delete photo",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                }
                            }
                    )
                    .show();
        });


        return convertView;
    }
}