from pydantic import BaseModel
from typing import Optional


class DoctorScheduleBase(BaseModel):
    doctor_id: int
    week: int
    Monday: Optional[str] = None
    Tuesday: Optional[str] = None
    Wednesday: Optional[str] = None
    Thursday: Optional[str] = None
    Friday: Optional[str] = None
    Saturday: Optional[str] = None
    Sunday: Optional[str] = None

class DoctorScheduleCreate(DoctorScheduleBase):
    pass

class DoctorScheduleUpdate(DoctorScheduleBase):
    pass

class DoctorSchedule(DoctorScheduleBase):
    id: int

    class Config:
        orm_mode = True