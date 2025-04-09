package com.example.app1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.R;
import com.example.app1.models.ChatHistoryResponse.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import android.text.Html;
import android.text.Spanned;
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER_MESSAGE = 1;
    private static final int VIEW_TYPE_BOT_MESSAGE = 2;

    private List<ChatMessage> messages;

    public ChatAdapter() {
        this.messages = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bot_message, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ChatMessage message = new ChatMessage("Bắt đầu", "user");
            if (holder instanceof UserMessageViewHolder) {
                ((UserMessageViewHolder) holder).bind(message);
            }
        } else {
            ChatMessage message = messages.get(position);
            if (holder instanceof UserMessageViewHolder) {
                ((UserMessageViewHolder) holder).bind(message);
            } else if (holder instanceof BotMessageViewHolder) {

                ((BotMessageViewHolder) holder).bind(message);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if ("user".equals(message.getType())) {
            return VIEW_TYPE_USER_MESSAGE;
        } else {
            return VIEW_TYPE_BOT_MESSAGE;
        }
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        UserMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_user);
        }

        void bind(ChatMessage message) {
            if (message.getText().startsWith("!")) {
                messageText.setText("Chuẩn đoán...");
            } else {
                messageText.setText(message.getText());
            }

        }
    }

    static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        BotMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_bot);
        }

        void bind(ChatMessage message) {
            String formatted = message.getText()
                    .replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>")         // **bold**
                    .replaceAll("\\*(.*?)\\*", "<i>$1</i>")               // *italic*
                    .replaceAll("\\*/(.*?)\\*/", "<u>$1</u>")             // */underline*/
                    .replaceAll("\\n", "<br>");                           // xuống dòng

            Spanned spanned = Html.fromHtml(formatted, Html.FROM_HTML_MODE_LEGACY);
            messageText.setText(spanned);
        }
    }
}