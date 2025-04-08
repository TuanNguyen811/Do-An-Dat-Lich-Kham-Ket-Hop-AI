package com.example.app1.API;

import com.example.app1.utils.SessionManager;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    private SessionManager sessionManager;

    public TokenInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Check if token exists and add it to header
        String token = sessionManager.getToken();
        if (token != null && !token.isEmpty()) {
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", token)
                    .build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(originalRequest);
    }
}