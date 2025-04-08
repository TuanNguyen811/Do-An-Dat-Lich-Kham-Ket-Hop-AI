package com.example.app1.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Appointment implements Parcelable {
    @SerializedName("appointment_id")
    private int appointment_id;

    @SerializedName("patient_id")
    private int patient_id;

    @SerializedName("patient_name")
    private String patient_name;

    @SerializedName("patient_phone")
    private String patient_phone;

    @SerializedName("patient_gender")
    private String patient_gender;

    @SerializedName("patient_date_of_birth")
    private String patient_date_of_birth;

    @SerializedName("doctor_id")
    private int doctor_id;

    @SerializedName("doctor_name")
    private String doctor_name;

    @SerializedName("doctor_avatar_url")
    private String doctor_avatar_url;

    @SerializedName("department_id")
    private int department_id;

    @SerializedName("department_name")
    private String department_name;

    @SerializedName("appointment_date")
    private String appointment_date;

    @SerializedName("shift")
    private String shift; // Possible values: "Shift 1", "Shift 2", "Shift 3", "Shift 4"

    @SerializedName("description")
    private String description; // Optional

    @SerializedName("status")
    private String status; // Default value: "Scheduled", possible values: "Scheduled", "Completed", "Cancelled

    @SerializedName("created_at")
    private String created_at;

    // Default constructor
    public Appointment() {
    }

    // Constructor used by Parcel
    protected Appointment(Parcel in) {
        appointment_id = in.readInt();
        patient_id = in.readInt();
        patient_name = in.readString();
        patient_gender = in.readString();
        patient_date_of_birth = in.readString();
        doctor_id = in.readInt();
        doctor_name = in.readString();
        doctor_avatar_url = in.readString();
        department_id = in.readInt();
        department_name = in.readString();
        appointment_date = in.readString();
        shift = in.readString();
        description = in.readString();
        status = in.readString();
        created_at = in.readString();
        patient_phone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(appointment_id);
        dest.writeInt(patient_id);
        dest.writeString(patient_name);
        dest.writeString(patient_gender);
        dest.writeString(patient_date_of_birth);
        dest.writeInt(doctor_id);
        dest.writeString(doctor_name);
        dest.writeString(doctor_avatar_url);
        dest.writeInt(department_id);
        dest.writeString(department_name);
        dest.writeString(appointment_date);
        dest.writeString(shift);
        dest.writeString(description);
        dest.writeString(status);
        dest.writeString(created_at);
        dest.writeString(patient_phone);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

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