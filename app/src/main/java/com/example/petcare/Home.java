package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {
    private DatabaseReference reminderRef;
    private CardView cardProfile, cardCalendar, cardReminder;
    private ImageButton btnHelpline;
    private Button btnHelp;
    private TextView txtWelcome;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        Button btnbot = findViewById(R.id.btnbot);

        btnbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, ChatbotActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        txtWelcome = findViewById(R.id.txtWelcome);
        TextView txtUpcomingReminder = findViewById(R.id.txtUpcomingReminder);
        username = getIntent().getStringExtra("username");
        reminderRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(username)
                .child("reminders");
        loadUpcomingReminder(txtUpcomingReminder);
        Toast.makeText(this, "Username = " + username, Toast.LENGTH_LONG).show();
        if (username == null)
            username = "";

        txtWelcome.setText("Welcome, " + username + "!");

        cardProfile = findViewById(R.id.cardProfile);
        cardCalendar = findViewById(R.id.cardCalendar);
        cardReminder = findViewById(R.id.cardReminder);

        cardProfile.setOnClickListener(v -> {

            Intent intent = new Intent(Home.this, Profilepage.class);
            intent.putExtra("username", username);
            startActivity(intent);

        });

        cardCalendar.setOnClickListener(v -> {

            Intent intent = new Intent(Home.this, Calendar.class);

            intent.putExtra("username", username);

            startActivity(intent);

        });

        cardReminder.setOnClickListener(v ->
                startActivity(new Intent(Home.this, Memories.class)));

    }
    private void loadUpcomingReminder(TextView txtUpcomingReminder) {

        reminderRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    txtUpcomingReminder.setText("No upcoming reminders");
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {

                    Reminder reminder = data.getValue(Reminder.class);

                    if (reminder != null) {

                        txtUpcomingReminder.setText(
                                reminder.getTitle() +
                                        "\n" +
                                        reminder.getDate() +
                                        "  " +
                                        reminder.getTime()
                        );

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}