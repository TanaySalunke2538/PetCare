package com.example.petcare;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;
import java.util.ArrayList;

public class ChatbotActivity extends AppCompatActivity {
    private GeminiService geminiService;
    private DatabaseReference chatRef;
    private ImageButton btnClearChat;
    private String username;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend, btnBack;

    private ArrayList<Message> messageList;
    private MessageAdapter adapter;

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
        messageList = new ArrayList<>();

        adapter = new MessageAdapter(messageList);
        geminiService = new GeminiService();

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);

        // Welcome message
        loadChatHistory();

        btnBack.setOnClickListener(
                v -> finish()
        );

        btnSend.setOnClickListener(
                v -> sendMessage()
        );
    }

    private void sendMessage() {

        String userMessage = etMessage.getText().toString().trim();

        if(userMessage.isEmpty())
            return;

        // User message
        Message userMessageObject = new Message(userMessage, true);

        messageList.add(userMessageObject);

        saveMessageToFirebase(userMessageObject);

        adapter.notifyItemInserted(messageList.size()-1);
        recyclerView.scrollToPosition(messageList.size()-1);

        etMessage.setText("");

        // Disable send button while waiting
        btnSend.setEnabled(false);

        // Typing indicator
        messageList.add(new Message("PetCare AI is typing...",false));
        adapter.notifyItemInserted(messageList.size()-1);
        recyclerView.scrollToPosition(messageList.size()-1);

        int typingIndex = messageList.size()-1;

        ArrayList<Message> conversationHistory = new ArrayList<>();

        for (Message message : messageList) {
            if (!message.getMessage().equals("PetCare AI is typing...")) {
                conversationHistory.add(message);
            }
        }

        geminiService.askGemini(
                conversationHistory,
                new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String response) {

                runOnUiThread(() -> {

                    // Remove typing message
                    messageList.remove(typingIndex);
                    adapter.notifyItemRemoved(typingIndex);

                    // Add AI response
                    Message botMessage = new Message(response, false);

                    messageList.add(botMessage);
                    saveMessageToFirebase(botMessage);
                    adapter.notifyItemInserted(messageList.size()-1);

                    recyclerView.scrollToPosition(messageList.size()-1);

                    btnSend.setEnabled(true);

                });

            }

            @Override
            public void onError(String error) {

                runOnUiThread(() -> {

                    messageList.remove(typingIndex);
                    adapter.notifyItemRemoved(typingIndex);

                    messageList.add(
                            new Message(
                                    "⚠ Unable to contact Gemini.\n\n"+error,
                                    false));

                    adapter.notifyItemInserted(messageList.size()-1);

                    recyclerView.scrollToPosition(messageList.size()-1);

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
}