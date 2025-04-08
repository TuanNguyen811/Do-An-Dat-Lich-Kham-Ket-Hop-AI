package com.example.app1.adapter;

import static com.example.app1.API.ApiClient.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app1.R;
import com.example.app1.activity.AppointmentActivity;
import com.example.app1.activity.MainActivity;
import com.example.app1.models.Department;

import java.util.ArrayList;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder> {
    private ArrayList<Department> departmentList;
    private Context context;
    private OnDepartmentClickListener listener;

    public interface OnDepartmentClickListener {
        void onDepartmentClick(Department department);
    }

    public DepartmentAdapter(ArrayList<Department> departmentList, Context context, DepartmentAdapter.OnDepartmentClickListener listener) {
        this.departmentList = departmentList;
        this.context = context;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name, description;
        Button bookAppointment;

        public ViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.ImageView_main_department_avatar);
            name = view.findViewById(R.id.textView_main_department_name);
            description = view.findViewById(R.id.textView_main_department_description);
            bookAppointment = view.findViewById(R.id.button_main_department_appointments);
        }
    }

    @NonNull
    @Override
    public DepartmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_department_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Department d = departmentList.get(position);
        holder.name.setText(d.getName());
        holder.description.setText(d.getDescription());

        // Tải ảnh bằng Glide
        Glide.with(context)
                .load(BASE_URL + d.getAvatar_url())
                .placeholder(R.drawable.icons8_load) // ảnh mặc định khi đang tải
                .error(R.drawable.icons8_department_40) // ảnh nếu lỗi
                .into(holder.avatar);

        holder.itemView.setOnClickListener(v -> {
            listener.onDepartmentClick(d);
        });
        holder.bookAppointment.setOnClickListener(v -> {
            listener.onDepartmentClick(d);
        });
    }

    @Override
    public int getItemCount() {
        return departmentList.size();
    }
}

