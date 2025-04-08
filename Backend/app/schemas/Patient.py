from pydantic import BaseModel, EmailStr
from datetime import date
from typing import Optional

from schemas.User import *


# Patient specific schemas
class PatientBase(BaseModel):
    insurance_id: int
    pass

class PatientCreate(UserCreate, PatientBase):
    pass

class PatientUpdate(UserUpdate, PatientBase):
    insurance_id: Optional[int] = None
    pass

class PatientInDB(UserInDB, PatientBase):
    patient_id: int

class PatientResponse(UserResponse, PatientBase):
    patient_id: int

    class Config:
        orm_mode = True
        from_attributes = True
