package com.example.app1.models;

import java.io.Serializable;

public class PatientHealthMetrics  implements Serializable {
    private Integer patient_id;
    private Integer systolic_bp;
    private Integer diastolic_bp;
    private Integer heart_rate;
    private Double body_temperature;
    private Integer respiratory_rate;
    private Double weight_kg;
    private Double height_cm;
    private Double bmi;
    private Double blood_glucose;
    private Double cholesterol_total;
    private Double ldl;
    private Double hdl;
    private Double triglycerides;
    private Double hemoglobin;
    private Integer id;
    private String recorded_at;

    // Default constructor
    public PatientHealthMetrics() {
    }

    // Constructor with all parameters
    public PatientHealthMetrics(Integer patient_id, Integer systolic_bp, Integer diastolic_bp, Integer heart_rate,
                                Double body_temperature, Integer respiratory_rate, Double weight_kg, Double height_cm,
                                Double bmi, Double blood_glucose, Double cholesterol_total, Double ldl, Double hdl,
                                Double triglycerides, Double hemoglobin, Integer id, String recorded_at) {
        this.patient_id = patient_id;
        this.systolic_bp = systolic_bp;
        this.diastolic_bp = diastolic_bp;
        this.heart_rate = heart_rate;
        this.body_temperature = body_temperature;
        this.respiratory_rate = respiratory_rate;
        this.weight_kg = weight_kg;
        this.height_cm = height_cm;
        this.bmi = bmi;
        this.blood_glucose = blood_glucose;
        this.cholesterol_total = cholesterol_total;
        this.ldl = ldl;
        this.hdl = hdl;
        this.triglycerides = triglycerides;
        this.hemoglobin = hemoglobin;
        this.id = id;
        this.recorded_at = recorded_at;
    }

    // Getters and Setters
    public Integer getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Integer patient_id) {
        this.patient_id = patient_id;
    }

    public Integer getSystolic_bp() {
        return systolic_bp;
    }

    public void setSystolic_bp(Integer systolic_bp) {
        this.systolic_bp = systolic_bp;
    }

    public Integer getDiastolic_bp() {
        return diastolic_bp;
    }

    public void setDiastolic_bp(Integer diastolic_bp) {
        this.diastolic_bp = diastolic_bp;
    }

    public Integer getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(Integer heart_rate) {
        this.heart_rate = heart_rate;
    }

    public Double getBody_temperature() {
        return body_temperature;
    }

    public void setBody_temperature(Double body_temperature) {
        this.body_temperature = body_temperature;
    }

    public Integer getRespiratory_rate() {
        return respiratory_rate;
    }

    public void setRespiratory_rate(Integer respiratory_rate) {
        this.respiratory_rate = respiratory_rate;
    }

    public Double getWeight_kg() {
        return weight_kg;
    }

    public void setWeight_kg(Double weight_kg) {
        this.weight_kg = weight_kg;
    }

    public Double getHeight_cm() {
        return height_cm;
    }

    public void setHeight_cm(Double height_cm) {
        this.height_cm = height_cm;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public Double getBlood_glucose() {
        return blood_glucose;
    }

    public void setBlood_glucose(Double blood_glucose) {
        this.blood_glucose = blood_glucose;
    }

    public Double getCholesterol_total() {
        return cholesterol_total;
    }

    public void setCholesterol_total(Double cholesterol_total) {
        this.cholesterol_total = cholesterol_total;
    }

    public Double getLdl() {
        return ldl;
    }

    public void setLdl(Double ldl) {
        this.ldl = ldl;
    }

    public Double getHdl() {
        return hdl;
    }

    public void setHdl(Double hdl) {
        this.hdl = hdl;
    }

    public Double getTriglycerides() {
        return triglycerides;
    }

    public void setTriglycerides(Double triglycerides) {
        this.triglycerides = triglycerides;
    }

    public Double getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(Double hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRecorded_at() {
        return recorded_at;
    }

    public void setRecorded_at(String recorded_at) {
        this.recorded_at = recorded_at;
    }

    @Override
    public String toString() {
        return "PatientHealthMetrics{" +
                "systolic_bp=" + systolic_bp +
                ", diastolic_bp=" + diastolic_bp +
                ", heart_rate=" + heart_rate +
                ", body_temperature=" + body_temperature +
                ", respiratory_rate=" + respiratory_rate +
                ", weight_kg=" + weight_kg +
                ", height_cm=" + height_cm +
                ", bmi=" + bmi +
                ", blood_glucose=" + blood_glucose +
                ", cholesterol_total=" + cholesterol_total +
                ", ldl=" + ldl +
                ", hdl=" + hdl +
                ", triglycerides=" + triglycerides +
                ", hemoglobin=" + hemoglobin +
                ", recorded_at='" + recorded_at + '\'' +
                '}';
    }
}