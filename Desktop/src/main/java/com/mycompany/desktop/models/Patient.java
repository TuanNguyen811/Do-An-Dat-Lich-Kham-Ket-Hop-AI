/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.desktop.models;

public class Patient extends User {
    private int patient_id;
    private int insurance_id;

    // Constructors
    public Patient() {
        super();
    }

    public Patient(int user_id, String email, String full_name, String phone, String date_of_birth, String gender, String address, String avatar_url, String password, int insurance_id, int patient_id) {
        super(user_id, email, full_name, phone, date_of_birth, gender, address, avatar_url, password);
        this.insurance_id = insurance_id;
        this.patient_id = patient_id;
    }

    // Getters and Setters
    public int getInsurance_id() {
        return insurance_id;
    }

    public void setInsurance_id(int insurance_id) {
        this.insurance_id = insurance_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "email='" + getEmail() + '\'' +
                ", role='" + getRole() + '\'' +
                ", full_name='" + getFull_name() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", date_of_birth=" + getDate_of_birth() +
                ", gender='" + getGender() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", avatar_url='" + getAvatar_url() + '\'' +
                ", user_id=" + getUser_id() +
                ", insurance_id=" + insurance_id +
                ", patient_id=" + patient_id +
                '}';
    }
}