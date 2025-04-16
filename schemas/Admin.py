from pydantic import BaseModel, EmailStr, Field
from datetime import date, datetime
from typing import Optional, List, Union

from schemas import User, UserCreate, UserUpdate, UserInDB, UserResponse


# Admin specific schemas
class AdminBase(BaseModel):
    # Add any admin-specific fields here if needed
    pass

class AdminCreate(UserCreate, AdminBase):
    pass

class AdminUpdate(UserUpdate, AdminBase):
    pass

class AdminInDB(UserInDB, AdminBase):
    admin_id: int

class AdminResponse(UserResponse, AdminBase):
    admin_id: int

    class Config:
        orm_mode = True
        from_attributes = True