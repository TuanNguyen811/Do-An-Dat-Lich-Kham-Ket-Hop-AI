package com.example.app1.activity;

import static android.app.ProgressDialog.show;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.adapter.AppointmentAdapter;
import com.example.app1.adapter.DoctorAdapter;
import com.example.app1.dialog.AppointmentDetailBottomSheet;
import com.example.app1.models.Appointment;
import com.example.app1.models.Doctor;
import com.example.app1.utils.DateUtils;
import com.example.app1.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.recyclerview.widget.LinearLayoutManager;

public class AppointmentsActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;
    private AuthService apiService;
    private AppointmentAdapter appointmentAdapter;
    private ArrayList<Appointment> appointmentList;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //khai báo API, token
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getAuthService(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_lichKham);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                getOnBackPressedDispatcher().onBackPressed();
                finish();
                overridePendingTransition(0, 0);
            }else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                overridePendingTransition(0, 0);
            } else if (itemId == R.id.nav_thongBao) {
                startActivity(new Intent(this, NotificationsActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
            return true;
        });
        //khai báo recyclerView
        appointmentList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView_appointments999);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Add this line
        loadAppointments("Scheduled");


        tabLayout = findViewById(R.id.tabLayout_appointments);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                appointmentList.clear();
                String status = tab.getText().toString();
                String capitalizedText = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                loadAppointments(capitalizedText);
                //
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselected event if needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselected event if needed
            }
        });

    }

    private void loadAppointments(String status) {
        // Load appointments from the API
        String token = sessionManager.getToken();
        appointmentList = new ArrayList<>();
        apiService.getAppointmentsByPatient(token, status).enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful()) {
                    // Clear the existing list and add the new appointments
                    appointmentList.clear();
                    appointmentList = new ArrayList<>(response.body());
                    if (appointmentList.get(0).getStatus().equals("Cancelled") || appointmentList.get(0).getStatus().equals("Completed")){
                        appointmentList.sort((a, b) -> {
                            String timeA = a.getShiftName();
                            String timeB = b.getShiftName();
                            return timeA.compareTo(timeB);
                        });
                        appointmentList.sort((a, b) -> {
                            String dateA = a.getAppointmentDate();
                            String dateB = b.getAppointmentDate();
                            return dateB.compareTo(dateA) ;
                        });
                    }else {
                        appointmentList.sort((a, b) -> {
                            String timeA = a.getShiftName();
                            String timeB = b.getShiftName();
                            return timeA.compareTo(timeB);
                        });
                        appointmentList.sort((a, b) -> {
                            String dateA = a.getAppointmentDate();
                            String dateB = b.getAppointmentDate();
                            return dateA.compareTo(dateB) ;
                        });
                    }
                   appointmentAdapter = new AppointmentAdapter(AppointmentsActivity.this, appointmentList, new AppointmentAdapter.OnAppointmentClickListener() {
                        @Override
                        public void onAppointmentClick(Appointment appointment) {
                            showAppointmentDetails(appointment);
                        }

                       @Override
                       public void onCancelAppointment(Appointment appointment) {
                           updateAppointments(appointment, "");
                       }
                   });
                    recyclerView.setAdapter(appointmentAdapter);
                } else {
                    appointmentList.clear();
                    recyclerView.setAdapter(null);
                   // appointmentAdapter.notifyDataSetChanged();

                    Toast.makeText(AppointmentsActivity.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) { 
                Toast.makeText(AppointmentsActivity.this, "E: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAppointmentDetails(Appointment appointment) {
        AppointmentDetailBottomSheet bottomSheet = AppointmentDetailBottomSheet.newInstance(appointment);
        bottomSheet.setOnAppointmentActionListener(new AppointmentDetailBottomSheet.OnAppointmentActionListener() {
            @Override
            public void onBtn1(Appointment appointment) {
                // Handle cancel appointment action
                if (appointment.getStatus().equals("Scheduled")){
                    updateAppointments(appointment, "Cancelled");
                }else if(appointment.getStatus().equals("Completed")){

                }else if(appointment.getStatus().equals("Cancelled")){
                    deteleAppointment(appointment);
                }
            }

            @Override
            public void onBtn2(Appointment appointment) {
                Intent intent = new Intent(AppointmentsActivity.this, AppointmentActivity.class);
                startActivity(intent);
            }
        });
        bottomSheet.show(getSupportFragmentManager(), "AppointmentDetailBottomSheet");
    }

    public void deteleAppointment(Appointment appointment) {
        String token = sessionManager.getToken();
        apiService.deleteAppointment(token, appointment.getAppointment_id()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    // Appointment deleted successfully
                    Toast.makeText(AppointmentsActivity.this, "Appointment deleted", Toast.LENGTH_SHORT).show();
                    appointmentList.remove(appointment);
                    appointmentAdapter.notifyDataSetChanged();

                } else {
                    // Handle error
                    Toast.makeText(AppointmentsActivity.this, "Failed to delete appointment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(AppointmentsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void updateAppointments(Appointment appointment, String status) {
        if (status.equals("")){
            if (appointment.getStatus().equals("Cancelled")){
                status = "Scheduled";
            }else if (appointment.getStatus().equals("Scheduled")){
                status = "Cancelled";
            }
        }
        String token = sessionManager.getToken();

        apiService.updateAppointmentStatus(token, appointment.getAppointment_id(), status).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    // Appointment status updated successfully
                    Toast.makeText(AppointmentsActivity.this, "Appointment status updated", Toast.LENGTH_SHORT).show();
                    appointmentList.remove(appointment);
                    appointmentAdapter.notifyDataSetChanged();

                } else {
                    // Handle error
                    Toast.makeText(AppointmentsActivity.this, "Failed to update appointment status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(AppointmentsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}