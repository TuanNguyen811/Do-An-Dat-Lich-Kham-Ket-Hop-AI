package com.example.app1.API;

import com.example.app1.models.*;
import com.example.app1.utils.AvatarResponse;
import com.example.app1.utils.CombinedResultsResponse;
import com.example.app1.utils.TokenResponse;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface AuthService {
    @FormUrlEncoded
    @POST("/login")
    Call<TokenResponse> login(
            @Field("username") String email,
            @Field("password") String password,
            @Field("role") String role);

    @POST("/register/patient")
    Call<Patient> registerPatient(@Body Patient request);

    @GET("/profile/patient")
    Call<Patient> getPatientProfile(@Header("Authorization") String token);

    @Multipart
    @POST("/user/avatar")
    Call<AvatarResponse> uploadAvatar(
            @Part MultipartBody.Part file,
            @Header("Authorization") String token
    );

    @GET("/user/avatar")
    Call<ResponseBody> getAvatar(
            @Header("Authorization") String token
    );
    @PUT("/profile/patient/update")
    Call<Patient> updatePatientProfile(
            @Header("Authorization") String token,
            @Body Patient request
    );

    @GET("departments")
    Call<List<Department>> getDepartments();


    // Doctors list
    @GET("doctors")
    Call<List<Doctor>> getListDoctors(
            @Header("Authorization") String token,
            @Query("department_id") Integer departmentId
    );
    @FormUrlEncoded
    @POST("appointments/count_and_schedule")
    Call<CombinedResultsResponse> getAppointmentStats(
            @Field("doctor_id") int doctorId,
            @Field("appointment_date") String appointmentDate
    );

    @POST("appointments")
    Call<JsonObject> createAppointment(
            @Header("Authorization") String token,
            @Body Appointment appointment
    );
    @GET("/appointments/by_patient")
    Call<List<Appointment>> getAppointmentsByPatient(
            @Header("Authorization") String token,
            @Query("status") String status
    );

    @PUT("/appointments/{appointment_id}/status")
    Call<JsonObject> updateAppointmentStatus(
            @Header("Authorization") String token,
            @Path("appointment_id") int appointmentId,
            @Query("status") String status
    );
//@router.delete("/appointments/{appointment_id}"
    @DELETE("/appointments/{appointment_id}")
    Call<JsonObject> deleteAppointment(
            @Header("Authorization") String token,
            @Path("appointment_id") int appointmentId
    );

    @POST("chat")
    Call<ChatResponse> sendChatMessage(@Header("Authorization") String token, @Body ChatRequest request);

    @POST("chat/history")
    Call<List<ChatHistoryResponse>> getChatHistory(@Header("Authorization") String token);

    @POST("/chat/reset_session")
    Call<JsonObject> resetChatSession(@Header("Authorization") String token);

    // Create a new health metric
    @POST("/patient/health-metrics")
    Call<PatientHealthMetrics> createHealthMetric(
            @Header("Authorization") String token,
            @Body PatientHealthMetrics healthMetrics
    );

    @GET("/patient/health-metrics")
    Call<PatientHealthMetrics> getHealthMetrics(
            @Header("Authorization") String token
    );
}