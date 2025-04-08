package com.example.app1.models;

import com.google.gson.annotations.SerializedName;

public class ChatRequest {
    @SerializedName("message")
    private String message;
    private String type = "user";

    public ChatRequest(String message) {
        this.message = message;
    }

    public ChatRequest(String message, String type) {
        this.message = message;
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}