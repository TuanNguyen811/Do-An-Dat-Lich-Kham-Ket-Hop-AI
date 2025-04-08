from sqlalchemy import text
from sqlalchemy.orm import Session

import schemas
import crud
from typing import Optional, Dict, Any, List


# MedicalHistory CRUD operations
def get_medical_history(db: Session, history_id: int):
    query = text("SELECT * FROM MedicalHistory WHERE history_id = :history_id")
    result = db.execute(query, {"history_id": history_id}).first()
    return result


def get_medical_histories(db: Session, patient_id: int = None, skip: int = 0, limit: int = 100) -> List[dict]:
    if patient_id:
        query = text("""
            SELECT * FROM MedicalHistory 
            WHERE patient_id = :patient_id 
            LIMIT :limit OFFSET :skip
        """)
        result = db.execute(query, {"patient_id": patient_id, "skip": skip, "limit": limit}).fetchall()
    else:
        query = text("SELECT * FROM MedicalHistory LIMIT :limit OFFSET :skip")
        result = db.execute(query, {"skip": skip, "limit": limit}).fetchall()

    histories = [
        {
            "history_id": row[0],
            "appointment_id": row[1],
            "patient_id": row[2],
            "doctor_id": row[3],
            "department_id": row[4],
            "visit_date": row[5],
            "diagnosis": row[6],
            "treatment": row[7],
            "notes": row[8],
            "created_at": row[9]
        } for row in result
    ]

    return histories


def create_medical_history(db: Session, history: schemas.MedicalHistoryCreate):
    query = text("""
        INSERT INTO MedicalHistory (appointment_id, patient_id, doctor_id, department_id, visit_date, diagnosis, treatment, notes)
        VALUES (:appointment_id, :patient_id, :doctor_id, :department_id, :visit_date, :diagnosis, :treatment, :notes)
    """)

    db.execute(
        query,
        {
            "appointment_id": history.appointment_id,
            "patient_id": history.patient_id,
            "doctor_id": history.doctor_id,
            "department_id": history.department_id,
            "visit_date": history.visit_date,
            "diagnosis": history.diagnosis,
            "treatment": history.treatment,
            "notes": history.notes
        }
    )
    db.commit()

    # Get the last inserted ID
    result = db.execute(text("SELECT LAST_INSERT_ID()")).scalar()

    return get_medical_history(db, result)


def update_medical_history(db: Session, history_id: int, history_data: Dict[str, Any]):
    # First check if history exists
    history = get_medical_history(db, history_id)
    if not history:
        return None

    # Prepare update parts
    update_parts = []
    params = {"history_id": history_id}

    valid_fields = ["diagnosis", "treatment", "notes"]

    for key, value in history_data.items():
        if key in valid_fields:
            update_parts.append(f"{key} = :{key}")
            params[key] = value

    if not update_parts:
        return history

    # Build and execute update query
    query = text(f"""
        UPDATE MedicalHistory
        SET {', '.join(update_parts)}
        WHERE history_id = :history_id
    """)

    db.execute(query, params)
    db.commit()

    return get_medical_history(db, history_id)


def delete_medical_history(db: Session, history_id: int):
    # First get the history to return it
    history = get_medical_history(db, history_id)
    if not history:
        return None

    # Delete the history
    query = text("DELETE FROM MedicalHistory WHERE history_id = :history_id")
    db.execute(query, {"history_id": history_id})
    db.commit()

    return history
