package com.example.app1.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.R;
import com.example.app1.adapter.DepartmentAdapterVertcal;
import com.example.app1.adapter.DoctorAdapter;
import com.example.app1.models.Department;
import com.example.app1.models.Doctor;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class SelectDoctorBottomSheet extends BottomSheetDialogFragment {
    private RecyclerView recyclerView;
    private DoctorAdapter adapter;
    private ArrayList<Doctor> doctorList;
    private DoctorSelectionListener listener;

    // Interface để giao tiếp với Activity hoặc Fragment
    public interface DoctorSelectionListener {
        void onDoctorSelected(Doctor doctor);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DoctorSelectionListener) {
            listener = (DoctorSelectionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DoctorSelectionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout của BottomSheet
        View view = inflater.inflate(R.layout.fragment_select_doctor, container, false);

        recyclerView = view.findViewById(R.id.recycler_fragment_doctor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy danh sách khoa từ arguments (được truyền từ Activity)
        doctorList = (ArrayList<Doctor>) getArguments().getSerializable("doctors");
        if (doctorList == null) {
            doctorList = new ArrayList<>();
        }
        adapter = new DoctorAdapter(requireContext(), doctorList, new DoctorAdapter.OnDoctorClickListener() {
            @Override
            public void onDoctorClick(Doctor doctor) {
                if (listener != null) {
                    listener.onDoctorSelected(doctor);
                    dismiss();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }
}
