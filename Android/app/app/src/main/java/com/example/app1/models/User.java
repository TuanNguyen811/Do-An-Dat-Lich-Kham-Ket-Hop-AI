package com.example.app1.models;

import java.io.Serializable;

public class User implements Serializable {
    protected String email;
    protected String role;
    protected String full_name;
    protected String phone;
    protected String date_of_birth;
    protected String gender;
    protected String address;
    protected String avatar_url;
    protected int user_id;
    protected String password;

    public User() {
    }

    public User(int user_id, String email, String full_name, String phone, String date_of_birth, String gender, String address, String avatar_url, String password) {
        this.user_id = user_id;
        this.email = email;
        this.full_name = full_name;
        this.phone = phone;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.address = address;
        this.avatar_url = avatar_url;
        this.password = password;
        this.role = "Patient";
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role.equals("Patient") || role.equals("Admin") || role.equals("Doctor")) {
            this.role = role;
        } else {
            throw new IllegalArgumentException("Role must be 'Patient', 'Admin' or 'Doctor'");
        }
    }
}
