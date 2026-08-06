package com.example.petcare;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MemoryAdapter extends BaseAdapter {

    Context context;
    ArrayList<Bitmap> images;

    public MemoryAdapter(Context context,
                         ArrayList<Bitmap> images) {

        this.context = context;
        this.images = images;
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
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_memory, parent, false);

        }

        ImageView image =
                convertView.findViewById(R.id.imgMemory);

        TextView date =
                convertView.findViewById(R.id.txtDate);

        image.setImageBitmap(images.get(position));

        String currentDate =
                new SimpleDateFormat("dd MMM yyyy",
                        Locale.getDefault()).format(new Date());

        date.setText(currentDate);

        return convertView;
    }
}