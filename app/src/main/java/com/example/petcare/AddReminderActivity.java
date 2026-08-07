package com.example.petcare;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    EditText etTitle, etNotes;
    Spinner spCategory;
    Button btnDate, btnTime, btnSave;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.addreminderactivity);
        String username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        etTitle = findViewById(R.id.etTitle);
        etNotes = findViewById(R.id.etNotes);

        spCategory = findViewById(R.id.spCategory);

        btnDate = findViewById(R.id.btnSelectDate);
        btnTime = findViewById(R.id.btnSelectTime);
        btnSave = findViewById(R.id.btnSave);

        String[] categories = {
                "Vaccination",
                "Medicine",
                "Vet Visit",
                "Grooming",
                "Exercise",
                "Feeding"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories);

        spCategory.setAdapter(adapter);

        btnDate.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();

            new DatePickerDialog(
                    this,
                    (view, year, month, day) ->
                            btnDate.setText(day + "/" + (month + 1) + "/" + year),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();

        });

        btnTime.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();

            new TimePickerDialog(
                    this,
                    (view, hour, minute) ->
                            btnTime.setText(hour + ":" + minute),
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE),
                    false
            ).show();

        });

        btnSave.setOnClickListener(v -> {

            Toast.makeText(this, "Save button clicked", Toast.LENGTH_SHORT).show();

            String title = etTitle.getText().toString().trim();

            String category = spCategory.getSelectedItem().toString();

            String date = btnDate.getText().toString();

            String time = btnTime.getText().toString();

            String notes = etNotes.getText().toString().trim();

            if(title.isEmpty()){

                etTitle.setError("Enter reminder title");
                return;

            }

            if (btnDate.getText().toString().equals("Select Date")) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (btnTime.getText().toString().equals("Select Time")) {
                Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
                return;
            }


            String reminderId = databaseReference
                    .child(username)
                    .child("reminders")
                    .push()
                    .getKey();
            Toast.makeText(this, "ID = " + reminderId, Toast.LENGTH_LONG).show();
            Reminder reminder = new Reminder(
                    title,
                    category,
                    date,
                    time,
                    notes
            );

            databaseReference
                    .child(username)
                    .child("reminders")
                    .child(reminderId)
                    .setValue(reminder)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Reminder Saved", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this,
                                "Save Failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });

        });

    }
}
