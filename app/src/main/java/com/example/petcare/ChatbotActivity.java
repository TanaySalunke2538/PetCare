package com.example.petcare;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatbotActivity extends AppCompatActivity {
    private GeminiService geminiService;
    private DatabaseReference chatRef;
    private ImageButton btnClearChat;
    private String username;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend, btnBack;
    private TextView typingIndicator;
    private ArrayList<Message> messageList;
    private MessageAdapter adapter;
    private boolean isWaitingForResponse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        username = getIntent().getStringExtra("username");

        if (username == null) {
            username = "";
        }

        android.util.Log.d(
                "CHAT_DEBUG",
                "Username = " + username
        );

        // Create Firebase reference FIRST
        chatRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(username)
                .child("chatHistory");

        android.util.Log.d(
                "CHAT_DEBUG",
                "Firebase chat reference created"
        );

        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        btnClearChat = findViewById(R.id.btnClearChat);
        typingIndicator = findViewById(R.id.typingIndicator);
        messageList = new ArrayList<>();

        adapter = new MessageAdapter(messageList);
        geminiService = new GeminiService();

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);
        recyclerView.addOnLayoutChangeListener(
                (v, left, top, right, bottom,
                 oldLeft, oldTop, oldRight, oldBottom) -> {

                    if (bottom < oldBottom) {
                        recyclerView.post(() ->
                                recyclerView.scrollToPosition(
                                        messageList.size() - 1
                                )
                        );
                    }
                }
        );

        // Welcome message
        loadChatHistory();

        btnBack.setOnClickListener(
                v -> finish()
        );
        btnClearChat.setOnClickListener(v -> showClearChatDialog());

        btnSend.setOnClickListener(
                v -> sendMessage()
        );
    }

    private void sendMessage() {
        if (isWaitingForResponse) {
            return;
        }

        String userMessage = etMessage.getText().toString().trim();

        if(userMessage.isEmpty())
            return;

        isWaitingForResponse = true;

        // User message
        Message userMessageObject = new Message(userMessage, true);

        messageList.add(userMessageObject);

        saveMessageToFirebase(userMessageObject);

        adapter.notifyItemInserted(messageList.size()-1);
        recyclerView.scrollToPosition(messageList.size()-1);

        etMessage.setText("");

        // Disable send button while waiting
        btnSend.setEnabled(false);
        typingIndicator.setVisibility(View.VISIBLE);
        ArrayList<Message> conversationHistory =
                new ArrayList<>(messageList);


        geminiService.askGemini(
                conversationHistory,
                new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String response) {

                runOnUiThread(() -> {

                    typingIndicator.setVisibility(View.GONE);

                    // Add AI response
                    Message botMessage = new Message(response, false);

                    messageList.add(botMessage);
                    saveMessageToFirebase(botMessage);
                    adapter.notifyItemInserted(messageList.size()-1);

                    recyclerView.scrollToPosition(messageList.size()-1);

                    isWaitingForResponse = false;
                    btnSend.setEnabled(true);


                });

            }

            @Override
            public void onError(String error) {

                runOnUiThread(() -> {

                    typingIndicator.setVisibility(View.GONE);

                    messageList.add(
                            new Message(
                                    "⚠ Unable to contact Gemini.\n\n"+error,
                                    false));

                    adapter.notifyItemInserted(messageList.size()-1);

                    recyclerView.scrollToPosition(messageList.size()-1);
                    isWaitingForResponse = false;
                    btnSend.setEnabled(true);

                });

            }

        });

    }
    private void saveMessageToFirebase(Message message) {

        android.util.Log.d(
                "CHAT_DEBUG",
                "saveMessageToFirebase() CALLED"
        );

        android.util.Log.d(
                "CHAT_DEBUG",
                "Username = [" + username + "]"
        );

        if (username == null || username.isEmpty()) {

            android.util.Log.e(
                    "CHAT_DEBUG",
                    "Username is empty. Firebase write cancelled."
            );

            return;
        }

        String messageId = chatRef.push().getKey();

        if (messageId == null) {

            android.util.Log.e(
                    "CHAT_DEBUG",
                    "Could not generate message ID."
            );

            return;
        }

        android.util.Log.d(
                "CHAT_DEBUG",
                "Generated message ID = " + messageId
        );

        chatRef.child(messageId)
                .setValue(message)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        android.util.Log.d(
                                "CHAT_DEBUG",
                                "Message saved successfully!"
                        );

                    } else {

                        Exception error = task.getException();

                        android.util.Log.e(
                                "CHAT_DEBUG",
                                "Firebase save failed: "
                                        + (error != null
                                        ? error.getMessage()
                                        : "Unknown error"),
                                error
                        );
                    }
                });
    }
    private void loadChatHistory() {

        if (username.isEmpty()) {

            showWelcomeMessage();
            return;
        }

        chatRef.addListenerForSingleValueEvent(
                new com.google.firebase.database.ValueEventListener() {

                    @Override
                    public void onDataChange(
                            com.google.firebase.database.DataSnapshot snapshot) {

                        messageList.clear();

                        if (snapshot.exists()) {

                            for (com.google.firebase.database.DataSnapshot data
                                    : snapshot.getChildren()) {

                                Message message =
                                        data.getValue(Message.class);

                                if (message != null) {
                                    messageList.add(message);
                                }
                            }

                        }

                        // No previous conversation
                        if (messageList.isEmpty()) {
                            showWelcomeMessage();
                        }

                        adapter.notifyDataSetChanged();

                        if (!messageList.isEmpty()) {

                            recyclerView.scrollToPosition(
                                    messageList.size() - 1
                            );
                        }

                        android.util.Log.d(
                                "CHAT_DEBUG",
                                "Chat history loaded. Messages = "
                                        + messageList.size()
                        );
                    }

                    @Override
                    public void onCancelled(
                            com.google.firebase.database.DatabaseError error) {

                        android.util.Log.e(
                                "CHAT_DEBUG",
                                "Failed to load chat history: "
                                        + error.getMessage()
                        );

                        showWelcomeMessage();
                    }
                }
        );
    }
    private void showWelcomeMessage() {

        messageList.add(
                new Message(
                        "Hello! I'm PetCare AI 🐾\nHow can I help you today?",
                        false
                )
        );

        adapter.notifyItemInserted(
                messageList.size() - 1
        );
    }
    private void showClearChatDialog() {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Clear chat?")
                .setMessage(
                        "This will permanently delete your PetCare AI conversation."
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Clear", (dialog, which) -> clearChat())
                .show();
    }
    private void clearChat() {

        if (username == null || username.isEmpty()) {
            return;
        }

        chatRef.removeValue()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        messageList.clear();

                        adapter.notifyDataSetChanged();

                        showWelcomeMessage();

                        android.widget.Toast.makeText(
                                ChatbotActivity.this,
                                "Chat cleared",
                                android.widget.Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Exception error = task.getException();

                        android.widget.Toast.makeText(
                                ChatbotActivity.this,
                                "Unable to clear chat",
                                android.widget.Toast.LENGTH_SHORT
                        ).show();

                        android.util.Log.e(
                                "CHAT_DEBUG",
                                "Clear chat failed",
                                error
                        );
                    }
                });
    }
}