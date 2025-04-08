package com.example.app1.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatHistoryResponse {
    @SerializedName("full_name")
    private String full_name;

    @SerializedName("history_chat")
    private List<ChatMessage> historyChat;
    public ChatHistoryResponse() {
    }
    public ChatHistoryResponse(String full_name, List<ChatMessage> historyChat) {
        this.full_name = full_name;
        this.historyChat = historyChat;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public List<ChatMessage> getHistoryChat() {
        return historyChat;
    }

    public void setHistoryChat(List<ChatMessage> historyChat) {
        this.historyChat = historyChat;
    }

    public static class ChatMessage {
        @SerializedName("text")
        private String text;

        @SerializedName("role")
        private String type;

        public ChatMessage() {
        }
        public ChatMessage(String text, String type) {
            this.text = text;
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
