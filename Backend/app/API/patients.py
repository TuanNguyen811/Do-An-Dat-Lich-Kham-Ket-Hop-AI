from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import Dict, Any, List

import crud
import schemas
from Oauth import deps
from crud import crud_patient

router = APIRouter(prefix="", tags=["patients"])

# Registration routes
@router.post("/register/patient", response_model=Dict[str, Any])
def register_patient(
        patient: schemas.PatientCreate,
        db: Session = Depends(deps.get_db),
):

    # Check if user with email already exists
    db_user = crud.get_user_by_email(db, email=patient.email)
    if db_user:
        raise HTTPException(status_code=400, detail="Email already registered")

    # Set role to Patient for the user
    patient.role = "Patient"

    # Create patient (this will create a user first and then a patient)
    result = crud.create_patient(db=db, patient=patient)

    if not result:
        raise HTTPException(status_code=500, detail="Failed to register patient")

    # Convert result to dict for response
    return {"message": "Patient registered successfully", "user_id": result[0]}


# User profile routes
@router.get("/profile/patient", response_model=schemas.PatientResponse)
def read_patient_me(
        current_user=Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    if current_user["role"] != "Patient":
        raise HTTPException(status_code=403, detail="Not a patient account")

    patient = crud_patient.get_patient(db, patient_id=current_user["user_id"])

    if not patient:
        raise HTTPException(status_code=404, detail="Patient profile not found")

    # Convert row object to dictionary that can be serialized
    patient_dict = {column: getattr(patient, column) for column in patient._mapping.keys()}
    return patient_dict


# Patient routes
@router.get("/patients", response_model=List[Dict[str, Any]])
def get_patients(
        skip: int = 0,
        limit: int = 100,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    if current_user.get("role") not in ["Admin", "Doctor"]:
        raise HTTPException(status_code=403, detail="Not authorized to view all patients")

    patients = crud.get_patients(db, skip=skip, limit=limit)
    return patients


@router.get("/patients/{patient_id}", response_model=Dict[str, Any])
def get_patient(
        patient_id: int,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    if current_user.get("role") in ["Admin", "Doctor"]:
        # Admins and Doctors can view any patient
        pass
    else:
        raise HTTPException(status_code=403, detail="Not authorized to view this patient")

    patient = crud.get_patient(db, patient_id=patient_id)
    if not patient:
        raise HTTPException(status_code=404, detail="Patient not found")

    # Convert row to dict
    patient_dict = {
        "user_id": patient[0],
        "email": patient[1],
        "role": patient[3],
        "full_name": patient[4],
        "phone": patient[5],
        "date_of_birth": patient[6],
        "gender": patient[7],
        "address": patient[8],
        "avatar_url": patient[9],
        "patient_id": patient[0],
        "insurance_id": patient[10]
    }

    return patient_dict

@router.put("/profile/patient/update", response_model=Dict[str, Any])
def update_patient_me(
        patient_update: schemas.PatientUpdate,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    if current_user.get("role") != "Patient":
        raise HTTPException(status_code=403, detail="Not a patient account")

    user_id = current_user.get("user_id")

    # Convert Pydantic model to dict and remove None values
    update_data = {k: v for k, v in patient_update.dict().items() if v is not None}

    # Update the patient
    updated_patient = crud.update_patient(db, patient_id=user_id, patient_data=update_data)
    if not updated_patient:
        raise HTTPException(status_code=404, detail="Patient not found")

    # Get the updated patient data to return
    patient = crud.get_patient(db, patient_id=user_id)
    if not patient:
        raise HTTPException(status_code=404, detail="Patient not found")

    # Convert row to dict
    patient_dict = {
        "user_id": patient[0],
        "email": patient[1],
        "role": patient[3],
        "full_name": patient[4],
        "phone": patient[5],
        "date_of_birth": patient[6],
        "gender": patient[7],
        "address": patient[8],
        "avatar_url": patient[9],
        "patient_id": patient[0],
        "insurance_id": patient[10]
    }

    return {"message": "Patient updated successfully", "patient": patient_dict}