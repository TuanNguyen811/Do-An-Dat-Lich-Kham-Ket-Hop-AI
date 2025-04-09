package com.example.app1.models;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("reply")
    private String reply;
    //time_vn
    @SerializedName("time_vn")
    private String time_vn;
    @SerializedName("type")
    private String type = "bot";

    public ChatResponse(String reply) {
        this.reply = reply;
    }
    public ChatResponse(String reply, String type) {
        this.reply = reply;
        this.type = type;
    }
    public ChatResponse(String reply, String type, String time_vn) {
        this.reply = reply;
        this.type = type;
        this.time_vn = time_vn;
    }
    public String getTime_vn() {
        return time_vn;
    }
    public void setTime_vn(String time_vn) {
        this.time_vn = time_vn;
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