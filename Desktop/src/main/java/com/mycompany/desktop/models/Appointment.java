/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.desktop.models;


import java.util.Date;

public class Appointment {
    private int appointment_id;
    private int patient_id;
    private String patient_name;
    private String patient_phone;
    private String patient_gender;
    private String patient_date_of_birth;
    private int doctor_id;
    private String doctor_name;
    private String doctor_avatar_url;
    private int department_id;
    private String department_name;
    private String appointment_date;
    private String shift; // Possible values: "Shift 1", "Shift 2", "Shift 3", "Shift 4"
    private String description; // Optional
    private String status; // Default value: "Scheduled", possible values: "Scheduled", "Completed", "Cancelled
    private String created_at;

    // Default constructor
    public Appointment() {
    }

    public Appointment(int appointment_id, int patient_id, String patient_name, String patient_phone, String patient_gender, String patient_date_of_birth, int doctor_id, String doctor_name, String doctor_avatar_url, int department_id, String department_name, String appointment_date, String shift, String description, String status, String created_at) {
        this.appointment_id = appointment_id;
        this.patient_id = patient_id;
        this.patient_name = patient_name;
        this.patient_phone = patient_phone;
        this.patient_gender = patient_gender;
        this.patient_date_of_birth = patient_date_of_birth;
        this.doctor_id = doctor_id;
        this.doctor_name = doctor_name;
        this.doctor_avatar_url = doctor_avatar_url;
        this.department_id = department_id;
        this.department_name = department_name;
        this.appointment_date = appointment_date;
        this.shift = shift;
        this.description = description;
        this.status = status;
        this.created_at = created_at;
    }

   
    // Getters and setters
    public int getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getPatientName() {
        return patient_name;
    }

    public void setPatientName(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getPatientGender() {
        return patient_gender;
    }

    public void setPatientGender(String patient_gender) {
        this.patient_gender = patient_gender;
    }

    public String getPatientDateOfBirth() {
        return patient_date_of_birth;
    }

    public void setPatientDateOfBirth(String patient_date_of_birth) {
        this.patient_date_of_birth = patient_date_of_birth;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getDoctorName() {
        return doctor_name;
    }

    public void setDoctorName(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDoctorAvatarUrl() {
        return doctor_avatar_url;
    }

    public void setDoctorAvatarUrl(String doctor_avatar_url) {
        this.doctor_avatar_url = doctor_avatar_url;
    }

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
    }

    public String getDepartmentName() {
        return department_name;
    }

    public void setDepartmentName(String department_name) {
        this.department_name = department_name;
    }

    public String getAppointmentDate() {
        return appointment_date;
    }

    public void setAppointmentDate(String appointment_date) {
        this.appointment_date = appointment_date;
    }

    public String getShiftName() {
        return shift;
    }

    public void setShiftName(String shift) {
        this.shift = shift;
    }

    public String getNotes() {
        return description;
    }

    public void setNotes(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPatientPhone() {
        return patient_phone;
    }
    public void setPatientPhone(String patient_phone) {
        this.patient_phone = patient_phone;
    }
}