package com.example.app1.models;

public class Doctor extends User  {
    private int department_id;
    private String description;


    public Doctor() {
        super();
    }

    public Doctor(int department_id, String description) {
        this.department_id = department_id;
        this.description = description;
    }

    public Doctor(int department_id, String description, int user_id, String email, String full_name, String phone, String date_of_birth, String gender, String address, String avatar_url, String password) {
        super(user_id, email, full_name, phone, date_of_birth, gender, address, avatar_url, password);
        this.department_id = department_id;
        this.description = description;
    }

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
