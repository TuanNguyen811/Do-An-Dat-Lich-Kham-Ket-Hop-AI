from pydantic import BaseModel, EmailStr, Field
from datetime import date, datetime
from typing import Optional, List, Union

# PatientHealthMetrics schemas
class PatientHealthMetricsBase(BaseModel):
    patient_id: int
    heart_rate: Optional[int] = None
    blood_pressure_systolic: Optional[int] = None
    blood_pressure_diastolic: Optional[int] = None
    blood_sugar_level: Optional[float] = None
    notes: Optional[str] = None

class PatientHealthMetricsCreate(PatientHealthMetricsBase):
    pass

class PatientHealthMetricsUpdate(BaseModel):
    heart_rate: Optional[int] = None
    blood_pressure_systolic: Optional[int] = None
    blood_pressure_diastolic: Optional[int] = None
    blood_sugar_level: Optional[float] = None
    notes: Optional[str] = None

class PatientHealthMetricsInDB(PatientHealthMetricsBase):
    metric_id: int
    recorded_at: datetime

class PatientHealthMetricsResponse(PatientHealthMetricsBase):
    metric_id: int
    recorded_at: datetime

    class Config:
        orm_mode = True
        from_attributes = True
