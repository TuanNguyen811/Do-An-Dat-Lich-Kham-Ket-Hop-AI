from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException, Form
from sqlalchemy.orm import Session
from typing import Dict, Any, List, Optional

import crud
import schemas
from Oauth import deps
from crud import crud_appointment, crud_DoctorSchedule

router = APIRouter(prefix="", tags=["appointments"])

# Appointment routes
@router.post("/appointments", response_model=Dict[str, Any])
def create_appointment(
        appointment: schemas.AppointmentCreate,
        current_user=Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    appointment.patient_id = current_user.get("user_id")

    # Check if doctor exists
    doctor = crud.get_doctor(db, doctor_id=appointment.doctor_id)
    if not doctor:
        raise HTTPException(status_code=404, detail="Doctor not found")

    # Check if department exists
    department = crud.get_department(db, department_id=appointment.department_id)
    if not department:
        raise HTTPException(status_code=404, detail="Department not found")

    # Create appointment
    result = crud.create_appointment(db=db, appointment=appointment)
    if not result:
        raise HTTPException(status_code=500, detail="Failed to create appointment")

    return {"message": "Appointment created successfully", "appointment_id": result[0]}

@router.post("/appointments/count_and_schedule", response_model=Dict[str, Any])
def count_and_schedule_appointments(
        doctor_id: int = Form(...),
        appointment_date: str = Form(...),
        db: Session = Depends(deps.get_db)
):
    #dem lich da hen
    # Count appointments for each shift on the given date
    shifts = ['Shift 1', 'Shift 2', 'Shift 3', 'Shift 4']
    appointment_counts = [crud_appointment.count_appointments(db, doctor_id, appointment_date, shift) for shift in shifts]

    # Get week and weekday
    week, weekday = get_week_and_weekday(appointment_date)

    #dem max
    # Get doctor's schedule for the week
    doctor_schedules = crud_DoctorSchedule.get_doctor_schedules_by_week(db, week, doctor_id=doctor_id)
    if not doctor_schedules:
        raise HTTPException(status_code=404, detail="Doctor schedule not found")

    # Get max appointments for the specific weekday
    max_appointments = doctor_schedules[0].get(weekday, "0-0-0-0").split('-')
    max_appointments = list(map(int, max_appointments))

    # Combine max appointments and appointment counts into a single list
    combined_results = max_appointments + appointment_counts

    return {
        "combined_results": combined_results
    }

@router.get("/appointments", response_model=List[Dict[str, Any]])
def get_appointments(
        skip: int = 0,
        limit: int = 100,
        doctor_id: Optional[int] = None,
        appointment_date: Optional[str] = None,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    if current_user.get("role") != "Admin":
        raise HTTPException(status_code=403, detail="Only admins can access this endpoint")

    appointments = crud_appointment.get_appointments(db, doctor_id, appointment_date, skip, limit)
    if not appointments:
        raise HTTPException(status_code=404, detail="No appointments found")

    return appointments

@router.get("/appointments/by_doctor", response_model=List[Dict[str, Any]])
def get_appointments_by_doctor(
        appointment_date: Optional[str] = None,
        skip: int = 0,
        limit: int = 100,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    doctor_id = current_user.get("user_id")
    appointments = crud_appointment.get_appointments(db, doctor_id, appointment_date, skip, limit)
    if not appointments:
        raise HTTPException(status_code=404, detail="No appointments found for the given doctor")
    return appointments

@router.get("/appointments/by_patient", response_model=List[Dict[str, Any]])
def get_appointments_by_patient(
        status: str,
        skip: int = 0,
        limit: int = 100,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    patient_id = current_user.get("user_id")

    appointments = crud_appointment.get_appointments_by_patient(db, patient_id, status, skip, limit)
    if not appointments:
        raise HTTPException(status_code=404, detail="No appointments found for the given patient")
    return appointments




@router.get("/appointments/{appointment_id}", response_model=Dict[str, Any])
def get_appointment(
        appointment_id: int,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    appointment = crud.get_appointment(db, appointment_id=appointment_id)
    if not appointment:
        raise HTTPException(status_code=404, detail="Appointment not found")

    # Convert row to dict
    appointment_dict = {
        "appointment_id": appointment[0],
        "patient_id": appointment[1],
        "doctor_id": appointment[2],
        "department_id": appointment[3],
        "appointment_date": appointment[4],
        "shift": appointment[5],
        "description": appointment[6],
        "status": appointment[7],
        "created_at": appointment[8]
    }

    # Apply access control
    user_role = current_user.get("role")
    user_id = current_user.get("user_id")

    if (user_role == "Patient" and appointment_dict["patient_id"] != user_id) or \
            (user_role == "Doctor" and appointment_dict["doctor_id"] != user_id):
        raise HTTPException(status_code=403, detail="Not authorized to view this appointment")

    return appointment_dict


@router.put("/appointments/{appointment_id}", response_model=Dict[str, Any])
def update_appointment(
        appointment_id: int,
        appointment_update: schemas.AppointmentUpdate,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    # Check if appointment exists
    appointment = crud.get_appointment(db, appointment_id=appointment_id)
    if not appointment:
        raise HTTPException(status_code=404, detail="Appointment not found")

    # Apply access control
    user_role = current_user.get("role")
    user_id = current_user.get("user_id")

    if user_role == "Patient" and appointment[1] != user_id:
        raise HTTPException(status_code=403, detail="Not authorized to update this appointment")
    elif user_role == "Doctor" and appointment[2] != user_id:
        raise HTTPException(status_code=403, detail="Not authorized to update this appointment")

    # Convert Pydantic model to dict and remove None values
    update_data = {k: v for k, v in appointment_update.dict().items() if v is not None}

    # Update the appointment
    updated_appointment = crud.update_appointment(db, appointment_id=appointment_id, appointment_data=update_data)
    if not updated_appointment:
        raise HTTPException(status_code=500, detail="Failed to update appointment")

    return {"message": "Appointment updated successfully"}

@router.put("/appointments/{appointment_id}/status", response_model=Dict[str, Any])
def update_appointment_status(
        appointment_id: int,
        status: str,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    # Check if appointment exists
    appointment = crud.get_appointment(db, appointment_id=appointment_id)
    if not appointment:
        raise HTTPException(status_code=404, detail="Appointment not found")

    # Apply access control
    user_role = current_user.get("role")
    user_id = current_user.get("user_id")

    if user_role == "Patient" and appointment[1] != user_id:
        raise HTTPException(status_code=403, detail="Not authorized to update this appointment")
    elif user_role == "Doctor" and appointment[2] != user_id:
        raise HTTPException(status_code=403, detail="Not authorized to update this appointment")

    # Update the status
    updated_appointment = crud.update_appointment(db, appointment_id=appointment_id, appointment_data={"status": status})
    if not updated_appointment:
        raise HTTPException(status_code=500, detail="Failed to update appointment status")

    return {"message": "Appointment status updated successfully", "appointment_id": appointment_id, "status": status}

@router.delete("/appointments/{appointment_id}", response_model=Dict[str, Any])
def delete_appointment(
        appointment_id: int,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    # Check if appointment exists
    appointment = crud.get_appointment(db, appointment_id=appointment_id)
    if not appointment:
        raise HTTPException(status_code=404, detail="Appointment not found")

    # Apply access control
    user_role = current_user.get("role")
    user_id = current_user.get("user_id")

    if user_role == "Patient" and appointment[1] != user_id:
        raise HTTPException(status_code=403, detail="Not authorized to delete this appointment")
    elif user_role == "Doctor" and appointment[2] != user_id:
        raise HTTPException(status_code=403, detail="Not authorized to delete this appointment")

    # Delete the appointment
    deleted_appointment = crud.delete_appointment(db, appointment_id=appointment_id)
    if not deleted_appointment:
        raise HTTPException(status_code=500, detail="Failed to delete appointment")

    return {"message": "Appointment deleted successfully"}


def get_week_and_weekday(date_str: str):
    date_obj = datetime.strptime(date_str, '%Y-%m-%d')
    week_number = date_obj.isocalendar()[1]
    weekday = date_obj.strftime('%A')
    return week_number, weekday




