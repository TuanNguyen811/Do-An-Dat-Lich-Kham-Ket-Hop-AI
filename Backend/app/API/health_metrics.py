from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import Dict, Any, Optional

from crud import crud_patient
from schemas import PatientHealthMetricsCreate, PatientHealthMetricsResponse
from Oauth import deps
from Oauth.deps import get_db
from crud.crud_health_metrics import create_or_update_health_metric, get_health_metric_by_patient

router = APIRouter(prefix="", tags=["health-metrics"])


@router.post("/patient/health-metrics", response_model=PatientHealthMetricsResponse)
def update_health_metrics(
        metric_data: PatientHealthMetricsCreate,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(get_db)
):
    """
    Update or create health metrics for the authenticated patient.
    If no records exist, creates new ones with default values of 0.
    """
    if current_user["role"] != "Patient":
        raise HTTPException(status_code=403, detail="Only patients can update their health metrics")

    # Set the patient_id from the authenticated user
    metric_data.patient_id = current_user.get("user_id")

    # Create or update health metrics
    result = create_or_update_health_metric(db, metric_data)
    if not result:
        raise HTTPException(status_code=500, detail="Failed to update health metrics")

    return result


@router.get("/patient/health-metrics", response_model=PatientHealthMetricsResponse)
def get_health_metrics(
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(get_db)
):
    """
    Get health metrics for the authenticated patient or for a specific patient (if doctor)
    """
    user_id = current_user.get("user_id")

    if current_user["role"] == "Patient":
        # Patient can only view their own metrics
        patient_id = user_id
    elif current_user["role"] == "Doctor":
        # Doctors can view any patient metrics
        patient_id = user_id  # Can be modified to accept a parameter for specific patient
    else:
        raise HTTPException(status_code=403, detail="Not authorized")

    metrics = get_health_metric_by_patient(db, patient_id)

    if not metrics:
        # Create default metrics record if none exists
        default_metrics = PatientHealthMetricsCreate(
            patient_id=patient_id,
            systolic_bp=0, diastolic_bp=0, heart_rate=0,
            body_temperature=0, respiratory_rate=0,
            weight_kg=0, height_cm=0, bmi=0,
            blood_glucose=0, cholesterol_total=0,
            ldl=0, hdl=0, triglycerides=0, hemoglobin=0, other_metrics =None
        )
        metrics = create_or_update_health_metric(db, default_metrics)
    return metrics


@router.get("/doctor/patient/{patient_id}/health-metrics", response_model=PatientHealthMetricsResponse)
def get_patient_health_metrics(
        patient_id: int,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(get_db)
):
    """
    Get health metrics for a specific patient (doctor access)
    """
    if current_user["role"] != "Doctor":
        raise HTTPException(status_code=403, detail="Only doctors can access this endpoint")

    metrics = get_health_metric_by_patient(db, patient_id)

    if not metrics:
        # Create default metrics record if none exists
        default_metrics = PatientHealthMetricsCreate(
            patient_id=patient_id,
            systolic_bp=0, diastolic_bp=0, heart_rate=0,
            body_temperature=0, respiratory_rate=0,
            weight_kg=0, height_cm=0, bmi=0,
            blood_glucose=0, cholesterol_total=0,
            ldl=0, hdl=0, triglycerides=0, hemoglobin=0, other_metrics=None
        )
        metrics = create_or_update_health_metric(db, default_metrics)
    return metrics


@router.post("/doctor/patient/{patient_id}/health-metrics", response_model=PatientHealthMetricsResponse)
def update_patient_health_metrics(
        patient_id: int,
        metric_data: PatientHealthMetricsCreate,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(get_db)
):
    """
    Update health metrics for a specific patient (doctor access)
    """
    if current_user["role"] != "Doctor":
        raise HTTPException(status_code=403, detail="Only doctors can access this endpoint")

    # Ensure the passed patient_id is used rather than any value in the request body
    metric_data.patient_id = patient_id

    # Check if patient exists
    patient = crud_patient.get_patient(db, patient_id=patient_id)
    if not patient:
        raise HTTPException(status_code=404, detail="Patient not found")

    # Update or create health metrics for the patient
    result = create_or_update_health_metric(db, metric_data)
    if not result:
        raise HTTPException(status_code=500, detail="Failed to update health metrics")

    return result