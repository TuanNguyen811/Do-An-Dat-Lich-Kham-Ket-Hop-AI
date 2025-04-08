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
import com.example.app1.models.Department;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class SelectDepartmentBottomSheet extends BottomSheetDialogFragment {
    private RecyclerView recyclerView;
    private DepartmentAdapterVertcal adapter;
    private ArrayList<Department> departmentList;
    private DepartmentSelectionListener listener;

    // Interface để giao tiếp với Activity hoặc Fragment
    public interface DepartmentSelectionListener {
        void onDepartmentSelected(Department department);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DepartmentSelectionListener) {
            listener = (DepartmentSelectionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DepartmentSelectionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout của BottomSheet
        View view = inflater.inflate(R.layout.fragment_select_department, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_departments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy danh sách khoa từ arguments (được truyền từ Activity)
        departmentList = (ArrayList<Department>) getArguments().getSerializable("departments");
        if (departmentList == null) {
            departmentList = new ArrayList<>();
        }

        adapter = new DepartmentAdapterVertcal(requireContext(), departmentList, new DepartmentAdapterVertcal.OnDepartmentClickListener() {
            @Override
            public void onDepartmentClick(Department department) {
                if (listener != null) {
                    // Gọi phương thức trong listener khi người dùng chọn một khoa
                    listener.onDepartmentSelected(department);
                    dismiss();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }
}
