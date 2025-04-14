package com.mycompany.desktop.utils;

import java.util.prefs.Preferences;

public class SessionManager {
    private static SessionManager instance;
    private Preferences preferences;
    private int currentUserId = 0;

    private static final String PREF_NAME = "com.mycompany.desktop.utils";

    public SessionManager() {
        preferences = Preferences.userRoot().node(PREF_NAME);
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    public int getCurrentUserId() {
        return this.currentUserId;
    }

    public void saveToken(int userId, String token) {
        preferences.put(userId + "_token", token);
        preferences.putBoolean(userId + "_isLoggedIn", true);
    }

    public String getToken() {
        if (currentUserId == 0) return null;
        return preferences.get(currentUserId + "_token", null);
    }

    public boolean isLoggedIn() {
        if (currentUserId == 0) return false;
        return preferences.getBoolean(currentUserId + "_isLoggedIn", false);
    }

    public void clearSession(int userId) {
        preferences.remove(userId + "_token");
        preferences.remove(userId + "_isLoggedIn");
    }
}
