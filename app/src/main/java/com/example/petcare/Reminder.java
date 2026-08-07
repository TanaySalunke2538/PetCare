package com.example.petcare;

public class Reminder {

    private String title;
    private String category;
    private String date;
    private String time;
    private String notes;

    public Reminder() {
        // Required for Firebase
    }

    public Reminder(String title,
                    String category,
                    String date,
                    String time,
                    String notes) {

        this.title = title;
        this.category = category;
        this.date = date;
        this.time = time;
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return title + " - " + time;
    }
    public String getNotes() {
        return notes;
    }
}