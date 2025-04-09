package com.example.app1.dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.activity.RegisterActivity;
import com.example.app1.models.Appointment;
import com.example.app1.models.Patient;
import com.example.app1.utils.SessionManager;
import com.example.app1.utils.ValidationUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileBottomSheet extends BottomSheetDialogFragment {
    private TextInputEditText editTextName, editTexTinsurance_id, editTextPassword;
    private TextInputEditText editTextPhone,editTextEmail , editTextBirthdate, editTextAddress;
    private RadioGroup radioGroupGender;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private AuthService authService;
    private SessionManager sessionManager;
   private Patient patient;
   private OnProfileUpdateListener listener;
   public interface OnProfileUpdateListener {
       void onProfileUpdated(Patient updatedPatient);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UpdateProfileBottomSheet.OnProfileUpdateListener) {
            listener = (UpdateProfileBottomSheet.OnProfileUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DoctorSelectionListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        // Initialize your views here
        sessionManager = new SessionManager(getContext());
        authService = ApiClient.getAuthService(getContext());
        editTextEmail = view.findViewById(R.id.register_editTextEmail);
        editTextName = view.findViewById(R.id.register_editTextName);
        editTexTinsurance_id = view.findViewById(R.id.register_editTextinsurance_id);
        editTextPassword =  view.findViewById(R.id.register_editTextPassword);
        editTextPhone =  view.findViewById(R.id.register_editTextPhone);
        editTextBirthdate =  view.findViewById(R.id.register_editTextBirthdate);
        editTextAddress =  view.findViewById(R.id.register_editTextAddress);
        radioGroupGender =  view.findViewById(R.id.radioGroupGender);
        buttonRegister =  view.findViewById(R.id.register_buttonRegister);
        progressBar =  view.findViewById(R.id.register_progressBar);
        authService = ApiClient.getAuthService(getContext());
        setupDatePicker();
        buttonRegister.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dark_blue));

        patient =(Patient) getArguments().getSerializable("patient");
        if (patient == null) {
            Log.e("UpdateProfile", "Patient data is null");
        } else {
            Log.d("UpdateProfile", "Patient data: " + patient.toString());
        }
        if (patient != null){
            editTextName.setText(patient.getFull_name());
            editTextEmail.setText(patient.getEmail());
            //editTextPassword.setText(patient.getPassword());
            editTextPhone.setText(patient.getPhone());
            editTextBirthdate.setText(patient.getDate_of_birth());
            editTexTinsurance_id.setText(String.valueOf(patient.getInsurance_id()));
            if (patient.getGender().equals("Male")){
                radioGroupGender.check(R.id.radioButtonMale);
            }else if (patient.getGender().equals("Female")){
                radioGroupGender.check(R.id.radioButtonFemale);
            }
            editTextAddress.setText(patient.getAddress());
        }
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        return view;
    }
    private void setupDatePicker() {
        editTextBirthdate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(),
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
    private void updateProfile() {
        // Get input values
        String name = editTextName.getText().toString().trim();
        //String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String birthdate = editTextBirthdate.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        int insurance_id = Integer.parseInt(editTexTinsurance_id.getText().toString().trim());

        // Get selected gender
        String gender = "other";
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.radioButtonMale) {
            gender = "male";
        } else if (selectedGenderId == R.id.radioButtonFemale) {
            gender = "female";
        }

        if (phone.length() > 0 && !ValidationUtils.isValidPhone(phone)) {
            editTextPhone.setError("Please enter a valid phone number");
            editTextPhone.requestFocus();
            return;
        }
        // Show progress
        setLoading(true);
        // Create registration request
        Patient patient = new Patient(0, null, name, phone, birthdate, gender, address,"", password, insurance_id, 0);
        String token = sessionManager.getToken();
        authService.updatePatientProfile(token, patient).enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(Call<Patient> call, Response<Patient> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Patient updatedPatient = response.body();
                    if (listener != null) {
                        listener.onProfileUpdated(updatedPatient);
                    }
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else if (response.code() == 401) {
                    Toast.makeText(getContext(), "Unauthorized", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Patient> call, Throwable t) {
                setLoading(false);
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
    @Override
    public int getTheme() {
        return R.style.BottomSheetTheme;
    }
}
