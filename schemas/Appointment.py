from pydantic import BaseModel, EmailStr, Field
from datetime import date, datetime
from typing import Optional, List, Union

# Appointment schemas
class AppointmentBase(BaseModel):
    patient_id: int
    doctor_id: int
    department_id: int
    appointment_date: datetime
    shift: str = Field(..., description="Shift must be 'Shift 1', 'Shift 2', 'Shift 3' or 'Shift 4'")
    description: Optional[str] = None

class AppointmentCreate(AppointmentBase):
    pass

class AppointmentUpdate(BaseModel):
    appointment_date: Optional[datetime] = None
    shift: Optional[str] = None
    description: Optional[str] = None
    status: Optional[str] = None

class AppointmentInDB(AppointmentBase):
    appointment_id: int
    status: str
    created_at: datetime

class AppointmentResponse(AppointmentBase):
    appointment_id: int
    status: str
    created_at: datetime

    class Config:
        orm_mode = True
        from_attributes = True
