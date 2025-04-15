package com.example.app1.models;

public class Notification {
    private String type;
    private String message;
    private String scheduled_time;
    private String status;

    public Notification(String type, String message, String scheduledTime, String status) {
        this.type = type;
        this.message = message;
        this.scheduled_time = scheduledTime;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getScheduledTime() {
        return scheduled_time;
    }

    public String getStatus() {
        return status;
    }
}