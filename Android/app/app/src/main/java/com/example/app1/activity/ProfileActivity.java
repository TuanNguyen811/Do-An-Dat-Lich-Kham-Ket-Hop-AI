package com.example.app1.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.dialog.SelectShiftBottomSheet;
import com.example.app1.dialog.UpdateProfileBottomSheet;
import com.example.app1.utils.AvatarResponse;
import com.example.app1.utils.SessionManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.app1.models.Patient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity implements UpdateProfileBottomSheet.OnProfileUpdateListener {
    private ConstraintLayout button_profile_exit;

    private static final String TAG = "ProfileActivity";

    private TextView textViewName, textViewEmail, textViewPhone, textViewInsuranceId;
    private TextView textViewBirthdate, textViewGender, textViewAddress, textViewName1;
    private Button buttonLogout;
    private ConstraintLayout buttonUpdateProfile;
    private AuthService authService;
    private SessionManager sessionManager;

    private static final int FILE_PICKER_REQUEST_CODE = 100;
    private Uri selectedImageUri;
    private ImageView imageViewAvatar;
    private BottomNavigationView bottomNavigationView;
    private UpdateProfileBottomSheet bottomSheetDialogFragment;
    private Patient patient;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                getOnBackPressedDispatcher().onBackPressed();
                finish();
                overridePendingTransition(0, 0);
            } else if (itemId == R.id.nav_thongBao) {
                startActivity(new Intent(this, NotificationsActivity.class));
                finish();
                overridePendingTransition(0, 0);
            } else if (itemId == R.id.nav_lichKham) {
                startActivity(new Intent(this, AppointmentsActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
            return true;
        });
        // Initialize views
        buttonUpdateProfile = findViewById(R.id.button_update_profile);
        textViewName1 = findViewById(R.id.profile_textViewName1);
        button_profile_exit = findViewById(R.id.button_profile_exit);
        buttonLogout = findViewById(R.id.profile_buttonLogout);
        textViewName = findViewById(R.id.profile_textViewName);
        textViewEmail = findViewById(R.id.profile_textViewEmail);
        textViewPhone = findViewById(R.id.profile_textViewPhone);
        textViewBirthdate = findViewById(R.id.profile_textViewBirthdate);
        textViewGender = findViewById(R.id.profile_textViewGender);
        textViewAddress = findViewById(R.id.profile_textViewAddress);
        imageViewAvatar = findViewById(R.id.imageView_profile);
        textViewInsuranceId = findViewById(R.id.profile_textViewinsurance_id);
        // Set button click listeners
        button_profile_exit.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });

        buttonLogout.setOnClickListener(v -> {
            // Clear session and navigate to IntroActivity
            getSharedPreferences("app_prefs", MODE_PRIVATE).edit().putBoolean("intro_shown", false).apply();
            startActivity(new Intent(ProfileActivity.this, IntroActivity.class));
            finish();
            logoutUser();
        });

        authService = ApiClient.getAuthService(this);
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
        }else {
            loadUserProfile();
        }

        imageViewAvatar.setOnClickListener(v -> {
            // Check for storage permission
                chooseImage();
        });
        buttonUpdateProfile.setOnClickListener(v -> {
            bottomSheetDialogFragment = new UpdateProfileBottomSheet();
            Bundle bundle = new Bundle();
            bundle.putSerializable("patient", patient);
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "UpdateProfileBottomSheet");

        });

    }
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");  // Chỉ chọn ảnh
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imageViewAvatar.setImageURI(selectedImageUri);
                uploadSelectedAvatar();
            }
        }
    }

    private void uploadSelectedAvatar() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token rỗng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            navigateToLogin();
            return;
        }

        File file = new File(getRealPathFromURI(selectedImageUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        authService.uploadAvatar(body, "Bearer " + token).enqueue(new Callback<AvatarResponse>() {
            @Override
            public void onResponse(Call<AvatarResponse> call, Response<AvatarResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Tải ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                    downloadAvatar();
                } else {
                    Toast.makeText(ProfileActivity.this, "Lỗi tải ảnh: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AvatarResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getRealPathFromURI(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = new File(getCacheDir(), "temp_avatar.jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void downloadAvatar() {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token rỗng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            navigateToLogin();
            return;
        }

        authService.getAvatar("Bearer " + token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] bytes = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageViewAvatar.setImageBitmap(bitmap);
                        MainActivity.imageView_main_Avatar.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Không thể tải ảnh đại diện", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(ProfileActivity.this, "Không thể tải ảnh đại diện", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadUserProfile() {
        downloadAvatar();
        // Show progress
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token rỗng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            navigateToLogin();
            return;
        }

        // Call user profile API
        authService.getPatientProfile(token).enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(Call<Patient> call, Response<Patient> response) {

                if (response.isSuccessful() && response.body() != null) {
                    patient = response.body();
                    displayUserProfile(response.body());
                } else {
                    // Handle error
                    if (response.code() == 401) {
                        // Token expired or invalid
                        Toast.makeText(ProfileActivity.this,
                                "Session expired. Please login again.",
                                Toast.LENGTH_LONG).show();

                        sessionManager.clearSession();
                        navigateToLogin();
                    } else {
                        Toast.makeText(ProfileActivity.this,
                                "Failed to load profile. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }

                    Log.e(TAG, "Profile error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Patient> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "Profile network error: ", t);
            }
        });
    }

    private void displayUserProfile(Patient patient) {
        textViewName.setText(patient.getFull_name());
        textViewName1.setText(patient.getFull_name());
        textViewEmail.setText(patient.getEmail());
        textViewInsuranceId.setText(String.valueOf(patient.getInsurance_id()));
        // Handle optional fields
        textViewPhone.setText(patient.getPhone() != null ? patient.getPhone() : "Not provided");
        textViewBirthdate.setText(patient.getDate_of_birth() != null ? patient.getDate_of_birth() : "Not provided");
        textViewGender.setText(patient.getGender() != null ? capitalizeFirstLetter(patient.getGender()) : "Not provided");
        textViewAddress.setText(patient.getAddress() != null ? patient.getAddress() : "Not provided");
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private void logoutUser() {
        // Clear session
        sessionManager.clearSession();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to login
        //navigateToLogin();
    }
    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onProfileUpdated(Patient updatedPatient) {
        loadUserProfile();
    }
}