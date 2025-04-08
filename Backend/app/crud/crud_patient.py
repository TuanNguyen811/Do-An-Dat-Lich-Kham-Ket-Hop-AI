from sqlalchemy import text
from sqlalchemy.orm import Session

import schemas
import crud
from typing import Optional, Dict, Any, List

from crud import create_user, update_user


# Patient CRUD operations
def get_patient(db: Session, patient_id: int):
    query = text("""
        SELECT u.*, p.insurance_id, p.patient_id
        FROM Patients p
        JOIN Users u ON p.patient_id = u.user_id
        WHERE p.patient_id = :patient_id
    """)
    result = db.execute(query, {"patient_id": patient_id}).first()
    return result

def get_patients(db: Session, skip: int = 0, limit: int = 100) -> List[dict]:
    query = text("""
        SELECT u.* 
        FROM Patients p
        JOIN Users u ON p.patient_id = u.user_id
        LIMIT :limit OFFSET :skip
    """)
    result = db.execute(query, {"skip": skip, "limit": limit}).fetchall()

    patients = [
        {
            "user_id": row[0],
            "email": row[1],
            "password_hash": row[2],
            "role": row[3],
            "full_name": row[4],
            "phone": row[5],
            "date_of_birth": row[6],
            "gender": row[7],
            "address": row[8],
            "avatar_url": row[9],
            "patient_id": row[0],  # Same as user_id
            "insurance_id": row[10]
        } for row in result
    ]

    return patients

def create_patient(db: Session, patient: schemas.PatientCreate):
    # First create the user
    user = create_user(db, patient)
    if not user:
        return None

    user_id = user[0]  # Get the user_id from the result

    # Then create the patient
    query = text("""
        INSERT INTO Patients (patient_id, insurance_id)
        VALUES (:patient_id, :insurance_id)
    """)


    db.execute(
        query,
        {
            "patient_id": user_id,
            "insurance_id": patient.insurance_id
        }
    )
    db.commit()

    return get_patient(db, user_id)

def update_patient(db: Session, patient_id: int, patient_data: Dict[str, Any]):
    # First check if patient exists
    patient = get_patient(db, patient_id)
    if not patient:
        return None

    # Update user data
    update_user(db, patient_id, patient_data)

    return get_patient(db, patient_id)

def delete_patient(db: Session, patient_id: int):
    # First get the patient to verify it exists
    patient = get_patient(db, patient_id)
    if not patient:
        return None

    # Delete from Patients table
    query = text("DELETE FROM Patients WHERE patient_id = :patient_id")
    db.execute(query, {"patient_id": patient_id})

    # Then delete from Users table
    query = text("DELETE FROM Users WHERE user_id = :user_id")
    db.execute(query, {"user_id": patient_id})

    db.commit()

    return patient