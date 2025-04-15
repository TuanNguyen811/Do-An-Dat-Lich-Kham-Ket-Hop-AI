/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.desktop.models;

import java.time.LocalDateTime;

public class NotificationCreate {
    private int user_id;
    private String type;
    private String message;
    private LocalDateTime scheduled_time;

    public NotificationCreate(int user_id, String type, String message, LocalDateTime scheduled_time) {
        this.user_id = user_id;
        this.type = type;
        this.message = message;
        this.scheduled_time = scheduled_time;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getScheduled_time() {
        return scheduled_time;
    }

    // Setters nếu cần
}
