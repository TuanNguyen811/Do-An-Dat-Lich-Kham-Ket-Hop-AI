from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import Dict, Any, List, Optional

import crud
import schemas
from Oauth import deps
from crud import crud_doctor

router = APIRouter(prefix="", tags=["doctor"])

@router.post("/register/doctor", response_model=Dict[str, Any])
def register_doctor(
        doctor: schemas.DoctorCreate,
        db: Session = Depends(deps.get_db),
        current_user=Depends(deps.get_current_user)
):
    if current_user["role"] != "Admin":
        raise HTTPException(status_code=403, detail="Not an admin account")

    # Check if user with email already exists
    db_user = crud.get_user_by_email(db, email=doctor.email)
    if db_user:
        raise HTTPException(status_code=400, detail="Email already registered")

    # Check if department exists
    db_department = crud.get_department(db, department_id=doctor.department_id)
    if not db_department:
        raise HTTPException(status_code=404, detail="Department not found")

    # Set role to Doctor for the user
    doctor.role = "Doctor"

    # Create doctor (this will create a user first and then a doctor)
    result = crud.create_doctor(db=db, doctor=doctor)
    if not result:
        raise HTTPException(status_code=500, detail="Failed to register doctor")

    # Convert result to dict for response
    return {"message": "Doctor registered successfully", "user_id": result[0]}

@router.get("/profile/doctor", response_model=schemas.DoctorResponse)
def read_doctor_me(
        current_user=Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    if current_user["role"] != "Doctor":
        raise HTTPException(status_code=403, detail="Not a doctor account")

    doctor = crud_doctor.get_doctor2(db, doctor_id=current_user["user_id"])
    if not doctor:
        raise HTTPException(status_code=404, detail="Doctor profile not found")

    # Convert row object to dictionary that can be serialized
    #doctor_dict = {column: getattr(doctor, column) for column in doctor._mapping.keys()}
    return doctor

# Doctor routes
@router.get("/doctors", response_model=List[Dict[str, Any]])
def get_doctors(
        skip: int = 0,
        limit: int = 100,
        department_id: Optional[int] = None,
        db: Session = Depends(deps.get_db),
        # current_user: Dict[str, Any] = Depends(deps.get_current_user) # chua dung den
):
    doctors = crud.get_doctors(db, skip=skip, limit=limit)

    # Filter by department_id if provided
    if department_id is not None:
        doctors = [d for d in doctors if d['department_id'] == department_id]

    return doctors

@router.get("/doctors/{doctor_id}", response_model=Dict[str, Any])
def get_doctor(
        doctor_id: int,
        db: Session = Depends(deps.get_db)
):
    doctor = crud.get_doctor(db, doctor_id=doctor_id)
    if not doctor:
        raise HTTPException(status_code=404, detail="Doctor not found")

    # Convert row to dict
    doctor_dict = {
        "user_id": doctor[0],
        "email": doctor[1],
        "role": doctor[3],
        "full_name": doctor[4],
        "phone": doctor[5],
        "date_of_birth": doctor[6],
        "gender": doctor[7],
        "address": doctor[8],
        "avatar_url": doctor[9],
        "doctor_id": doctor[0],
        "department_id": doctor[10],
        "description": doctor[11]
    }

    return doctor_dict


@router.put("/profile/doctor/update/{doctor_id}", response_model=Dict[str, Any])
def update_doctor_me(
        doctor_update: schemas.DoctorUpdate,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        doctor_id: int = None,
        db: Session = Depends(deps.get_db)
):
    # Convert Pydantic model to dict and remove None values
    update_data = {k: v for k, v in doctor_update.dict().items() if v is not None}

    if current_user.get("role") == "Admin":
        updated_doctor = crud.update_doctor(db, doctor_id=doctor_id, doctor_data=update_data)
    else:
        user_id = current_user.get("user_id")
        updated_doctor = crud.update_doctor(db, doctor_id=user_id, doctor_data=update_data)

    if not updated_doctor:
        raise HTTPException(status_code=404, detail="Doctor not found")

    return {"message": "Doctor updated successfully", "doctor_id": updated_doctor["doctor_id"]}

@router.delete("/doctors/{doctor_id}", response_model=dict)
def delete_doctor(
    doctor_id: int,
    db: Session = Depends(deps.get_db),
    current_user=Depends(deps.get_current_user)
):
    if current_user["role"] != "Admin":
        raise HTTPException(status_code=403, detail="Not an admin account")

    deleted_doctor = crud_doctor.delete_doctor(db, doctor_id)
    if not deleted_doctor:
        raise HTTPException(status_code=404, detail="Doctor not found")

    return {"message": "Doctor deleted successfully", "doctor_id": doctor_id}