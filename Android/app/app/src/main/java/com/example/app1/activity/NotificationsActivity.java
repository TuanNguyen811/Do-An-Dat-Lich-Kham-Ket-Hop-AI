package com.example.app1.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NotificationsActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;
    private AuthService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //khai báo API, token
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getAuthService(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_thongBao);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                getOnBackPressedDispatcher().onBackPressed();
                finish();
                overridePendingTransition(0, 0);
            } else if (itemId == R.id.nav_lichKham) {
                startActivity(new Intent(this, AppointmentsActivity.class));
                finish();
                overridePendingTransition(0, 0);
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                overridePendingTransition(0, 0);
            } else if (itemId == R.id.nav_thongBao) {

            }
            return true;
        });

    }
    private boolean cheackToken() {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            showAlertAndRedirect();
            return false;
        }
        return true;
    }
    private void showAlertAndRedirect() {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Token rỗng, vui lòng đăng nhập lại.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Redirect to LoginActivity
                        Intent intent = new Intent(NotificationsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Close the current activity
                    }
                })
                .show();
    }
}