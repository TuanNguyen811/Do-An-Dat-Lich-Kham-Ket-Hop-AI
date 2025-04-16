package com.example.app1.models;

public class VerifyOtpRequest {
    private String email;
    private String otp;
    private String password;

    public VerifyOtpRequest(String email, String otp, String newPassword) {
        this.email = email;
        this.otp = otp;
        this.password = newPassword;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return password;
    }

    public void setNewPassword(String newPassword) {
        this.password = newPassword;
    }
}