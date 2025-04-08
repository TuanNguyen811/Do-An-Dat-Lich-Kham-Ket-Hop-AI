from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import Dict, Any, List, Optional

import crud
import schemas
from Oauth import deps

router = APIRouter(prefix="", tags=["mealth-metrics"])


# Health metrics routes
@router.post("/health-metrics", response_model=Dict[str, Any])
def create_health_metric(
        metric: schemas.PatientHealthMetricsCreate,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    # Check permissions
    if current_user.get("role") == "Patient" and current_user.get("user_id") != metric.patient_id:
        raise HTTPException(status_code=403, detail="Not authorized to create metrics for this patient")

    # Check if patient exists
    patient = crud.get_patient(db, patient_id=metric.patient_id)
    if not patient:
        raise HTTPException(status_code=404, detail="Patient not found")

    # Create health metric
    result = crud.create_patient_health_metric(db=db, metric=metric)
    if not result:
        raise HTTPException(status_code=500, detail="Failed to create health metric")

    return {"message": "Health metric created successfully", "metric_id": result[0]}


@router.get("/health-metrics", response_model=List[Dict[str, Any]])
def get_health_metrics(
        skip: int = 0,
        limit: int = 100,
        patient_id: Optional[int] = None,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    # Apply access control
    user_role = current_user.get("role")
    user_id = current_user.get("user_id")

    if user_role == "Patient":
        # Patients can only see their own metrics
        patient_id = user_id

    metrics = crud.get_patient_health_metrics(db, patient_id=patient_id, skip=skip, limit=limit)
    return metrics


# Medical history routes
@router.post("/medical-history", response_model=Dict[str, Any])
def create_medical_history(
        history: schemas.MedicalHistoryCreate,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    # Only doctors can create medical history
    if current_user.get("role") != "Doctor":
        raise HTTPException(status_code=403, detail="Only doctors can create medical history records")

    # Check if the doctor is the one assigned to the appointment
    appointment = crud.get_appointment(db, appointment_id=history.appointment_id)
    if not appointment:
        raise HTTPException(status_code=404, detail="Appointment not found")

    if appointment[2] != current_user.get("user_id"):
        raise HTTPException(status_code=403,
                            detail="You are not authorized to create medical history for this appointment")

    # Create medical history
    result = crud.create_medical_history(db=db, history=history)
    if not result:
        raise HTTPException(status_code=500, detail="Failed to create medical history")

    return {"message": "Medical history created successfully", "history_id": result[0]}


@router.get("/medical-history", response_model=List[Dict[str, Any]])
def get_medical_histories(
        skip: int = 0,
        limit: int = 100,
        patient_id: Optional[int] = None,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    # Apply access control
    user_role = current_user.get("role")
    user_id = current_user.get("user_id")

    if user_role == "Patient":
        # Patients can only see their own medical history
        patient_id = user_id

    histories = crud.get_medical_histories(db, patient_id=patient_id, skip=skip, limit=limit)

    if user_role == "Doctor":
        # Doctors can only see medical histories they created
        histories = [h for h in histories if h['doctor_id'] == user_id]

    return histories


@router.get("/medical-history/{history_id}", response_model=Dict[str, Any])
def get_medical_history(
        history_id: int,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    history = crud.get_medical_history(db, history_id=history_id)
    if not history:
        raise HTTPException(status_code=404, detail="Medical history not found")

    # Convert row to dict
    history_dict = {
        "history_id": history[0],
        "appointment_id": history[1],
        "patient_id": history[2],
        "doctor_id": history[3],
        "department_id": history[4],
        "visit_date": history[5],
        "diagnosis": history[6],
        "treatment": history[7],
        "notes": history[8],
        "created_at": history[9]
    }

    # Apply access control
    user_role = current_user.get("role")
    user_id = current_user.get("user_id")

    if (user_role == "Patient" and history_dict["patient_id"] != user_id) or \
            (user_role == "Doctor" and history_dict["doctor_id"] != user_id):
        raise HTTPException(status_code=403, detail="Not authorized to view this medical history")

    return history_dict

