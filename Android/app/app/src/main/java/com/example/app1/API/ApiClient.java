package com.example.app1.API;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.app1.utils.*;

public class ApiClient {
   // private static String BASE_URL = "http://localhost:8000"; // mặc định
    private static Retrofit retrofit = null;

    // Cập nhật IP mới và lưu vào SharedPreferences
    public static void setBaseUrl(Context context, String BASE_URL) {
        retrofit = null;
        SharedPreferences prefs = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        prefs.edit().putString("base_url", BASE_URL).apply();
    }

    public static String getBaseUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String savedUrl = prefs.getString("base_url", "http://localhost:8000"); // IP mặc định nếu chưa lưu
        return savedUrl;
    }
    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // Nếu chưa có BASE_URL từ SharedPreferences, dùng mặc định
            SharedPreferences prefs = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            String savedUrl = prefs.getString("base_url", "http://localhost:8000"); // IP mặc định nếu chưa lưu


            // Create logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Create token interceptor
            SessionManager sessionManager = new SessionManager(context);
            TokenInterceptor tokenInterceptor = new TokenInterceptor(sessionManager);

            // Create OkHttp client with interceptors
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(tokenInterceptor)
                    .build();

            // Create and return retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(savedUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static AuthService getAuthService(Context context) {
        return getClient(context).create(AuthService.class);
    }
}