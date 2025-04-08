package com.example.app1.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.R;
import com.example.app1.adapter.DepartmentAdapterVertcal;
import com.example.app1.models.Department;

import java.util.ArrayList;

public class ListDepartmentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DepartmentAdapterVertcal adapter;
    private ArrayList<Department> departmentList;
    private ConstraintLayout imageView_listDepartment_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_department);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView_listDepartment_exit = findViewById(R.id.button_listDepartment_exit);
        imageView_listDepartment_exit.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView_department);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Nhận dữ liệu
        departmentList = (ArrayList<Department>) getIntent().getSerializableExtra("departments");
        if (departmentList == null) departmentList = new ArrayList<>();

        adapter = new DepartmentAdapterVertcal( this,departmentList, null);
        recyclerView.setAdapter(adapter);
    }
}
