package com.example.app1.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app1.models.Notification;
import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.adapter.NotificationsAdapter;
import com.example.app1.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;
    private AuthService apiService;
    private RecyclerView recycler_view_notifications, recycler_view_notifications_old;
    private NotificationsAdapter adapter, adapterOld;
    private List<Notification> notificationList, notificationListOld;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);
        // Set up the window insets to adjust for system bars

        // Apply window insets
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
        recycler_view_notifications = findViewById(R.id.recycler_view_notifications);
        recycler_view_notifications_old = findViewById(R.id.recycler_view_notifications_old);

        recycler_view_notifications.setLayoutManager(new LinearLayoutManager(this));
        recycler_view_notifications_old.setLayoutManager(new LinearLayoutManager(this));
        // Sample data for testing
        notificationList = new ArrayList<>();
        notificationListOld = new ArrayList<>();

        adapter = new NotificationsAdapter(notificationList);
        recycler_view_notifications.setAdapter(adapter);

        adapterOld = new NotificationsAdapter(notificationListOld);
        recycler_view_notifications_old.setAdapter(adapterOld);

        loadNotifications();
    }
    private void loadNotifications() {
        // Call your API to fetch notifications here
        // For example:
        apiService.getNotifications(sessionManager.getToken()).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    notificationList.clear();
                    notificationListOld.clear();
                    List<Notification> notifications = response.body();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    Date currentTime = new Date(); // Thời gian hiện tại

                    notifications.sort((Notification n1, Notification n2) -> {
                        try {
                            Date date1 = sdf.parse(n1.getScheduledTime());
                            Date date2 = sdf.parse(n2.getScheduledTime());
                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0; // Nếu có lỗi, không thay đổi thứ tự
                        }
                    });


                    for (Notification notification : notifications) {
                        try {
                            Date scheduledTime = sdf.parse(notification.getScheduledTime());
                            if (scheduledTime.before(currentTime)) {
                                notificationList.add(notification);
                            } else {
                                notificationListOld.add(notification);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    adapterOld.notifyDataSetChanged();
                } else {
                    Toast.makeText(NotificationsActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(NotificationsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}