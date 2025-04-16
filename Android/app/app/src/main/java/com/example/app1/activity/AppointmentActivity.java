package com.example.app1.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.dialog.SelectDepartmentBottomSheet;
import com.example.app1.dialog.SelectDoctorBottomSheet;
import com.example.app1.dialog.SelectShiftBottomSheet;
import com.example.app1.models.Appointment;
import com.example.app1.models.Department;
import com.example.app1.models.Doctor;
import com.example.app1.utils.DateUtils;
import com.example.app1.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class AppointmentActivity extends AppCompatActivity implements SelectDepartmentBottomSheet.DepartmentSelectionListener
        , SelectDoctorBottomSheet.DoctorSelectionListener, SelectShiftBottomSheet.ShiftSelectionListener {
    private ArrayList<Department> departmentList;
    private Department department;
    private Doctor doctor;
    private String appointmentDate, appointmentDayOfWeek;
    private int shift;
    private ArrayList<Doctor> doctorList;

    private AuthService apiService;
    private SessionManager sessionManager;


    private TextView textView_appointment_department_title, appointment_department, appointment_department_decription, appointment_doctor,
            appointment_doctor_decription, appointment_date, appointment_shift;
    private BottomSheetDialogFragment bottomSheetDialogFragment;
    private Button appointment;
    private LinearLayout layout_appointment_doctor, layout_appointment_department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getAuthService(this);
        // Khởi tạo các view
        textView_appointment_department_title = findViewById(R.id.textView_appointment_department_title);
        appointment_department = findViewById(R.id.textView_appointment_department);
        appointment_department_decription = findViewById(R.id.textView_appointment_department_description);
        appointment_doctor_decription = findViewById(R.id.textView_appointment_doctor_description);
        appointment_doctor = findViewById(R.id.textView_appointment_doctor);
        appointment_shift = findViewById(R.id.textView_appointment_shift);
        layout_appointment_doctor = findViewById(R.id.layout_appointment_doctor);
        layout_appointment_department = findViewById(R.id.layout_appointment_department);
        appointment = findViewById(R.id.button_appointment);

        appointment_date = findViewById(R.id.textView_appointment_date);
        // Nhận dữ liệu

        departmentList = (ArrayList<Department>) getIntent().getSerializableExtra("departments");
        if (departmentList == null) departmentList = new ArrayList<>();

        department = (Department) getIntent().getSerializableExtra("item_department");
        if (department == null) department = new Department();
        // Set data to views

        appointment_department.setText(department.getName());
        appointment_department_decription.setText(department.getDescription());

        layout_appointment_department.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialogFragment = new SelectDepartmentBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putSerializable("departments", departmentList);
                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "SelectDepartmentBottomSheet");
            }
        });

        layout_appointment_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (department == null) {
                    textView_appointment_department_title.setError("Vui lòng chọn khoa trước");
                    return;
                } else {
                    loadListDoctors(department.getId());
                }
            }
        });
        setupDatePicker();

        appointment_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (doctor == null) {
                    appointment_shift.setError("Vui lòng chọn bác sĩ trước");
                    return;
                } else {
                    bottomSheetDialogFragment = new SelectShiftBottomSheet();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("doctor", doctor);
                    bundle.putString("appointment_date", appointmentDate);
                    bundle.putString("appointment_day_of_week", appointmentDayOfWeek);
                    bottomSheetDialogFragment.setArguments(bundle);
                    bottomSheetDialogFragment.show(getSupportFragmentManager(), "SelectShiftBottomSheet");
                }
            }
        });
        appointment.setEnabled(false);
        appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAppointment();
            }
        });
    }

    private void createAppointment() {
        String token = sessionManager.getToken();
        checkButton();
        Appointment appointment = new Appointment();
        appointment.setDoctor_id(doctor.getUser_id());
        appointment.setDepartment_id(department.getId());
        appointment.setAppointmentDate(this.appointmentDate);
        appointment.setShiftName(String.valueOf(shift));

        apiService.createAppointment(token, appointment).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AppointmentActivity.this, "Đặt lịch khám thành công"  + response.body(), Toast.LENGTH_SHORT).show();
                    // Chuyển hướng về trang Appointments
                    Intent intent = new Intent(AppointmentActivity.this, AppointmentsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
//                    try {
//                        //Toast.makeText(AppointmentActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
                    // Chuyển hướng về trang Appointments
                    Intent intent = new Intent(AppointmentActivity.this, AppointmentsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(AppointmentActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void loadListDoctors(int department_id) {
        doctorList = new ArrayList<>();
        String token = sessionManager.getToken();
        apiService.getListDoctors(token, department_id).enqueue(new Callback<List<Doctor>>() {
            @Override
            public void onResponse(@NonNull Call<List<Doctor>> call, @NonNull Response<List<Doctor>> response) {
                if (response.isSuccessful()) {
                    doctorList = new ArrayList<>(response.body());
                    if (doctorList == null) {
                        doctorList = new ArrayList<>();
                    }
                    if (doctorList.size() == 0) {
                        Toast.makeText(AppointmentActivity.this, "Không có bác sĩ nào trong khoa này", Toast.LENGTH_SHORT).show();
                        return;
                    } else{
                        bottomSheetDialogFragment = new SelectDoctorBottomSheet();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("doctors", doctorList);
                        bottomSheetDialogFragment.setArguments(bundle);
                        bottomSheetDialogFragment.show(getSupportFragmentManager(), "SelectDoctorBottomSheet");
                    }

                } else {
                    try {
                        Toast.makeText(AppointmentActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Doctor>> call, @NonNull Throwable t) {
                Toast.makeText(AppointmentActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDepartment(Department department) {
        this.department = department;
        if (department == null) {
            layout_appointment_department.setBackgroundResource(R.drawable.exittext_button_background);
            appointment_department.setText("Chọn khoa");
            appointment_department_decription.setText("");
            return;
        }
        layout_appointment_department.setBackgroundResource(R.drawable.exittext_button_background2);
        appointment_department.setText(department.getName());
        appointment_department_decription.setText(department.getDescription());
    }
    private void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        if (doctor == null) {
            appointment_doctor.setText("Chọn bác sĩ");
            appointment_doctor_decription.setText("");
            layout_appointment_doctor.setBackgroundResource(R.drawable.exittext_button_background);
            return;
        }
        layout_appointment_doctor.setBackgroundResource(R.drawable.exittext_button_background2);
        appointment_doctor.setText(doctor.getFull_name());
        appointment_doctor_decription.setText(doctor.getDescription());
    }

    private void setupDatePicker() {
        appointment_date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            setShift(0, null);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AppointmentActivity.this,
                    R.style.CustomDatePickerDialog,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Tạo đối tượng Calendar từ ngày được chọn
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);

                        // Format để lấy thứ bằng tiếng Việt
                        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
                        String dayOfWeek = dayOfWeekFormat.format(selectedDate.getTime()); // Ví dụ: "Thứ hai"

                        // Format ngày theo định dạng YYYY-MM-DD
                        String formattedDate = String.format("%d-%02d-%02d",
                                selectedYear, selectedMonth + 1, selectedDay);

                        // Ví dụ hiển thị thứ + ngày
                        String displayText = dayOfWeek + ", " + formattedDate;

                        setAppointmentDate(formattedDate, dayOfWeek); // Gọi hàm để lưu ngày
                        //Toast.makeText(this, "Ngày được chọn: " + displayText, Toast.LENGTH_SHORT).show();

                        checkButton();
                    },
                    year, month, day
            );
            // Khởi tạo DatePickerDialog với ngày hiện tại
            // Đặt ngày bắt đầu từ ngày hiện tại
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()); // setMinDate() cho phép chọn từ ngày hiện tại trở đi
            datePickerDialog.show(); // Hiển thị DatePickerDialog
        });
    }


    private void setAppointmentDate(String date, String dayOfWeek) {
        this.appointmentDayOfWeek = dayOfWeek;
        this.appointmentDate = date;

        if (date == null) {
            appointment_date.setBackgroundResource(R.drawable.exittext_button_background);
            appointment_date.setText("Chọn ngày khám");
            return;
        }
        appointment_date.setText(dayOfWeek + ", " + DateUtils.formatDate(date));
        appointment_date.setBackgroundResource(R.drawable.exittext_button_background2);
    }
    private void setShift(int shift, String queue){
        this.shift = shift;
        if (shift == 0) {
            appointment_shift.setText("Chọn giờ khám");
            appointment_shift.setBackgroundResource(R.drawable.exittext_button_background);
            return;
        }
        appointment_shift.setBackgroundResource(R.drawable.exittext_button_background2);
        if (shift == 1) {
            appointment_shift.setText("7h00-9h00 - Vị trí: " + queue + " (dự kiến)");
        } else if (shift == 2) {
            appointment_shift.setText("9h00-11h00 - Vị trí:" + queue + " (dự kiến)");
        } else if (shift == 3) {
            appointment_shift.setText("13h00-15h00 - Vị trí: " + queue + " (dự kiến)");
        } else if (shift == 4) {
            appointment_shift.setText("15h00-17h00 - Vị trí: " + queue + " (dự kiến)");
        }
    }

    private void checkButton(){
        if (doctor == null) {
            appointment.setEnabled(false);
            return;
        }
        if (department == null) {
            appointment.setEnabled(false);
            return;
        }
        if (appointment_date.getText().toString().equals("Chọn ngày khám")) {
            appointment.setEnabled(false);
            return;
        }
        if (shift == 0) {
            appointment.setEnabled(false);
            return;
        }

        appointment.setEnabled(true);
    }


    @Override
    public void onDepartmentSelected(Department department) {
       setDepartment(department);
       setDoctor(null);
       //setAppointmentDate(null);
        checkButton();
       setShift(0, null);

    }

    @Override
    public void onDoctorSelected(Doctor doctor) {
        setDoctor(doctor);
        //setAppointmentDate(null);
        setShift(0, null);
        checkButton();
    }

    @Override
    public void setListener(int shift, String queue) {
        setShift(shift, queue);
        checkButton();
    }
}
