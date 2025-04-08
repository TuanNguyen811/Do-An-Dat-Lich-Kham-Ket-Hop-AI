package com.example.app1.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.adapter.ChatAdapter;
import com.example.app1.models.ChatHistoryResponse;
import com.example.app1.models.ChatRequest;
import com.example.app1.models.ChatResponse;
import com.example.app1.utils.SessionManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ImageButton newChatButton;
    private ProgressBar progressBar;
    private ImageView backButton;

    private ChatAdapter chatAdapter;
    private SessionManager sessionManager;
    private AuthService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view_chat);
        messageEditText = findViewById(R.id.edit_text_message);
        sendButton = findViewById(R.id.button_send);
        newChatButton = findViewById(R.id.button_newChat);
        progressBar = findViewById(R.id.progress_bar);
        backButton = findViewById(R.id.btn_back);

        // Setup RecyclerView
        chatAdapter = new ChatAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Setup API service
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getAuthService(this);

        // Set click listener for send button
        sendButton.setOnClickListener(v -> sendMessage());

        // Set click listener for new chat button
        newChatButton.setOnClickListener(v -> showNewChatConfirmation());

        // Set editor action listener for EditText (for keyboard send button)
        messageEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
        messageEditText.requestFocus();

        // Set click listener for back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Load chat history
        loadChatHistory();
    }

    private void showNewChatConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Cuộc trò chuyện mới")
                .setMessage("Bạn có muốn bắt đầu cuộc trò chuyện mới không? Cuộc trò chuyện hiện tại sẽ kết thúc.")
                .setPositiveButton("Đồng ý", (dialog, which) -> startNewChat())
                .setNegativeButton("Hủy", null)
                .show();

    }
    private void startNewChat() {
        // Reset the API chat session (if needed)
        resetChatSession();
        chatAdapter.clearMessages();
        // Show welcome message
        sendMessageToApi("Bạn hãy đóng vai là một bác sĩ tên là Bác sĩ Tuân, chuyên tư vấn sức khỏe. Tôi sẽ hỏi bạn về các vấn đề sức khỏe, bạn chỉ cần tập trung vào tư vấn y tế thôi, không lan man sang chủ đề khác. Trả lời ngắn gọn, rõ ràng như đang nhắn tin. Hãy bắt đầu bằng một lời chào và giới thiệu ngắn nhé!");
        loadChatHistory();
    }

    private void resetChatSession() {
        String token = "Bearer " + sessionManager.getToken();

        apiService.resetChatSession(token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    // Session reset successfully
                } else {
                    // Handle error
                    Toast.makeText(ChatbotActivity.this, "Không thể đặt lại phiên trò chuyện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Handle network failure
                Toast.makeText(ChatbotActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChatHistory() {
        showLoading(true);
        String token = "Bearer " + sessionManager.getToken();
        apiService.getChatHistory(token).enqueue(new Callback<List<ChatHistoryResponse>>() {
            @Override
            public void onResponse(Call<List<ChatHistoryResponse>> call, Response<List<ChatHistoryResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ChatHistoryResponse history = response.body().get(0);
                    if (history.getHistoryChat() != null && !history.getHistoryChat().isEmpty()) {
                        chatAdapter.setMessages(history.getHistoryChat());
                        scrollToBottom();
                    } else {
                        startNewChat();
                    }
                } else {
                    startNewChat();
                }
            }
            @Override
            public void onFailure(Call<List<ChatHistoryResponse>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(ChatbotActivity.this, "Không thể tải lịch sử tin nhắn: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        // Add user message to chat
        ChatHistoryResponse.ChatMessage userMessage = new ChatHistoryResponse.ChatMessage();
        userMessage.setText(message);
        userMessage.setType("user");
        chatAdapter.addMessage(userMessage);
        scrollToBottom();

        // Clear input field
        messageEditText.setText("");

        // Send message to API
        sendMessageToApi(message);
    }

    private void sendMessageToApi(String message) {
        showLoading(true);
        String token = "Bearer " + sessionManager.getToken();
        ChatRequest request = new ChatRequest(message);

        apiService.sendChatMessage(token, request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ChatResponse botResponse = response.body();

                    // Add bot response to chat
                    ChatHistoryResponse.ChatMessage botMessage = new ChatHistoryResponse.ChatMessage();
                    botMessage.setText(botResponse.getReply());
                    botMessage.setType("bot");
                    chatAdapter.addMessage(botMessage);
                    scrollToBottom();
                } else {
                    // Handle error response
                    ChatHistoryResponse.ChatMessage errorMessage = new ChatHistoryResponse.ChatMessage();
                    errorMessage.setText("Xin lỗi, có lỗi xảy ra. Vui lòng thử lại sau.");
                    errorMessage.setType("bot");
                    chatAdapter.addMessage(errorMessage);
                    scrollToBottom();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                showLoading(false);
                // Handle network failure
                ChatHistoryResponse.ChatMessage errorMessage = new ChatHistoryResponse.ChatMessage();
                errorMessage.setText("Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng và thử lại.");
                errorMessage.setType("bot");
                chatAdapter.addMessage(errorMessage);
                scrollToBottom();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            sendButton.setEnabled(false);
            newChatButton.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            sendButton.setEnabled(true);
            newChatButton.setEnabled(true);
        }
    }

    private void scrollToBottom() {
        recyclerView.post(() -> {
            if (chatAdapter.getItemCount() > 0) {
                recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });
    }
}