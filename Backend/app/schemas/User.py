from pydantic import BaseModel, EmailStr, Field
from datetime import date, datetime
from typing import Optional, List, Union

# Base User schemas
class UserBase(BaseModel):
    email: EmailStr
    role: str = Field(..., description="Role must be 'Patient', 'Admin' or 'Doctor'")
    full_name: str
    phone: Optional[str] = None
    date_of_birth: Optional[date] = None
    gender: Optional[str] = None
    address: Optional[str] = None
    avatar_url: Optional[str] = None

class UserCreate(UserBase):
    password: str = Field(..., min_length=6)

class UserUpdate(BaseModel):
    email: Optional[EmailStr] = None
    full_name: Optional[str] = None
    phone: Optional[str] = None
    date_of_birth: Optional[date] = None
    gender: Optional[str] = None
    address: Optional[str] = None
    avatar_url: Optional[str] = None
    password: Optional[str] = None

class UserInDB(UserBase):
    user_id: int
    password_hash: str

class UserResponse(UserBase):
    user_id: int

    class Config:
        orm_mode = True
        from_attributes = True