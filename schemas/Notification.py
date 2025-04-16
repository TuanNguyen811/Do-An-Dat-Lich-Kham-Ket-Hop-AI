from pydantic import BaseModel, EmailStr, Field
from datetime import date, datetime
from typing import Optional, List, Union


# Notification schemas
class NotificationBase(BaseModel):
    user_id: int
    type: str = Field(..., description="Type must be 'Appointment' or 'Medication'")
    message: str
    scheduled_time: datetime

class NotificationCreate(NotificationBase):
    pass

class NotificationUpdate(BaseModel):
    message: Optional[str] = None
    scheduled_time: Optional[datetime] = None
    status: Optional[str] = None

class NotificationInDB(NotificationBase):
    notification_id: int
    status: str
    created_at: datetime

class NotificationResponse(NotificationBase):
    notification_id: int
    status: str
    created_at: datetime

    class Config:
        orm_mode = True
        from_attributes = True