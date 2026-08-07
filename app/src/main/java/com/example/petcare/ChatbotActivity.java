package com.example.petcare;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatbotActivity extends AppCompatActivity {
    private GeminiService geminiService;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend, btnBack;

    private ArrayList<Message> messageList;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);

        messageList = new ArrayList<>();

        adapter = new MessageAdapter(messageList);
        geminiService = new GeminiService();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Welcome message
        messageList.add(new Message(
                "Hello! I'm PetCare AI 🐾\nHow can I help you today?",
                false));
        adapter.notifyItemInserted(messageList.size() - 1);

        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {

        String userMessage = etMessage.getText().toString().trim();

        if(userMessage.isEmpty())
            return;

        // User message
        messageList.add(new Message(userMessage,true));
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

        geminiService.askGemini(userMessage,new GeminiService.GeminiCallback() {

            @Override
            public void onSuccess(String response) {

                runOnUiThread(() -> {

                    // Remove typing message
                    messageList.remove(typingIndex);
                    adapter.notifyItemRemoved(typingIndex);

                    // Add AI response
                    messageList.add(new Message(response,false));
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

}