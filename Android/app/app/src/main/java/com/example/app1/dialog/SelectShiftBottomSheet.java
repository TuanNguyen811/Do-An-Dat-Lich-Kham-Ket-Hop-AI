package com.example.app1.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app1.API.ApiClient;
import com.example.app1.API.AuthService;
import com.example.app1.R;
import com.example.app1.utils.CombinedResultsResponse;
import com.example.app1.models.Doctor;
import com.example.app1.utils.DateUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectShiftBottomSheet extends BottomSheetDialogFragment {
    private ShiftSelectionListener listener;
    private TextView textView_fragment_shift1, textView_fragment_shift2, textView_fragment_shift3,
    textView_fragment_shift4, textView_selected_date;
    private Doctor doctor;
    private String appointmentDate;
    private String appointmentDayOfWeek;
    private AuthService apiService;
    private  List<Integer> appointmentStats;
    public interface ShiftSelectionListener {
        void setListener(int shift, String queue);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectShiftBottomSheet.ShiftSelectionListener) {
            listener = (SelectShiftBottomSheet.ShiftSelectionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DoctorSelectionListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout của BottomSheet
        View view = inflater.inflate(R.layout.fragment_select_shift, container, false);

        textView_fragment_shift1 = view.findViewById(R.id.textView_fragment_shift1);
        textView_fragment_shift2 = view.findViewById(R.id.textView_fragment_shift2);
        textView_fragment_shift3 = view.findViewById(R.id.textView_fragment_shift3);
        textView_fragment_shift4 = view.findViewById(R.id.textView_fragment_shift4);
        textView_fragment_shift1.setText("Không có ca trực");
        textView_fragment_shift2.setText("Không có ca trực");
        textView_fragment_shift3.setText("Không có ca trực");
        textView_fragment_shift4.setText("Không có ca trực");
        textView_fragment_shift1.setTextColor(getResources().getColor(R.color.gray));
        textView_fragment_shift1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_block,0);
        textView_fragment_shift2.setTextColor(getResources().getColor(R.color.gray));
        textView_fragment_shift2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_block,0);
        textView_fragment_shift3.setTextColor(getResources().getColor(R.color.gray));
        textView_fragment_shift3.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_block,0);
        textView_fragment_shift4.setTextColor(getResources().getColor(R.color.gray));
        textView_fragment_shift4.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_block,0);
        textView_fragment_shift1.setEnabled(false);
        textView_fragment_shift2.setEnabled(false);
        textView_fragment_shift3.setEnabled(false);
        textView_fragment_shift4.setEnabled(false);



        apiService = ApiClient.getAuthService(getContext());
        appointmentStats = new ArrayList<>();

        // Lấy danh sách khoa từ arguments (được truyền từ Activity)
        doctor = (Doctor) getArguments().getSerializable("doctor");
        appointmentDate = getArguments().getString("appointment_date");
        appointmentDayOfWeek = getArguments().getString("appointment_day_of_week");


        if (doctor == null) {
            doctor = new Doctor();
        }
        if (appointmentDate == null) {
            appointmentDate = "";
        }
        textView_selected_date = view.findViewById(R.id.textView_selected_date);
        textView_selected_date.setText(appointmentDayOfWeek + ", " + DateUtils.formatDate(appointmentDate));

        apiService.getAppointmentStats(doctor.getUser_id(), appointmentDate).enqueue(new Callback<CombinedResultsResponse>() {
            @Override
            public void onResponse(Call<CombinedResultsResponse> call, Response<CombinedResultsResponse> response) {
                if (response.isSuccessful()) {
                    CombinedResultsResponse combinedResultsResponse = response.body();
                    if (combinedResultsResponse != null) {
                        appointmentStats = combinedResultsResponse.getCombined_results();
                        if (appointmentStats != null) {

                            // Cập nhật dữ liệu vào các TextView
                            if (appointmentStats.get(4) < appointmentStats.get(0)) {
                                textView_fragment_shift1.setText("Đã đặt: " + appointmentStats.get(4) + "/" + appointmentStats.get(0));
                                textView_fragment_shift1.setTextColor(getResources().getColor(R.color.green));
                                textView_fragment_shift1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_circle,0);
                                textView_fragment_shift1.setEnabled(true);
                            } else {
                                textView_fragment_shift1.setText("Đã full: " + appointmentStats.get(4) + "/" + appointmentStats.get(0));
                                textView_fragment_shift1.setEnabled(false);
                                textView_fragment_shift1.setTextColor(getResources().getColor(R.color.gray));
                                textView_fragment_shift1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_block,0);

                            }
                            if (appointmentStats.get(5) < appointmentStats.get(1)) {
                                textView_fragment_shift2.setTextColor(getResources().getColor(R.color.green));
                                textView_fragment_shift2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_circle,0);
                                textView_fragment_shift2.setText("Đã đặt: " + appointmentStats.get(5) + "/" + appointmentStats.get(1));
                                textView_fragment_shift2.setEnabled(true);
                            } else {
                                textView_fragment_shift2.setText("Đã full: " + appointmentStats.get(5) + "/" + appointmentStats.get(1));
                                textView_fragment_shift2.setEnabled(false);
                                textView_fragment_shift2.setTextColor(getResources().getColor(R.color.gray));
                                textView_fragment_shift2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_block,0);
                            }
                            if (appointmentStats.get(6) < appointmentStats.get(2)) {
                                textView_fragment_shift3.setTextColor(getResources().getColor(R.color.green));
                                textView_fragment_shift3.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_circle,0);
                                textView_fragment_shift3.setText("Đã đặt: " + appointmentStats.get(6) + "/" + appointmentStats.get(2));
                                textView_fragment_shift3.setEnabled(true);

                            } else {
                                textView_fragment_shift3.setText("Đã full: " + appointmentStats.get(6) + "/" + appointmentStats.get(2));
                                textView_fragment_shift3.setEnabled(false);
                                textView_fragment_shift3.setTextColor(getResources().getColor(R.color.gray));
                                textView_fragment_shift3.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_block,0);
                            }
                            if (appointmentStats.get(7) < appointmentStats.get(3)) {
                                textView_fragment_shift4.setTextColor(getResources().getColor(R.color.green));
                                textView_fragment_shift4.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_circle,0);
                                textView_fragment_shift4.setText("Đã đặt: " + appointmentStats.get(7) + "/" + appointmentStats.get(3));
                                textView_fragment_shift4.setEnabled(true);

                            } else {
                                textView_fragment_shift4.setText("Đã full: " + appointmentStats.get(7) + "/" + appointmentStats.get(3));
                                textView_fragment_shift4.setEnabled(false);
                                textView_fragment_shift4.setTextColor(getResources().getColor(R.color.gray));
                                textView_fragment_shift4.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_block,0);
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể lấy dữ liệu ca trực", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CombinedResultsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Mạng lỗi", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

        textView_fragment_shift1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.setListener(1,(appointmentStats.get(4) + 1) + "/" + appointmentStats.get(0));
                    dismiss();
                }
            }
        });
        textView_fragment_shift2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.setListener(2,(appointmentStats.get(5) + 1) + "/" + appointmentStats.get(1));
                    dismiss();
                }
            }
        });
        textView_fragment_shift3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.setListener(3,(appointmentStats.get(6) + 1) + "/" + appointmentStats.get(2));
                    dismiss();
                }
            }
        });
        textView_fragment_shift4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.setListener(4,(appointmentStats.get(7) + 1)  + "/" + appointmentStats.get(3));
                    dismiss();
                }
            }
        });
        return view;
    }
}
