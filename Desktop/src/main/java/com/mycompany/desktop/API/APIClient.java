package com.mycompany.desktop.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mycompany.desktop.utils.SessionManager;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    public static String BASE_URL = "";
    private static Retrofit retrofit = null;

    public static void setBaseUrl(String ip) {
        BASE_URL = "http://" + ip + ":8000";
        System.out.println("BASE_URL updated to: " + BASE_URL);
        retrofit = null; // Bắt buộc reset lại retrofit
    }

    public static void init(String ip) {
        BASE_URL = ip;
    }

    public static Retrofit getClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                    @Override
                    public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.toString()); // ISO-8601 format: "2025-04-15T10:20"
                    }
                })
                .create();
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor) // Thêm interceptor nếu cần
                    .build();

            // Sửa lỗi: Gán giá trị vào biến toàn cục
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static AuthService getAuthService() {
        return getClient().create(AuthService.class);
    }
}
