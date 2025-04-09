from sqlalchemy import text
from sqlalchemy.orm import Session
from schemas import PatientHealthMetricsCreate, PatientHealthMetricsResponse
from typing import List, Optional
from datetime import datetime


# Create a new health metric record or update if exists
def create_or_update_health_metric(db: Session, metric: PatientHealthMetricsCreate):
    # Check if metrics exist for this patient
    check_query = text("SELECT id FROM PatientHealthMetrics WHERE patient_id = :patient_id")
    existing = db.execute(check_query, {"patient_id": metric.patient_id}).first()

    if existing:
        # Update existing metrics
        update_query = text("""
            UPDATE PatientHealthMetrics 
            SET systolic_bp = :systolic_bp,
                diastolic_bp = :diastolic_bp,
                heart_rate = :heart_rate,
                body_temperature = :body_temperature,
                respiratory_rate = :respiratory_rate,
                weight_kg = :weight_kg,
                height_cm = :height_cm,
                bmi = :bmi,
                blood_glucose = :blood_glucose,
                cholesterol_total = :cholesterol_total,
                ldl = :ldl,
                hdl = :hdl,
                triglycerides = :triglycerides,
                hemoglobin = :hemoglobin,
                recorded_at = CURRENT_TIMESTAMP
            WHERE patient_id = :patient_id
        """)

        db.execute(update_query, {
            "patient_id": metric.patient_id,
            "systolic_bp": metric.systolic_bp,
            "diastolic_bp": metric.diastolic_bp,
            "heart_rate": metric.heart_rate,
            "body_temperature": metric.body_temperature,
            "respiratory_rate": metric.respiratory_rate,
            "weight_kg": metric.weight_kg,
            "height_cm": metric.height_cm,
            "bmi": metric.bmi,
            "blood_glucose": metric.blood_glucose,
            "cholesterol_total": metric.cholesterol_total,
            "ldl": metric.ldl,
            "hdl": metric.hdl,
            "triglycerides": metric.triglycerides,
            "hemoglobin": metric.hemoglobin
        })
        db.commit()

    else:
        # Create new record with default values (0) for missing fields
        insert_query = text("""
            INSERT INTO PatientHealthMetrics (
                patient_id, systolic_bp, diastolic_bp, heart_rate, 
                body_temperature, respiratory_rate, weight_kg, height_cm, bmi,
                blood_glucose, cholesterol_total, ldl, hdl, triglycerides, hemoglobin
            ) VALUES (
                :patient_id, 
                COALESCE(:systolic_bp, 0),
                COALESCE(:diastolic_bp, 0),
                COALESCE(:heart_rate, 0),
                COALESCE(:body_temperature, 0),
                COALESCE(:respiratory_rate, 0),
                COALESCE(:weight_kg, 0),
                COALESCE(:height_cm, 0),
                COALESCE(:bmi, 0),
                COALESCE(:blood_glucose, 0),
                COALESCE(:cholesterol_total, 0),
                COALESCE(:ldl, 0),
                COALESCE(:hdl, 0),
                COALESCE(:triglycerides, 0),
                COALESCE(:hemoglobin, 0)
            )
        """)

        db.execute(insert_query, {
            "patient_id": metric.patient_id,
            "systolic_bp": metric.systolic_bp,
            "diastolic_bp": metric.diastolic_bp,
            "heart_rate": metric.heart_rate,
            "body_temperature": metric.body_temperature,
            "respiratory_rate": metric.respiratory_rate,
            "weight_kg": metric.weight_kg,
            "height_cm": metric.height_cm,
            "bmi": metric.bmi,
            "blood_glucose": metric.blood_glucose,
            "cholesterol_total": metric.cholesterol_total,
            "ldl": metric.ldl,
            "hdl": metric.hdl,
            "triglycerides": metric.triglycerides,
            "hemoglobin": metric.hemoglobin
        })
        db.commit()

    # Get and return the updated/created record
    return get_health_metric_by_patient(db, metric.patient_id)


# Get health metric by patient_id
def get_health_metric_by_patient(db: Session, patient_id: int) -> Optional[dict]:
    query = text("""
        SELECT 
            id, patient_id, recorded_at, 
            systolic_bp, diastolic_bp, heart_rate, 
            body_temperature, respiratory_rate, weight_kg, height_cm, bmi,
            blood_glucose, cholesterol_total, ldl, hdl, triglycerides, hemoglobin
        FROM PatientHealthMetrics
        WHERE patient_id = :patient_id
    """)

    result = db.execute(query, {"patient_id": patient_id}).first()

    if not result:
        return None

    return {
        "id": result[0],
        "patient_id": result[1],
        "recorded_at": result[2],
        "systolic_bp": result[3],
        "diastolic_bp": result[4],
        "heart_rate": result[5],
        "body_temperature": result[6],
        "respiratory_rate": result[7],
        "weight_kg": result[8],
        "height_cm": result[9],
        "bmi": result[10],
        "blood_glucose": result[11],
        "cholesterol_total": result[12],
        "ldl": result[13],
        "hdl": result[14],
        "triglycerides": result[15],
        "hemoglobin": result[16]
    }