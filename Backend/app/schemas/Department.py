from pydantic import BaseModel
from typing import Optional, List


# Department schemas
class DepartmentBase(BaseModel):
    name: str
    description: Optional[str] = None
    avatar_url: Optional[str] = None

class DepartmentCreate(DepartmentBase):
    pass

class DepartmentUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    avatar_url: Optional[str] = None

class DepartmentInDB(DepartmentBase):
    department_id: int

class DepartmentResponse(DepartmentBase):
    department_id: int

    class Config:
        orm_mode = True
        from_attributes = True