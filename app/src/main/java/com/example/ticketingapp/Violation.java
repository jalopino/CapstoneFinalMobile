package com.example.ticketingapp;

public class Violation {
    private int violationId;
    private int userId;
    private String name;
    private String violation;
    private String date;
    // Other fields as per your requirement

    // Constructor
    public Violation(int violationId, int userId, String name, String violation, String date /*, other parameters */) {
        this.violationId = violationId;
        this.userId = userId;
        this.name = name;
        this.violation = violation;
        this.date = date;
        // Initialize other fields
    }

    // Getters
    public int getViolationId() {
        return violationId;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getViolation() {
        return violation;
    }

    public String getDate() {
        return date;
    }

    // ... getters for other fields

    // Setters
    public void setViolationId(int violationId) {
        this.violationId = violationId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // ... setters for other fields
}