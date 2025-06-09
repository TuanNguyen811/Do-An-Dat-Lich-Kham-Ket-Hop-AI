package com.example.app1.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.app1.API.ApiClient;
import com.example.app1.R;
import com.example.app1.models.Appointment;
import com.example.app1.utils.DateUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AppointmentDetailBottomSheet extends BottomSheetDialogFragment {

    private Appointment appointment;
    private OnAppointmentActionListener listener;

    // Interface for callback actions
    public interface OnAppointmentActionListener {
        void onBtn1(Appointment appointment);
        void onBtn2(Appointment appointment);
    }

    public static AppointmentDetailBottomSheet newInstance(Appointment appointment) {
        AppointmentDetailBottomSheet fragment = new AppointmentDetailBottomSheet();
        Bundle args = new Bundle();
        args.putParcelable("appointment", appointment);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnAppointmentActionListener(OnAppointmentActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appointment = getArguments().getParcelable("appointment");
        }
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        // Expand the bottom sheet when it's first shown
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_appointment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (appointment == null) {
            dismiss();
            return;
        }

        // Initialize views
        ImageView btnClose = view.findViewById(R.id.btn_close_bottom_sheet);
        TextView tvStatus = view.findViewById(R.id.textView_appointment_status);
        TextView tvDateTime = view.findViewById(R.id.textView_appointment_datetime);
        TextView tvDepartment = view.findViewById(R.id.textView_appointment_department);
        TextView tvDoctor = view.findViewById(R.id.textView_appointment_doctor);
        ImageView imgDoctorAvatar = view.findViewById(R.id.imageView_doctor_avatar2);
        TextView tvPatientName = view.findViewById(R.id.textView_patient_name);
        TextView tvPatientPhone = view.findViewById(R.id.textView_patient_phone);
        TextView tvPatientDateOfBirth = view.findViewById(R.id.textView_patient_dob);
        TextView tvPatientGender = view.findViewById(R.id.textView_patient_gender);
        TextView tvNotes = view.findViewById(R.id.textView_appointment_notes);
        TextView textView_thoigiandat = view.findViewById(R.id.textView_thoigiandat);
        Button btn1 = view.findViewById(R.id.btn_1);
        Button btn2 = view.findViewById(R.id.btn_2);

        // Set data to views
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Set status with appropriate color
        tvStatus.setText(getAppointmentStatusText(appointment.getStatus()));
        tvStatus.setTextColor(getStatusColor(appointment.getStatus()));

        // Set appointment details
        String dateTimeText = appointment.getAppointmentDate() != null && !appointment.getAppointmentDate().isEmpty() ?
                DateUtils.formatDate(appointment.getAppointmentDate()) + " | " + getShiftTime(appointment.getShiftName()) :
                "Không có dữ liệu";


        tvDateTime.setText(dateTimeText);
        tvDepartment.setText(appointment.getDepartmentName());
        tvDoctor.setText("BS. " + appointment.getDoctorName());
        textView_thoigiandat.setText(appointment.getCreated_at() != null && !appointment.getCreated_at().isEmpty() ?
                "Thời gian đặt: " + DateUtils.formatDate1(appointment.getCreated_at()) : "Không có dữ liệu");

        String BASE_URL = ApiClient.getBaseUrl(requireContext());

        // Load doctor avatar if available
        if (appointment.getDoctorAvatarUrl() != null && !appointment.getDoctorAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(BASE_URL + appointment.getDoctorAvatarUrl())
                    .placeholder(R.drawable.doctor_placeholder)
                    .error(R.drawable.doctor_placeholder)
                    .circleCrop()
                    .into(imgDoctorAvatar);
        }


        // Set patient information
        tvPatientName.setText(appointment.getPatientName());
        tvPatientPhone.setText(appointment.getPatientPhone());
        if (appointment.getPatientDateOfBirth() != null && !appointment.getPatientDateOfBirth().isEmpty()) {
            tvPatientDateOfBirth.setText(appointment.getPatientDateOfBirth());
        } else {
            tvPatientDateOfBirth.setText("Không có dữ liệu");
        }

        tvPatientGender.setText(appointment.getPatientGender());

        // Set notes if available
        if (appointment.getNotes() != null && !appointment.getNotes().isEmpty()) {
            tvNotes.setText(appointment.getNotes());
        } else {
            tvNotes.setText("Không có ghi chú");
            tvNotes.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
        }


        // Configure button visibility based on appointment status
        if ("Scheduled".equalsIgnoreCase(appointment.getStatus())) {
            btn1.setText("Hủy lịch hẹn");
            btn2.setText("Đặt lịch mới");
            btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.VISIBLE);

        }else if ("Completed".equalsIgnoreCase(appointment.getStatus())) {
            btn2.setText("Đặt lịch mới");
            btn2.setVisibility(View.VISIBLE);
            btn1.setVisibility(View.GONE);

        } else if ("Cancelled".equalsIgnoreCase(appointment.getStatus())) {
            btn1.setText("Xóa lịch hẹn");
            btn2.setText("Đặt lịch mới");
            btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.VISIBLE);
        }

        btn1.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dark_blue));
        btn2.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.dark_blue));
        // Set click listeners
        btnClose.setOnClickListener(v -> dismiss());

        btn1.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBtn1(appointment);
            }
            dismiss();
        });

        btn2.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBtn2(appointment);
            } else {
            }
            dismiss();
        });

    }

    private String getAppointmentStatusText(String status) {
        if (status == null) return "Chưa xác định";

        switch (status.toLowerCase()) {
            case "scheduled":
                return "Đã lên lịch";
            case "completed":
                return "Đã hoàn thành";
            case "cancelled":
                return "Đã hủy";
            default:
                return "Chưa xác định";
        }
    }

    private int getStatusColor(String status) {
        int colorResId;
        if (status == null) return ContextCompat.getColor(requireContext(), R.color.gray);

        switch (status.toLowerCase()) {
            case "scheduled":
                colorResId = R.color.green;
                break;
            case "completed":
                colorResId = R.color.green;
                break;
            case "cancelled":
                colorResId = R.color.red;
                break;
            default:
                colorResId = R.color.gray;
        }
        return ContextCompat.getColor(requireContext(), colorResId);
    }
    private String getShiftTime(String shift) {
        switch (shift) {
            case "Shift 1":
                return "7h00-9h00";
            case "Shift 2":
                return "9h00-11h00";
            case "Shift 3":
                return "13h00-15h00";
            case "Shift 4":
                return "15h00-17h00";
            default:
                return shift; // Return the original if unknown
        }
    }
    @Override
    public int getTheme() {
        return R.style.BottomSheetTheme;
    }
}