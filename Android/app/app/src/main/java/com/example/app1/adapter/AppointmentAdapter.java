package com.example.app1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.R;
import com.example.app1.models.Appointment;
import com.example.app1.utils.DateUtils;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private ArrayList<Appointment> appointmentList;
    private Context context;
    private OnAppointmentClickListener listener;

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
        void onCancelAppointment(Appointment appointment);
    }

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointmentList, OnAppointmentClickListener listener) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView appointment_date, department, doctor, status;
        Button btn_view_appointment_details, btn_cancel_appointment;
        public ViewHolder(View itemView) {
            super(itemView);
            appointment_date = itemView.findViewById(R.id.textView_item_appointment_date);
            department = itemView.findViewById(R.id.textView_item_appointment_department);
            doctor = itemView.findViewById(R.id.textView_item_appointment_doctor);
            status = itemView.findViewById(R.id.textView_item_appointment_status);
            btn_view_appointment_details = itemView.findViewById(R.id.btn_view_appointment_details);
            btn_cancel_appointment = itemView.findViewById(R.id.btn_cancel_appointment);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        // Construct the shift time range string
        String shiftTime = getShiftTime(appointment.getShiftName());

        holder.appointment_date.setText(DateUtils.formatDate(appointment.getAppointmentDate()) + " (" + shiftTime + ")");

        holder.department.setText(appointment.getDepartmentName());
        holder.doctor.setText(appointment.getDoctorName());
        holder.status.setText(appointment.getStatus());

        String status = appointment.getStatus();  // Lấy trạng thái từ dữ liệu

        if (status.equalsIgnoreCase("Scheduled")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.white));

        } else if (status.equalsIgnoreCase("Completed")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.btn_cancel_appointment.setVisibility(View.GONE);
        } else if (status.equalsIgnoreCase("Cancelled")) {
            holder.btn_cancel_appointment.setText("Đăt lại");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.status.setBackground(ContextCompat.getDrawable(context, R.drawable.status_badge_background));
        }

        holder.itemView.setOnClickListener(v -> listener.onAppointmentClick(appointment));

        holder.btn_view_appointment_details.setOnClickListener(v -> {
            // Handle view appointment details click
            listener.onAppointmentClick(appointment);
        });
        holder.btn_cancel_appointment.setOnClickListener(v -> {
            // Handle cancel appointment click
            listener.onCancelAppointment(appointment);
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    // Helper method to return the shift time string
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
                return "";
        }
    }
}
