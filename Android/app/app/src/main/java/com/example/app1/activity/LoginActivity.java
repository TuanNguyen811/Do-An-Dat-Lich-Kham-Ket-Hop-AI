package com.example.app1.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app1.R;
import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.utils.TokenResponse;
import com.example.app1.utils.SessionManager;
import com.example.app1.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private TextView textViewRegister, login_loi, forgetPassword;
    private AuthService authService;
    private SessionManager sessionManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        login_loi = findViewById(R.id.tv_error_message);
        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        textViewRegister = findViewById(R.id.tv_register);
        forgetPassword = findViewById(R.id.tv_forgot_password);
        // Initialize API client and SessionManager
        authService = ApiClient.getAuthService(this);
        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            //navigateToProfile();
//return;
        }

        // Set button click listener
        buttonLogin.setOnClickListener(v -> loginUser());

        // Set register text click listener
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        forgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPassswordActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        // Get input values
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate inputs
        if (!ValidationUtils.isValidEmail(email)) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            editTextPassword.setError("Password must be at least 8 characters");
            editTextPassword.requestFocus();
            return;
        }

        // Show progress
        setLoading(true);

        // Call login API
        authService.login(email, password, "Patient").enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Save token to session
                    TokenResponse tokenResponse = response.body();
                    sessionManager.saveToken(tokenResponse.getFullToken());

                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    // Handle error
                    String errorMessage = "Login failed. Please check your credentials.";
                    if (response.code() == 401) {
                        errorMessage = "Invalid email or password";
                        login_loi.setVisibility(View.VISIBLE);
                        login_loi.setText("Invalid email or password");
                    } else if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            login_loi.setVisibility(View.VISIBLE);

                            login_loi.setText("Error reading error body");
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                    Log.e(TAG, "Login error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                setLoading(false);
                login_loi.setVisibility(View.VISIBLE);

                login_loi.setText("Network error: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Login network error: ", t);
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            buttonLogin.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            buttonLogin.setEnabled(true);
        }
    }
}