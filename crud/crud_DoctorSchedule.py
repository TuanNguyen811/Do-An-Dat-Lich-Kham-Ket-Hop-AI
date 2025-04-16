from sqlalchemy import text
from sqlalchemy.orm import Session

import schemas
from typing import Optional, Dict, Any, List


# DoctorSchedule CRUD operations
def get_doctor_schedule(db: Session, schedule_id: int):
    query = text("""
        SELECT * 
        FROM DoctorSchedules 
        WHERE schedule_id = :schedule_id
    """)
    result = db.execute(query, {"schedule_id": schedule_id}).first()
    return result

def get_doctor_schedules(db: Session, skip: int = 0, limit: int = 100, doctor_id: Optional[int] = None) -> List[dict]:
    query = text("""
        SELECT * 
        FROM DoctorSchedules 
        WHERE (:doctor_id IS NULL OR doctor_id = :doctor_id)
        LIMIT :limit OFFSET :skip
    """)
    result = db.execute(query, {"skip": skip, "limit": limit, "doctor_id": doctor_id}).fetchall()

    schedules = [
        {
            "schedule_id": row[0],
            "doctor_id": row[1],
            "week": row[2],
            "Monday": row[3],
            "Tuesday": row[4],
            "Wednesday": row[5],
            "Thursday": row[6],
            "Friday": row[7],
            "Saturday": row[8],
            "Sunday": row[9]
        } for row in result
    ]

    return schedules

def get_doctor_schedules_by_week(db: Session, week: int, skip: int = 0, limit: int = 100, doctor_id: Optional[int] = None) -> List[dict]:
    query = text("""
        SELECT * 
        FROM DoctorSchedules 
        WHERE (:doctor_id IS NULL OR doctor_id = :doctor_id)
        AND week = :week
        LIMIT :limit OFFSET :skip
    """)
    result = db.execute(query, {"skip": skip, "limit": limit, "doctor_id": doctor_id, "week": week}).fetchall()

    schedules = [
        {
            "schedule_id": row[0],
            "doctor_id": row[1],
            "week": row[2],
            "Monday": row[3],
            "Tuesday": row[4],
            "Wednesday": row[5],
            "Thursday": row[6],
            "Friday": row[7],
            "Saturday": row[8],
            "Sunday": row[9]
        } for row in result
    ]

    return schedules

def create_doctor_schedule(db: Session, schedule: schemas.DoctorScheduleCreate):
    query = text("""
        INSERT INTO DoctorSchedules (doctor_id, week, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday)
        VALUES (:doctor_id, :week, :Monday, :Tuesday, :Wednesday, :Thursday, :Friday, :Saturday, :Sunday)
    """)
    db.execute(
        query,
        {
            "doctor_id": schedule.doctor_id,
            "week": schedule.week,
            "Monday": schedule.Monday,
            "Tuesday": schedule.Tuesday,
            "Wednesday": schedule.Wednesday,
            "Thursday": schedule.Thursday,
            "Friday": schedule.Friday,
            "Saturday": schedule.Saturday,
            "Sunday": schedule.Sunday
        }
    )
    db.commit()

    return get_doctor_schedule(db, db.execute(text("SELECT LAST_INSERT_ID()")).scalar())

def update_doctor_schedule(db: Session, schedule_id: int, schedule_data: Dict[str, Any]):
    # First check if schedule exists
    schedule = get_doctor_schedule(db, schedule_id)
    if not schedule:
        return None

    # Update schedule data
    update_parts = []
    params = {"schedule_id": schedule_id}

    for key, value in schedule_data.items():
        update_parts.append(f"{key} = :{key}")
        params[key] = value

    query = text(f"""
        UPDATE DoctorSchedules
        SET {', '.join(update_parts)}
        WHERE schedule_id = :schedule_id
    """)

    db.execute(query, params)
    db.commit()

    return get_doctor_schedule(db, schedule_id)

def delete_doctor_schedule(db: Session, schedule_id: int):
    # First get the schedule to verify it exists
    schedule = get_doctor_schedule(db, schedule_id)
    if not schedule:
        return None

    # Delete from DoctorSchedules table
    query = text("DELETE FROM DoctorSchedules WHERE schedule_id = :schedule_id")
    db.execute(query, {"schedule_id": schedule_id})
    db.commit()

    return schedule