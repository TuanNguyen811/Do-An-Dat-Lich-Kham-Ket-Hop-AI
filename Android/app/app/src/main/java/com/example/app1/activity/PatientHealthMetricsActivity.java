package com.example.app1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.dialog.ResearchHealthDetailBottomSheet;
import com.example.app1.models.Patient;
import com.example.app1.models.PatientHealthMetrics;
import com.example.app1.utils.DateUtils;
import com.example.app1.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientHealthMetricsActivity extends AppCompatActivity implements ResearchHealthDetailBottomSheet.OnButtonClickListener {

    private EditText editSystolicBp, editDiastolicBp, editHeartRate, editBodyTemperature;
    private EditText editRespiratoryRate, editWeightKg, editHeightCm, editBmi;
    private EditText editBloodGlucose, editCholesterolTotal, editLdl, editHdl;
    private EditText editTriglycerides, editHemoglobin, editOtherMetrics;

    private TextView textInfo;
    private Button btnUpdate, btnResearch;
    private Patient patient_user;
    private ImageView imageViewBack;

    private SessionManager sessionManager;
    private AuthService apiService;
    private PatientHealthMetrics patientHealthMetrics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_health_metrics);

        // Initialize views
        initViews();

        patient_user = (Patient) getIntent().getSerializableExtra("patient_user");
        if (patient_user != null) {
            // Set the patient name in the title bar
            setTitle("Thông tin sức khỏe của " + patient_user.getFull_name());
            textInfo.setText("Thông tin sức khỏe của " + patient_user.getFull_name() +
                    "\nGiới tính: " + patient_user.getGender() + "\nNgày sinh: " +
                    DateUtils.formatDate2(patient_user.getDate_of_birth()));

        } else {
            setTitle("Thông tin sức khỏe");
        }

        // Initialize ViewModel
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getAuthService(this);
        loadPatientHealthMetrics();

        btnUpdate.setOnClickListener(v -> updatePatientHealthMetrics());
        btnResearch.setOnClickListener(v ->
                researchHealth());

        setupBmiCalculation(editWeightKg, editHeightCm, editBmi);
        imageViewBack = findViewById(R.id.imageView_back);
        imageViewBack.setOnClickListener(v -> {
            Intent intent = new Intent(PatientHealthMetricsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadPatientHealthMetrics(){
        String token = sessionManager.getToken();

        apiService.getHealthMetrics(token).enqueue(new Callback<PatientHealthMetrics>() {
            @Override
            public void onResponse(Call<PatientHealthMetrics> call, Response<PatientHealthMetrics> response) {
                if (response.isSuccessful() && response.body() != null) {
                    patientHealthMetrics = response.body();
                    setPatientHealthMetrics(patientHealthMetrics);
                } else if (response.code() == 401) {
                    Toast.makeText(PatientHealthMetricsActivity.this, "Unauthorized access", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(PatientHealthMetricsActivity.this, "Failed to load health metrics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PatientHealthMetrics> call, Throwable t) {

            }
        });
    }

    private void researchHealth() {
        updatePatientHealthMetrics();
        ResearchHealthDetailBottomSheet researchHealthDetailBottomSheet = new ResearchHealthDetailBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("patient", patient_user);
        args.putSerializable("patient_health_metrics", getPatientHealthMetrics());
        researchHealthDetailBottomSheet.setArguments(args);
        researchHealthDetailBottomSheet.show(getSupportFragmentManager(), "ResearchHealthDetailBottomSheet");
    }
    private void updatePatientHealthMetrics() {
        String token = sessionManager.getToken();

        PatientHealthMetrics metrics = getPatientHealthMetrics();
        apiService.createHealthMetric(token, metrics).enqueue(new Callback<PatientHealthMetrics>() {
            @Override
            public void onResponse(Call<PatientHealthMetrics> call, Response<PatientHealthMetrics> response) {
                if (response.isSuccessful()) {
                    patientHealthMetrics = response.body();
                    Toast.makeText(PatientHealthMetricsActivity.this, "Health metrics updated successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                    setPatientHealthMetrics(patientHealthMetrics);
                } else {
                    Toast.makeText(PatientHealthMetricsActivity.this, "Failed to update health metrics", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<PatientHealthMetrics> call, Throwable t) {
                Toast.makeText(PatientHealthMetricsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initViews() {
        textInfo = findViewById(R.id.textView_health__thongTin);
        editSystolicBp = findViewById(R.id.editSystolicBp);
        editDiastolicBp = findViewById(R.id.editDiastolicBp);
        editHeartRate = findViewById(R.id.editHeartRate);
        editBodyTemperature = findViewById(R.id.editBodyTemperature);
        editRespiratoryRate = findViewById(R.id.editRespiratoryRate);
        editWeightKg = findViewById(R.id.editWeightKg);
        editHeightCm = findViewById(R.id.editHeightCm);
        editBmi = findViewById(R.id.editBmi3);
        editBloodGlucose = findViewById(R.id.editBloodGlucose);
        editCholesterolTotal = findViewById(R.id.editCholesterolTotal);
        editLdl = findViewById(R.id.editLdl);
        editHdl = findViewById(R.id.editHdl);
        editTriglycerides = findViewById(R.id.editTriglycerides);
        editHemoglobin = findViewById(R.id.editHemoglobin);
        editOtherMetrics = findViewById(R.id.editOtherMetrics);

        btnResearch = findViewById(R.id.btn_health_research);
        btnUpdate = findViewById(R.id.btn_health_Update);
    }



    @NonNull
    private PatientHealthMetrics getPatientHealthMetrics() {
        PatientHealthMetrics metrics = new PatientHealthMetrics();

        // Get values from EditTexts - only add non-empty values
        String systolicStr = editSystolicBp.getText().toString().trim();
        if (!TextUtils.isEmpty(systolicStr)) {
            metrics.setSystolic_bp(Integer.parseInt(systolicStr));
        }

        String diastolicStr = editDiastolicBp.getText().toString().trim();
        if (!TextUtils.isEmpty(diastolicStr)) {
            metrics.setDiastolic_bp(Integer.parseInt(diastolicStr));
        }

        String heartRateStr = editHeartRate.getText().toString().trim();
        if (!TextUtils.isEmpty(heartRateStr)) {
            metrics.setHeart_rate(Integer.parseInt(heartRateStr));
        }

        String temperatureStr = editBodyTemperature.getText().toString().trim();
        if (!TextUtils.isEmpty(temperatureStr)) {
            metrics.setBody_temperature(Double.parseDouble(temperatureStr));
        }

        String respRateStr = editRespiratoryRate.getText().toString().trim();
        if (!TextUtils.isEmpty(respRateStr)) {
            metrics.setRespiratory_rate(Integer.parseInt(respRateStr));
        }

        String weightStr = editWeightKg.getText().toString().trim();
        if (!TextUtils.isEmpty(weightStr)) {
            metrics.setWeight_kg(Double.parseDouble(weightStr));
        }

        String heightStr = editHeightCm.getText().toString().trim();
        if (!TextUtils.isEmpty(heightStr)) {
            metrics.setHeight_cm(Double.parseDouble(heightStr));
        }

        try {
            String bmiStr = editBmi.getText().toString().trim();
            if (!TextUtils.isEmpty(bmiStr)) {
                metrics.setBmi(Double.parseDouble(bmiStr));
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid BMI value", Toast.LENGTH_SHORT).show();
        }

        String glucoseStr = editBloodGlucose.getText().toString().trim();
        if (!TextUtils.isEmpty(glucoseStr)) {
            metrics.setBlood_glucose(Double.parseDouble(glucoseStr));
        }

        String cholesterolStr = editCholesterolTotal.getText().toString().trim();
        if (!TextUtils.isEmpty(cholesterolStr)) {
            metrics.setCholesterol_total(Double.parseDouble(cholesterolStr));
        }

        String ldlStr = editLdl.getText().toString().trim();
        if (!TextUtils.isEmpty(ldlStr)) {
            metrics.setLdl(Double.parseDouble(ldlStr));
        }

        String hdlStr = editHdl.getText().toString().trim();
        if (!TextUtils.isEmpty(hdlStr)) {
            metrics.setHdl(Double.parseDouble(hdlStr));
        }

        String triglyceridesStr = editTriglycerides.getText().toString().trim();
        if (!TextUtils.isEmpty(triglyceridesStr)) {
            metrics.setTriglycerides(Double.parseDouble(triglyceridesStr));
        }

        String hemoglobinStr = editHemoglobin.getText().toString().trim();
        if (!TextUtils.isEmpty(hemoglobinStr)) {
            metrics.setHemoglobin(Double.parseDouble(hemoglobinStr));
        }

        String otherMetricsStr = editOtherMetrics.getText().toString().trim();
        if (!TextUtils.isEmpty(otherMetricsStr)) {
            metrics.setOther_metrics(otherMetricsStr);
        }
        // Set current timestamp for recorded_at
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        metrics.setRecorded_at(sdf.format(new Date()));

        return metrics;
    }

    private void setPatientHealthMetrics(PatientHealthMetrics metrics) {
        if (metrics.getSystolic_bp() != null) {
            editSystolicBp.setText(String.valueOf(metrics.getSystolic_bp()));
        }

        if (metrics.getDiastolic_bp() != null) {
            editDiastolicBp.setText(String.valueOf(metrics.getDiastolic_bp()));
        }

        if (metrics.getHeart_rate() != null) {
            editHeartRate.setText(String.valueOf(metrics.getHeart_rate()));
        }

        if (metrics.getBody_temperature() != null) {
            editBodyTemperature.setText(String.valueOf(metrics.getBody_temperature()));
        }

        if (metrics.getRespiratory_rate() != null) {
            editRespiratoryRate.setText(String.valueOf(metrics.getRespiratory_rate()));
        }

        if (metrics.getWeight_kg() != null) {
            editWeightKg.setText(String.valueOf(metrics.getWeight_kg()));
        }

        if (metrics.getHeight_cm() != null) {
            editHeightCm.setText(String.valueOf(metrics.getHeight_cm()));
        }

        if (metrics.getBmi() != null) {
            editBmi.setText(String.valueOf(metrics.getBmi()));
        }

        if (metrics.getBlood_glucose() != null) {
            editBloodGlucose.setText(String.valueOf(metrics.getBlood_glucose()));
        }

        if (metrics.getCholesterol_total() != null) {
            editCholesterolTotal.setText(String.valueOf(metrics.getCholesterol_total()));
        }

        if (metrics.getLdl() != null) {
            editLdl.setText(String.valueOf(metrics.getLdl()));
        }

        if (metrics.getHdl() != null) {
            editHdl.setText(String.valueOf(metrics.getHdl()));
        }

        if (metrics.getTriglycerides() != null) {
            editTriglycerides.setText(String.valueOf(metrics.getTriglycerides()));
        }

        if (metrics.getHemoglobin() != null) {
            editHemoglobin.setText(String.valueOf(metrics.getHemoglobin()));
        }
        if (metrics.getOther_metrics() != null) {
            editOtherMetrics.setText(metrics.getOther_metrics());
        }
    }

    private void clearForm() {
        editSystolicBp.setText("");
        editDiastolicBp.setText("");
        editHeartRate.setText("");
        editBodyTemperature.setText("");
        editRespiratoryRate.setText("");
        editWeightKg.setText("");
        editHeightCm.setText("");
        editBmi.setText("");
        editBloodGlucose.setText("");
        editCholesterolTotal.setText("");
        editLdl.setText("");
        editHdl.setText("");
        editTriglycerides.setText("");
        editHemoglobin.setText("");
        editOtherMetrics.setText("");
    }

    private void showLoading(boolean isLoading) {

    }
    private void setupBmiCalculation(EditText weightEditText, EditText heightEditText, EditText bmiEditText) {
        // Tạo TextWatcher để theo dõi thay đổi
        TextWatcher bmiTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý trước khi thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Không cần xử lý trong quá trình thay đổi
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateBmi(weightEditText, heightEditText, bmiEditText);
            }
        };

        // Thêm TextWatcher vào cả hai EditText
        weightEditText.addTextChangedListener(bmiTextWatcher);
        heightEditText.addTextChangedListener(bmiTextWatcher);
    }

    private void calculateBmi(EditText weightEditText, EditText heightEditText, EditText bmiEditText) {
        try {
            // Lấy giá trị cân nặng và chiều cao từ EditText
            String weightStr = weightEditText.getText().toString();
            String heightStr = heightEditText.getText().toString();

            // Kiểm tra nếu các trường không trống
            if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
                float weightKg = Float.parseFloat(weightStr);
                float heightCm = Float.parseFloat(heightStr);

                // Kiểm tra giá trị hợp lệ
                if (weightKg > 0 && heightCm > 0) {
                    // Chuyển chiều cao từ cm sang m
                    float heightM = heightCm / 100f;

                    // Tính BMI = cân nặng (kg) / (chiều cao (m) * chiều cao (m))
                    float bmi = weightKg / (heightM * heightM);

                    // Định dạng BMI với 2 chữ số thập phân, sử dụng Locale.US để đảm bảo dùng dấu chấm
                    String formattedBmi = String.format(Locale.US, "%.2f", bmi);

                    // Cập nhật vào trường BMI
                    bmiEditText.setText(formattedBmi);
                }
            }
        } catch (NumberFormatException e) {
            // Xử lý nếu có lỗi khi chuyển đổi
            bmiEditText.setText("");
        }
    }

    @Override
    public void onAdviceButtonClick() {
        Intent intent = new Intent(PatientHealthMetricsActivity.this, ChatbotActivity.class);
        intent.putExtra("messages", "Đưa ra lời khuyên cho chuẩn đoán trên");
        startActivity(intent);
    }

    @Override
    public void onResearchAgainButtonClick() {

    }
}