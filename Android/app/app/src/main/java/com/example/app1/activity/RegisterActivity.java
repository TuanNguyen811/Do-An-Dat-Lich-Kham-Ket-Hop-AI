package com.example.app1.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.models.*;

import com.example.app1.utils.SessionManager;
import com.example.app1.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private TextInputEditText editTextName, editTextEmail, editTextPassword;
    private TextInputEditText editTextPhone, editTextBirthdate, editTextAddress;
    private RadioGroup radioGroupGender;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private TextView textViewLogin;
    private AuthService authService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        editTextName = findViewById(R.id.register_editTextName);
        editTextEmail = findViewById(R.id.register_editTextEmail);
        editTextPassword = findViewById(R.id.register_editTextPassword);
        editTextPhone = findViewById(R.id.register_editTextPhone);
        editTextBirthdate = findViewById(R.id.register_editTextBirthdate);
        editTextAddress = findViewById(R.id.register_editTextAddress);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        buttonRegister = findViewById(R.id.register_buttonRegister);
        progressBar = findViewById(R.id.register_progressBar);
        textViewLogin = findViewById(R.id.register_textViewLogin);

        // Initialize API client
        authService = ApiClient.getAuthService(this);

        // Set date picker for birthdate field
        setupDatePicker();

        // Set button click listener
        buttonRegister.setOnClickListener(v -> registerUser());

        // Set login text click listener
        textViewLogin.setOnClickListener(v -> {
            finish(); // Go back to login activity
        });
    }

    private void setupDatePicker() {
        editTextBirthdate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RegisterActivity.this,
                    R.style.CustomDatePickerDialog,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format date as YYYY-MM-DD
                        String formattedDate = String.format("%d-%02d-%02d",
                                selectedYear, selectedMonth + 1, selectedDay);
                        editTextBirthdate.setText(formattedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void registerUser() {
        // Get input values
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String birthdate = editTextBirthdate.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        // Get selected gender
        String gender = "other";
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.radioButtonMale) {
            gender = "male";
        } else if (selectedGenderId == R.id.radioButtonFemale) {
            gender = "female";
        }

        // Validate inputs
        if (!ValidationUtils.isValidName(name)) {
            editTextName.setError("Please enter your name");
            editTextName.requestFocus();
            return;
        }

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

        if (phone.length() > 0 && !ValidationUtils.isValidPhone(phone)) {
            editTextPhone.setError("Please enter a valid phone number");
            editTextPhone.requestFocus();
            return;
        }

        // Show progress
        setLoading(true);

        // Create registration request
        Patient patient = new Patient(0, email, name, phone, birthdate, gender, address,"", password, 0, 0);

        authService.registerPatient(patient).enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(Call<Patient> call, Response<Patient> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                    finish(); // Go back to login screen
                } else {
                    // Handle error
                    String errorMessage = "Registration failed.";
                    if (response.code() == 400) {
                        errorMessage = "Email or phone number already registered";
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                    Log.e(TAG, "Registration error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Patient> call, Throwable t) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Registration network error: ", t);
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            buttonRegister.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            buttonRegister.setEnabled(true);
        }
    }
}