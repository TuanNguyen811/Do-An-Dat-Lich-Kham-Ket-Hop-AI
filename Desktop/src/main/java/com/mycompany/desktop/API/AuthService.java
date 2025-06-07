package com.mycompany.desktop.API;

import com.mycompany.desktop.models.CombinedResultWrapper;
import com.google.gson.JsonObject;
import com.mycompany.desktop.models.*;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    // USER
    @FormUrlEncoded
    @POST("login")
    Call<TokenResponse> login(
            @Field("username") String email,
            @Field("password") String password,
            @Field("role") String role
    );

    @Multipart
    @POST("user/avatar")
    Call<AvatarResponse> uploadAvatar(
            @Part MultipartBody.Part file,
            @Header("Authorization") String token
    );

    @GET("user/avatar")
    Call<ResponseBody> getAvatar(
            @Header("Authorization") String token
    );

    //DOCTOR
    @POST("register/doctor")
    Call<Doctor> registerDoctor(
            @Header("Authorization") String token,
            @Body Doctor doctor
    );

    @GET("profile/doctor")
    Call<Doctor> getDoctorProfile(@Header("Authorization") String token);

    @GET("doctors")
    Call<List<Doctor>> getListDoctors(
            @Header("Authorization") String token,
            @Query("department_id") Integer departmentId
    );

    @PUT("profile/doctor/update/{doctor_id}")
    Call<Doctor> updateDoctor(
            @Header("Authorization") String token,
            @Body Doctor doctor,
            @Path("doctor_id") int doctor_id
    );

    @DELETE("/doctors/{doctor_id}")
    Call<Doctor> deleteDoctor(
            @Header("Authorization") String token,
            @Path("doctor_id") int doctor_id
    );

    //ADMIN
    @GET("profile/admin")
    Call<Admin> getAdminProfile(@Header("Authorization") String token);

    @Multipart
    @POST("admin_set_avatar_user")
    Call<AvatarResponse> adminSetAvatarUser(
            @Part MultipartBody.Part file,
            @Header("Authorization") String token,
            @Part("user_id") String userId // Fixed `@Field` → `@Part`
    );

    @GET("admin_get_avatar_user")
    Call<ResponseBody> adminGetAvatarUser(
            @Header("Authorization") String token,
            @Query("user_id") String userId
    );

    @Multipart
    @POST("admin_set_avatar_department")
    Call<AvatarResponse> adminSetAvatarDepartment(
            @Part MultipartBody.Part file,
            @Header("Authorization") String token,
            @Part("department_id") String departmentId // Fixed `@Field` → `@Part`
    );

    @GET("admin_get_avatar_department")
    Call<ResponseBody> adminGetAvatarDepartment(
            @Header("Authorization") String token,
            @Query("department_id") String departmentId
    );

    // Departments
    @POST("departments")
    Call<Department> createDepartment(
            @Header("Authorization") String token,
            @Body Department department
    );

    @GET("departments")
    Call<List<Department>> getDepartments(@Header("Authorization") String token);

    @PUT("/departments/{department_id}")
    Call<Department> updateDepartments(
            @Header("Authorization") String token,
            @Body Department department,
            @Path("department_id") int department_id
    );

    @DELETE("/departments/{department_id}")
    Call<Department> deleteDepartments(
            @Header("Authorization") String token,
            @Path("department_id") int department_id
    );

    //schedules
    @POST("schedules")
    Call<DoctorSchedule> createSchedule(
            @Header("Authorization") String token,
            @Body DoctorSchedule schedule
    );

    @GET("schedules")
    Call<List<DoctorSchedule>> getSchedule(
            @Header("Authorization") String token,
            @Query("doctor_id") int doctor_id
    );

    @PUT("schedules/{schedule_id}")
    Call<DoctorSchedule> updateSchedule(
            @Header("Authorization") String token,
            @Path("schedule_id") int schedule_Id,
            @Body DoctorSchedule schedule
    );

    @FormUrlEncoded
    @POST("/appointments/weekly_count_and_schedule")
    Call<List<CombinedResultWrapper>> getWeeklyCombinedResults(
            @Header("Authorization") String token,
            @Field("doctor_id") int doctorId,
            @Field("start_date") String startDate
    );

    @DELETE("schedules/{schedule_id}")
    Call<DoctorSchedule> deleteSchedule(
            @Header("Authorization") String token,
            @Path("schedule_id") int schedule_Id
    );

    //appointments
    @GET("appointments")
    Call<List<Appointment>> getAppointments(
            @Header("Authorization") String token,
            @Query("doctor_id") int doctor_id,
            @Query("appointment_date") String appointment_date
    );

    @GET("appointments/by_doctor")
    Call<List<Appointment>> getAppointmentsByDoctor(
            @Header("Authorization") String token,
            @Query("appointment_date") String appointment_date
    );

    @PUT("/appointments/{appointment_id}")
    Call<JsonObject> updateAppointmentStatus(
            @Header("Authorization") String token,
            @Path("appointment_id") int appointmentId,
            @Body Appointment appointment
    );

    //Patient
    @GET("patients/{patient_id}")
    Call<Patient> getPatient(
            @Header("Authorization") String token,
            @Path("patient_id") int patient_id
    );

    // Create a new health metric
    @POST("/doctor/patient/{patient_id}/health-metrics")
    Call<PatientHealthMetrics> createHealthMetric(
            @Header("Authorization") String token,
            @Body PatientHealthMetrics healthMetrics,
            @Path("patient_id") int patient_id
    );

    @GET("/doctor/patient/{patient_id}/health-metrics")
    Call<PatientHealthMetrics> getHealthMetrics(
            @Header("Authorization") String token,
            @Path("patient_id") int patient_id
    );

    @POST("notifications/list")
    Call<ResponseBody> sendNotificationsToBackend(
            @Header("Authorization") String token,
            @Body List<NotificationCreate> notifications
    );

}
