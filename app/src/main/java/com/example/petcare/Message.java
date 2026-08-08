package com.example.petcare;

public class Message {

    private String message;
    private boolean isUser;

    public Message() {
        // Required for Firebase
    }

    public Message(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}