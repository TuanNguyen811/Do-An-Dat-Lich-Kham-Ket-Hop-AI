package com.example.app1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app1.R;
import java.util.List;
import com.example.app1.models.Notification;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<Notification> notificationList;

    public NotificationsAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.type.setText(notification.getType());
        holder.message.setText(notification.getMessage());
        holder.time.setText("Scheduled: " + notification.getScheduledTime());
        holder.status.setText("Status: " + notification.getStatus());
        if ("Pending".equals(notification.getStatus())) {
            holder.status.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
        } else if ("Sent".equals(notification.getStatus())) {
            holder.status.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        } else {
            holder.status.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.gray));
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type, message, time, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.text_notification_type);
            message = itemView.findViewById(R.id.text_notification_message);
            time = itemView.findViewById(R.id.text_notification_time);
            status = itemView.findViewById(R.id.text_notification_status);
        }
    }
}