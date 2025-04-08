package com.example.app1.utils;

import android.util.Patterns;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && !name.isEmpty();
    }
}