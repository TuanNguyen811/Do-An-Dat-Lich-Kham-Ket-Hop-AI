package com.mycompany.desktop.utils;
//cung cấp hàm regex email, pass 8 ký tự, phone...

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$"); // Regex đơn giản cho email

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{10,15}$"); // Chấp nhận số điện thoại từ 10-15 chữ số

    public static boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }
}
