from pydantic import BaseModel, EmailStr, Field
from datetime import date, datetime
from typing import Optional, List, Union


# MedicalHistory schemas
class MedicalHistoryBase(BaseModel):
    appointment_id: int
    patient_id: int
    doctor_id: int
    department_id: int
    visit_date: datetime
    diagnosis: Optional[str] = None
    treatment: Optional[str] = None
    notes: Optional[str] = None

class MedicalHistoryCreate(MedicalHistoryBase):
    pass

class MedicalHistoryUpdate(BaseModel):
    diagnosis: Optional[str] = None
    treatment: Optional[str] = None
    notes: Optional[str] = None

class MedicalHistoryInDB(MedicalHistoryBase):
    history_id: int
    created_at: datetime

class MedicalHistoryResponse(MedicalHistoryBase):
    history_id: int
    created_at: datetime

    class Config:
        orm_mode = True
        from_attributes = True