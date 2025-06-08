package com.example.app1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app1.API.ApiClient;
import com.example.app1.R;
import com.example.app1.models.Doctor;
import com.example.app1.utils.SessionManager;

import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    private ArrayList<Doctor> doctorList;
    private Context context;
    private DoctorAdapter.OnDoctorClickListener listener;
    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public DoctorAdapter(Context context, ArrayList<Doctor> doctorList, DoctorAdapter.OnDoctorClickListener listener) {
        this.context = context;
        this.doctorList = doctorList;
        this.listener = listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define your view elements here
        ImageView avatar;
        TextView name, description, department;
        Button bookAppointment;
        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.ImageView_itemdoctor_avatar);
            name = itemView.findViewById(R.id.textView_itemdoctor_name);
            description = itemView.findViewById(R.id.textView_itemdoctor_description);
            department = itemView.findViewById(R.id.textView_itemdoctor_department);
            bookAppointment = itemView.findViewById(R.id.button_book_appointment);
        }
    }

    @NonNull
    @Override
    public DoctorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor_vertical, parent, false);
        return new DoctorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorAdapter.ViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.name.setText("BS: " + doctor.getFull_name());
        holder.description.setText("Mô tả: " + doctor.getDescription());
        holder.department.setText("Email: " + doctor.getEmail());
        // Load image using Glide or any other image loading library
        String BASE_URL = ApiClient.getBaseUrl(context);

        Glide.with(context)
                .load(BASE_URL + doctor.getAvatar_url())
                .placeholder(R.drawable.icons8_load)
                .error(R.drawable.icons8_department_40)
                .into(holder.avatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDoctorClick(doctor);
                }
            }
        });
        holder.bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDoctorClick(doctor);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

}
