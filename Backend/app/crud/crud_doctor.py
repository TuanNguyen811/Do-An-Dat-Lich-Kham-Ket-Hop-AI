from sqlalchemy import text
from sqlalchemy.orm import Session

import schemas
import crud
from typing import Optional, Dict, Any, List

from crud import create_user, update_user


# Doctor CRUD operations
def get_doctor(db: Session, doctor_id: int):
    query = text("""
        SELECT u.*, d.department_id, d.description
        FROM Doctors d
        JOIN Users u ON d.doctor_id = u.user_id
        WHERE d.doctor_id = :doctor_id
    """)
    result = db.execute(query, {"doctor_id": doctor_id}).first()
    return result

def get_doctors(db: Session, skip: int = 0, limit: int = 100) -> List[dict]:
    query = text("""
        SELECT u.*, d.department_id, d.description 
        FROM Doctors d
        JOIN Users u ON d.doctor_id = u.user_id
        LIMIT :limit OFFSET :skip
    """)
    result = db.execute(query, {"skip": skip, "limit": limit}).fetchall()

    doctors = [
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
            "doctor_id": row[0],  # Same as user_id
            "department_id": row[10],
            "description": row[11]
        } for row in result
    ]

    return doctors

def create_doctor(db: Session, doctor: schemas.DoctorCreate):
    # First create the user
    user = create_user(db, doctor)
    if not user:
        return None

    user_id = user[0]  # Get the user_id from the result

    # Then create the doctor
    query = text("""
        INSERT INTO Doctors (doctor_id, department_id, description)
        VALUES (:doctor_id, :department_id, :description)
    """)

    db.execute(
        query,
        {
            "doctor_id": user_id,
            "department_id": doctor.department_id,
            "description": doctor.description
        }
    )
    db.commit()

    return get_doctor(db, user_id)

def update_doctor(db: Session, doctor_id: int, doctor_data: Dict[str, Any]):
    # First check if doctor exists
    doctor = get_doctor(db, doctor_id)
    if not doctor:
        return None

    # Separate user data and doctor-specific data
    doctor_specific_fields = ["department_id", "description"]
    user_data = {k: v for k, v in doctor_data.items() if k not in doctor_specific_fields}
    doctor_specific_data = {k: v for k, v in doctor_data.items() if k in doctor_specific_fields}

    # Update user data if any
    if user_data:
        update_user(db, doctor_id, user_data)

    # Update doctor-specific data if any
    if doctor_specific_data:
        update_parts = []
        params = {"doctor_id": doctor_id}

        for key, value in doctor_specific_data.items():
            update_parts.append(f"{key} = :{key}")
            params[key] = value

        query = text(f"""
            UPDATE Doctors
            SET {', '.join(update_parts)}
            WHERE doctor_id = :doctor_id
        """)

        db.execute(query, params)
        db.commit()

    return get_doctor(db, doctor_id)

def delete_doctor(db: Session, doctor_id: int):
    # First get the doctor to verify it exists
    doctor = get_doctor(db, doctor_id)
    if not doctor:
        return None

    # Delete from Doctors table
    query = text("DELETE FROM Doctors WHERE doctor_id = :doctor_id")
    db.execute(query, {"doctor_id": doctor_id})

    # Then delete from Users table
    query = text("DELETE FROM Users WHERE user_id = :user_id")
    db.execute(query, {"user_id": doctor_id})

    db.commit()

    return doctor