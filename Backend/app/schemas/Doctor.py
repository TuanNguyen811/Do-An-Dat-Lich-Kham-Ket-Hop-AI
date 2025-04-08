from pydantic import BaseModel
from typing import Optional

from schemas import *

# Doctor specific schemas
class DoctorBase(BaseModel):
    department_id: int
    description: str

class DoctorCreate(UserCreate, DoctorBase):
    pass

class DoctorUpdate(UserUpdate):
    department_id: Optional[int] = None
    description: Optional[str] = None

class DoctorInDB(UserInDB, DoctorBase):
    doctor_id: int

class DoctorResponse(UserResponse, DoctorBase):
    doctor_id: int

    class Config:
        orm_mode = True
        from_attributes = True