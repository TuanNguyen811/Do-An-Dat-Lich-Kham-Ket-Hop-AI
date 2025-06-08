package com.example.app1.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.adapter.DepartmentAdapter;
import com.example.app1.adapter.DepartmentAdapterVertcal;
import com.example.app1.models.Department;
import com.example.app1.models.Patient;
import com.example.app1.models.PatientHealthMetrics;
import com.example.app1.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;
    private AuthService apiService;
    private static final String TAG = "MainActivity";
    private TextView name, viewAll_main_departmainent, server;
    public static ImageView imageView_main_Avatar;
    private RecyclerView recyclerView;
    private DepartmentAdapter adapter;
    private ArrayList<Department> departmentList;
    private EditText searchView_main;
    private CardView headerLayout;

    private CardView cardView_main_appointment,  cardView_main_chatbot, cardView_main_PatientHealthMetrics;

    private Patient patient_user;
    private PatientHealthMetrics patientHealthMetrics;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setViewID();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
            } else if (itemId == R.id.nav_lichKham) {
                if (cheackToken()) {
                    startActivity(new Intent(MainActivity.this, AppointmentsActivity.class));
                    overridePendingTransition(0, 0);
                }
            } else if (itemId == R.id.nav_profile) {
                if (cheackToken()){
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                }
            } else if (itemId == R.id.nav_thongBao) {
                if (cheackToken()){
                    startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
                    overridePendingTransition(0, 0);
                }
            }
            return true;
        });

        //khai báo API, token
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getAuthService(this);
        server.setText("Server: " + ApiClient.getBaseUrl(this));
        server.setOnClickListener(v -> {
            showIpInputDialog(MainActivity.this);
        });
        //khai báo recyclerView và adapter
        departmentList = new ArrayList<>();
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);

        cardView_main_appointment.setOnClickListener(v -> {
            if (cheackToken()) {
                Intent intent = new Intent(MainActivity.this, AppointmentActivity.class);
                intent.putExtra("departments", new ArrayList<>(departmentList)); // Phải cast sang ArrayList nếu là List
                startActivity(intent);
            }

        });

        viewAll_main_departmainent.setOnClickListener(v -> {
            if (cheackToken()) {
                Intent intent = new Intent(MainActivity.this, ListDepartmentActivity.class);
                intent.putExtra("departments", new ArrayList<>(departmentList));
                startActivity(intent);
            }
        });
        imageView_main_Avatar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        cardView_main_chatbot.setOnClickListener(v -> {
            if (cheackToken()) {
                Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
                intent.putExtra("patient_user", patient_user);
                startActivity(intent);
            }
        });

        searchView_main.setOnClickListener(v -> {
            if (cheackToken()) {
                Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
                startActivity(intent);
            }
        });
        cardView_main_PatientHealthMetrics.setOnClickListener(v -> {
            if (cheackToken()) {
                Intent intent = new Intent(MainActivity.this, PatientHealthMetricsActivity.class);
                intent.putExtra("patient_user", patient_user);
                startActivity(intent);
            }
        });

        searchView_main.setFocusable(false);
        searchView_main.setFocusableInTouchMode(false);
        // If intro has been shown, proceed with setting the content view

        loadUserProfile();
        downloadAvatar();
        loadDepartments();
        loadPatientHealthMetrics();

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUserProfile();
                downloadAvatar();
                loadDepartments();
                loadPatientHealthMetrics();
            }
        });

    }

    public void setViewID(){
        server = findViewById(R.id.textView_main_server);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        name = findViewById(R.id.main_name);
        recyclerView = findViewById(R.id.recyclerView_main_department);
        cardView_main_appointment = findViewById(R.id.cardView_main_appointment);
        viewAll_main_departmainent = findViewById(R.id.viewAll_main_departmainent);
        imageView_main_Avatar = findViewById(R.id.imageView_main_Avatar);
        cardView_main_chatbot = findViewById(R.id.cardView_main_chatbot);
        searchView_main = findViewById(R.id.editText_main_chat);
        cardView_main_PatientHealthMetrics = findViewById(R.id.cardView_main_PatientHealthMetrics);
        headerLayout = findViewById(R.id.headerLayout);
    }
    public void showIpInputDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Nhập địa chỉ IP server");

        final EditText input = new EditText(context);

        String BASE_URL = ApiClient.getBaseUrl(context);

        input.setText(BASE_URL);
        builder.setView(input);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String ip = input.getText().toString().trim();
            if (!ip.isEmpty()) {
                // Lưu IP vào SharedPreferences
                ApiClient.setBaseUrl(context, ip);
                server.setText("Server: " + ip);
                apiService = ApiClient.getAuthService(context);
                Toast.makeText(context, "Đã lưu IP: " + ip, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
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
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Close the current activity
                    }
                })
                .show();
    }
    private void loadDepartments() {
        departmentList = new ArrayList<>();
        Call<List<Department>> call = apiService.getDepartments();
        call.enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(Call<List<Department>> call, Response<List<Department>> response) {
                if (response.isSuccessful()) {
                    departmentList = new ArrayList<>(response.body());

                    adapter = new DepartmentAdapter(departmentList, MainActivity.this, new DepartmentAdapter.OnDepartmentClickListener() {
                        @Override
                        public void onDepartmentClick(Department department) {
                            if (cheackToken()) {
                                Intent intent = new Intent(MainActivity.this, AppointmentActivity.class);
                                intent.putExtra("item_department", department); // gửi ID
                                intent.putExtra("departments", departmentList); // gửi danh sách
                                startActivity(intent);
                            }
                        }
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Department>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Không kết nối được: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void downloadAvatar() {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token rỗng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            return;
        }

        apiService.getAvatar("Bearer " + token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] bytes = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView_main_Avatar.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Không thể tải ảnh đại diện", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Không thể tải ảnh đại diện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadUserProfile() {
        // Show progress
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_LONG).show();
        }

        // Call user profile API
        apiService.getPatientProfile(token).enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(Call<Patient> call, Response<Patient> response) {
                patient_user = response.body();
                if (response.isSuccessful() && response.body() != null) {
                    name.setText("Xin chào! " + response.body().getFull_name());

                    Shader shader = new LinearGradient(
                            0, 0, name.getPaint().measureText(name.getText().toString()), 0,
                            new int[]{Color.parseColor("#FF8C00"), Color.parseColor("#FF1493"), Color.parseColor("#9370DB")},
                            null, Shader.TileMode.CLAMP);

                    name.getPaint().setShader(shader);
                } else {
                    // Handle error
                    if (response.code() == 401) {
                        // Token expired or invalid
                        Toast.makeText(MainActivity.this,
                                "Session expired. Please login again.",
                                Toast.LENGTH_LONG).show();
                        sessionManager.clearSession();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Failed to load profile. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                    Log.e(TAG, "Profile error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Patient> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "Profile network error: ", t);
            }
        });
    }
    private void loadPatientHealthMetrics(){
        String token = sessionManager.getToken();

        apiService.getHealthMetrics(token).enqueue(new Callback<PatientHealthMetrics>() {
            @Override
            public void onResponse(Call<PatientHealthMetrics> call, Response<PatientHealthMetrics> response) {
                if (response.isSuccessful() && response.body() != null) {
                    patientHealthMetrics = response.body();
                    displayHealthMetrics(patientHealthMetrics);
                } else if (response.code() == 401) {
                    Toast.makeText(MainActivity.this, "Unauthorized access", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "Failed to load health metrics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PatientHealthMetrics> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Trong phương thức onCreate() hoặc một phương thức riêng biệt để cập nhật UI
    private void displayHealthMetrics(PatientHealthMetrics patientHealthMetrics) {
        // Giả sử bạn đã có đối tượng PatientHealthMetrics
        if (patientHealthMetrics != null) {
            TextView heartRateValue = findViewById(R.id.heart_rate_value);
            TextView bloodPressureValue = findViewById(R.id.blood_pressure_value);
            TextView bmiValue = findViewById(R.id.bmi_value);
            TextView weightValue = findViewById(R.id.weight_value);
            TextView heightValue = findViewById(R.id.height_value);
            TextView bloodGlucoseValue = findViewById(R.id.blood_glucose_value);

            // Cập nhật giá trị nhịp tim
            if (patientHealthMetrics.getHeart_rate() != null && patientHealthMetrics.getHeart_rate() != 0) {
                heartRateValue.setText(String.valueOf(patientHealthMetrics.getHeart_rate()));
            } else {
                heartRateValue.setText("--");
            }

            // Cập nhật giá trị huyết áp
            if (patientHealthMetrics.getSystolic_bp() != null && patientHealthMetrics.getDiastolic_bp() != null &&
                    patientHealthMetrics.getSystolic_bp() != 0 && patientHealthMetrics.getDiastolic_bp() != 0) {
                String bp = patientHealthMetrics.getSystolic_bp() + "/" + patientHealthMetrics.getDiastolic_bp();
                bloodPressureValue.setText(bp);
            } else {
                bloodPressureValue.setText("--/--");
            }

            Double bmi = calculateBmi(patientHealthMetrics.getWeight_kg(), patientHealthMetrics.getHeight_cm());

            // Cập nhật giá trị BMI
            if (bmi != null && bmi > 0) {
                bmiValue.setText(String.format(Locale.US, "%.1f", patientHealthMetrics.getBmi()));
                // Thay đổi màu sắc của giá trị BMI dựa trên ngưỡng
                if (patientHealthMetrics.getBmi() < 18.5) {
                    bmiValue.setTextColor(Color.parseColor("#FF0000"));
                } else if (patientHealthMetrics.getBmi() >= 18.5 && patientHealthMetrics.getBmi() < 24.9) {
                    bmiValue.setTextColor(Color.parseColor("#00FF00"));
                } else if (patientHealthMetrics.getBmi() >= 25 && patientHealthMetrics.getBmi() < 29.9) {
                    bmiValue.setTextColor(Color.parseColor("#FFA500"));
                } else {
                    bmiValue.setTextColor(Color.parseColor("#FF0000"));
                }
            } else {
                bmiValue.setText("--");
            }

            // Cập nhật giá trị cân nặng
            if (patientHealthMetrics.getWeight_kg() != null && patientHealthMetrics.getWeight_kg() != 0.0) {
                weightValue.setText(String.format(Locale.US, "%.1f", patientHealthMetrics.getWeight_kg()));
            } else {
                weightValue.setText("--");
            }

            // Cập nhật giá trị chiều cao
            if (patientHealthMetrics.getHeight_cm() != null && patientHealthMetrics.getHeight_cm() != 0.0) {
                heightValue.setText(String.format(Locale.US, "%.1f", patientHealthMetrics.getHeight_cm()));
            } else {
                heightValue.setText("--");
            }

            // Cập nhật giá trị đường huyết
            if (patientHealthMetrics.getBlood_glucose() != null && patientHealthMetrics.getBlood_glucose() != 0.0) {
                bloodGlucoseValue.setText(String.format(Locale.US, "%.1f", patientHealthMetrics.getBlood_glucose()));
            } else {
                bloodGlucoseValue.setText("--");
            }

            // Cập nhật ngày ghi nhận
            TextView lastUpdatedText = findViewById(R.id.last_updated_text);
            if (patientHealthMetrics.getRecorded_at() != null && !patientHealthMetrics.getRecorded_at().isEmpty()) {
                // Định dạng ngày từ "yyyy-MM-dd'T'HH:mm:ss" thành "dd/MM/yyyy"
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    Date date = inputFormat.parse(patientHealthMetrics.getRecorded_at());
                    lastUpdatedText.setText("Cập nhật: " + outputFormat.format(date));
                } catch (Exception e) {
                    lastUpdatedText.setText("Cập nhật: " + patientHealthMetrics.getRecorded_at());
                }
            } else {
                lastUpdatedText.setText("Cập nhật: --/--/----");
            }

            // Thiết lập sự kiện nhấp chuột cho nút xem đầy đủ thông tin
            Button btnViewFullMetrics = findViewById(R.id.btn_view_full_metrics);
            btnViewFullMetrics.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, PatientHealthMetricsActivity.class);
                intent.putExtra("patient_user", patient_user);
                startActivity(intent);
            });
        }else {

        }
    }
    private Double calculateBmi(Double weightKg,  Double heightCm ) {
        // Kiểm tra giá trị hợp lệ
        if (weightKg > 0 && heightCm > 0) {
            // Chuyển chiều cao từ cm sang m
            Double heightM = heightCm / 100f;

            // Tính BMI = cân nặng (kg) / (chiều cao (m) * chiều cao (m))
            Double bmi = weightKg / (heightM * heightM);

            // Định dạng BMI với 2 chữ số thập phân, sử dụng Locale.US để đảm bảo dùng dấu chấm
            String formattedBmi = String.format(Locale.US, "%.2f", bmi);
            return bmi;
        } else {
            return 0.0d; // Trả về 0 nếu giá trị không hợp lệ
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Set the BottomNavigationView to the home item
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
}