package com.example.petcare;

public class Message {

    private String message;
    private boolean user;

    // Required by Firebase
    public Message() {
    }

    public Message(String message, boolean user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return user;
    }
}