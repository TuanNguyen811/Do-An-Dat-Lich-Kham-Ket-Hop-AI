package com.example.app1.API;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.app1.utils.*;

public class ApiClient {
    public static final String BASE_URL = "http://192.168.9.154:8000"; // For Android emulator to connect to localhost
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
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
                    .baseUrl(BASE_URL)
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