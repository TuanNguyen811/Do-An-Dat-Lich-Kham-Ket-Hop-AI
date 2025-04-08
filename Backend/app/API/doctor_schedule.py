from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import Dict, Any, List, Optional

import schemas
from Oauth import deps
from crud import crud_DoctorSchedule

router = APIRouter(prefix="", tags=["schedules"])

@router.post("/schedules", response_model=Dict[str, Any])
def create_schedule(
        schedule: schemas.DoctorScheduleCreate,
        db: Session = Depends(deps.get_db),
        current_user=Depends(deps.get_current_user)
):
    if current_user["role"] != "Admin":
        raise HTTPException(status_code=403, detail="Not an admin account")

    # Create schedule
    result = crud_DoctorSchedule.create_doctor_schedule(db=db, schedule=schedule)
    if not result:
        raise HTTPException(status_code=500, detail="Failed to create schedule")

    return {"message": "Schedule created successfully", "schedule_id": result.schedule_id}

@router.get("/schedules", response_model=List[Dict[str, Any]])
def get_schedules(
        skip: int = 0,
        limit: int = 100,
        doctor_id: int = None,
        db: Session = Depends(deps.get_db)
):
    schedules = crud_DoctorSchedule.get_doctor_schedules(db, skip=skip, limit=limit, doctor_id=doctor_id)
    return schedules


@router.get("/schedules/{schedule_id}", response_model=Dict[str, Any])
def get_schedule(
        schedule_id: int,
        db: Session = Depends(deps.get_db)
):
    schedule = crud_DoctorSchedule.get_doctor_schedule(db, schedule_id=schedule_id)
    if not schedule:
        raise HTTPException(status_code=404, detail="Schedule not found")

    return schedule


@router.put("/schedules/{schedule_id}", response_model=Dict[str, Any])
def update_schedule(
        schedule_id: int,
        schedule_update: schemas.DoctorScheduleUpdate,
        db: Session = Depends(deps.get_db),
        current_user=Depends(deps.get_current_user)
):
    if current_user["role"] != "Admin":
        raise HTTPException(status_code=403, detail="Not an admin account")

    # Update schedule
    updated_schedule = crud_DoctorSchedule.update_doctor_schedule(db, schedule_id=schedule_id, schedule_data=schedule_update.dict())
    if not updated_schedule:
        raise HTTPException(status_code=404, detail="Schedule not found")

    return {"message": "Schedule updated successfully"}


@router.delete("/schedules/{schedule_id}", response_model=Dict[str, Any])
def delete_schedule(
        schedule_id: int,
        db: Session = Depends(deps.get_db),
        current_user=Depends(deps.get_current_user)
):
    if current_user["role"] != "Admin":
        raise HTTPException(status_code=403, detail="Not an admin account")

    # Delete schedule
    result = crud_DoctorSchedule.delete_doctor_schedule(db, schedule_id=schedule_id)
    if not result:
        raise HTTPException(status_code=404, detail="Schedule not found")

    return {"message": "Schedule deleted successfully"}