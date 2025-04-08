from sqlalchemy import text
from sqlalchemy.orm import Session

import schemas
import crud
from typing import Optional, Dict, Any, List

# PatientHealthMetrics CRUD operations
def get_patient_health_metric(db: Session, metric_id: int):
    query = text("SELECT * FROM PatientHealthMetrics WHERE metric_id = :metric_id")
    result = db.execute(query, {"metric_id": metric_id}).first()
    return result


def get_patient_health_metrics(db: Session, patient_id: int = None, skip: int = 0, limit: int = 100) -> List[dict]:
    if patient_id:
        query = text("""
            SELECT * FROM PatientHealthMetrics 
            WHERE patient_id = :patient_id 
            LIMIT :limit OFFSET :skip
        """)
        result = db.execute(query, {"patient_id": patient_id, "skip": skip, "limit": limit}).fetchall()
    else:
        query = text("SELECT * FROM PatientHealthMetrics LIMIT :limit OFFSET :skip")
        result = db.execute(query, {"skip": skip, "limit": limit}).fetchall()

    metrics = [
        {
            "metric_id": row[0],
            "patient_id": row[1],
            "recorded_at": row[2],
            "heart_rate": row[3],
            "blood_pressure_systolic": row[4],
            "blood_pressure_diastolic": row[5],
            "blood_sugar_level": row[6],
            "notes": row[7]
        } for row in result
    ]

    return metrics


def create_patient_health_metric(db: Session, metric: schemas.PatientHealthMetricsCreate):
    query = text("""
        INSERT INTO PatientHealthMetrics (patient_id, heart_rate, blood_pressure_systolic, blood_pressure_diastolic, blood_sugar_level, notes)
        VALUES (:patient_id, :heart_rate, :blood_pressure_systolic, :blood_pressure_diastolic, :blood_sugar_level, :notes)
    """)

    db.execute(
        query,
        {
            "patient_id": metric.patient_id,
            "heart_rate": metric.heart_rate,
            "blood_pressure_systolic": metric.blood_pressure_systolic,
            "blood_pressure_diastolic": metric.blood_pressure_diastolic,
            "blood_sugar_level": metric.blood_sugar_level,
            "notes": metric.notes
        }
    )
    db.commit()

    # Get the last inserted ID
    result = db.execute(text("SELECT LAST_INSERT_ID()")).scalar()

    return get_patient_health_metric(db, result)


def update_patient_health_metric(db: Session, metric_id: int, metric_data: Dict[str, Any]):
    # First check if metric exists
    metric = get_patient_health_metric(db, metric_id)
    if not metric:
        return None

    # Prepare update parts
    update_parts = []
    params = {"metric_id": metric_id}

    valid_fields = ["heart_rate", "blood_pressure_systolic", "blood_pressure_diastolic", "blood_sugar_level", "notes"]

    for key, value in metric_data.items():
        if key in valid_fields:
            update_parts.append(f"{key} = :{key}")
            params[key] = value

    if not update_parts:
        return metric

    # Build and execute update query
    query = text(f"""
        UPDATE PatientHealthMetrics
        SET {', '.join(update_parts)}
        WHERE metric_id = :metric_id
    """)

    db.execute(query, params)
    db.commit()

    return get_patient_health_metric(db, metric_id)


def delete_patient_health_metric(db: Session, metric_id: int):
    # First get the metric to return it
    metric = get_patient_health_metric(db, metric_id)
    if not metric:
        return None

    # Delete the metric
    query = text("DELETE FROM PatientHealthMetrics WHERE metric_id = :metric_id")
    db.execute(query, {"metric_id": metric_id})
    db.commit()

    return metric