package com.example.app1.models;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("reply")
    private String reply;

    @SerializedName("type")
    private String type = "bot";

    public ChatResponse(String reply) {
        this.reply = reply;
    }
    public ChatResponse(String reply, String type) {
        this.reply = reply;
        this.type = type;
    }
    public String getReply() {
        return reply;
    }
    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}