package com.example.app1.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app1.R;

public class IntroActivity extends AppCompatActivity {
    private Button button_getStarted;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        boolean introShown = getSharedPreferences("app_prefs", MODE_PRIVATE).getBoolean("intro_shown", false);
        if (introShown) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        // Add logic to proceed to MainActivity after showing the intro
        findViewById(R.id.button_getStarted).setOnClickListener(v -> {
            // Save the state that intro has been shown
            getSharedPreferences("app_prefs", MODE_PRIVATE).edit().putBoolean("intro_shown", true).apply();
            // Proceed to MainActivity
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
        });
    }
}