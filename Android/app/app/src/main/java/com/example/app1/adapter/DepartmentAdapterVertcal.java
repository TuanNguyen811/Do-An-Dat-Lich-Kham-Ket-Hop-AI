package com.example.app1.adapter;

import static com.example.app1.API.ApiClient.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app1.R;
import com.example.app1.activity.AppointmentActivity;
import com.example.app1.models.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentAdapterVertcal extends RecyclerView.Adapter<DepartmentAdapterVertcal.ViewHolder> {
    private ArrayList<Department> departmentList;
    private Context context;
    private OnDepartmentClickListener listener;

    public interface OnDepartmentClickListener {
        void onDepartmentClick(Department department);
    }

    public DepartmentAdapterVertcal(Context context, ArrayList<Department> departmentList, OnDepartmentClickListener listener) {
        this.context = context;
        this.departmentList = departmentList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name, description;

        public ViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.ImageView_department_avatar);
            name = view.findViewById(R.id.textView_department_name);
            description = view.findViewById(R.id.textView_department_description);
        }
    }

    @NonNull
    @Override
    public DepartmentAdapterVertcal.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_department_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Department d = departmentList.get(position);
        holder.name.setText(d.getName());
        holder.description.setText(d.getDescription());

        Glide.with(context)
                .load(BASE_URL + d.getAvatar_url())
                .placeholder(R.drawable.icons8_load)
                .error(R.drawable.icons8_department_40)
                .into(holder.avatar);

        holder.itemView.setOnClickListener(v -> {

            if (context.getClass().getSimpleName().equals("ListDepartmentActivity")) {
                Intent intent = new Intent(context, AppointmentActivity.class);
                intent.putExtra("item_department", d); // gửi ID
                intent.putExtra("departments", departmentList); // gửi danh sách
                context.startActivity(intent);
            } else if (context.getClass().getSimpleName().equals("AppointmentActivity")) {
                if (listener != null) {
                    listener.onDepartmentClick(d);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return departmentList.size();
    }
}
