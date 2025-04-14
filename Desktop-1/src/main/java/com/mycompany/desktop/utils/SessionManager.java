package com.mycompany.desktop.utils;

import java.util.prefs.Preferences;

public class SessionManager {
    private static final String PREF_NAME = "com.example.app.utils";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static SessionManager instance;
    private Preferences preferences;

    public SessionManager() {
        preferences = Preferences.userRoot().node(PREF_NAME);
    }

    // Singleton - đảm bảo chỉ có một instance được dùng
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void saveToken(String token) {
        preferences.put(KEY_TOKEN, token);
        preferences.putBoolean(KEY_IS_LOGGED_IN, true);
    }

    public String getToken() {
        return preferences.get(KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        preferences.remove(KEY_TOKEN);
        preferences.remove(KEY_IS_LOGGED_IN);
    }
}
