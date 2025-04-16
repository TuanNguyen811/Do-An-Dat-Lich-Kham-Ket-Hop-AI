package com.example.app1.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app1.R;
import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.models.EmailRequest;
import com.example.app1.models.VerifyOtpRequest;
import com.example.app1.utils.SessionManager;
import com.example.app1.utils.TokenResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgetPassswordActivity extends AppCompatActivity {

    private EditText etEmail, etOtp, etNewPassword;
    private Button btnSendOtp, btnResetPassword;
    private AuthService authService;
    private SessionManager sessionManager;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_passsword);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etEmail = findViewById(R.id.et_email);
        etOtp = findViewById(R.id.et_otp);
        etNewPassword = findViewById(R.id.et_new_password);
        btnSendOtp = findViewById(R.id.btn_send_otp);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        sessionManager = new SessionManager(this);

        // Initialize API service
        authService = ApiClient.getAuthService(this);

        // Send OTP button click
        btnSendOtp.setOnClickListener(v -> sendOtp());

        // Reset Password button click
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void sendOtp() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        authService.sendOtp(new EmailRequest(email)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgetPassswordActivity.this, "OTP sent to your email", Toast.LENGTH_SHORT).show();
                    etOtp.setVisibility(View.VISIBLE);
                    etNewPassword.setVisibility(View.VISIBLE);
                    btnResetPassword.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ForgetPassswordActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ForgetPassswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();
        String otp = etOtp.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (otp.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "OTP and new password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        authService.verifyOtp(new VerifyOtpRequest(email, otp, newPassword)).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    TokenResponse tokenResponse = response.body();
                    sessionManager.saveToken(tokenResponse.getFullToken());
                    // Optionally, you can save the token in SharedPreferences or a database
                    startActivity(new Intent(ForgetPassswordActivity.this, MainActivity.class));

                    Toast.makeText(ForgetPassswordActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ForgetPassswordActivity.this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Toast.makeText(ForgetPassswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}