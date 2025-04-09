from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime

# Request schema for creating or updating health metrics
class PatientHealthMetricsCreate(BaseModel):
    patient_id: Optional[int] = Field(None, description="ID of the patient")
    systolic_bp: Optional[int] = Field(None, description="Systolic blood pressure (mmHg)")
    diastolic_bp: Optional[int] = Field(None, description="Diastolic blood pressure (mmHg)")
    heart_rate: Optional[int] = Field(None, description="Heart rate (bpm)")
    body_temperature: Optional[float] = Field(None, description="Body temperature (°C)")
    respiratory_rate: Optional[int] = Field(None, description="Respiratory rate (breaths/min)")
    weight_kg: Optional[float] = Field(None, description="Weight (kg)")
    height_cm: Optional[float] = Field(None, description="Height (cm)")
    bmi: Optional[float] = Field(None, description="Body Mass Index (kg/m²)")
    blood_glucose: Optional[float] = Field(None, description="Blood glucose (mg/dL)")
    cholesterol_total: Optional[float] = Field(None, description="Total cholesterol (mg/dL)")
    ldl: Optional[float] = Field(None, description="Low-density lipoprotein (mg/dL)")
    hdl: Optional[float] = Field(None, description="High-density lipoprotein (mg/dL)")
    triglycerides: Optional[float] = Field(None, description="Triglycerides (mg/dL)")
    hemoglobin: Optional[float] = Field(None, description="Hemoglobin (g/dL)")

# Response schema for retrieving health metrics
class PatientHealthMetricsResponse(PatientHealthMetricsCreate):
    id: int
    recorded_at: datetime

    class Config:
        orm_mode = True