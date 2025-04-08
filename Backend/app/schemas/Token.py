from pydantic import BaseModel, EmailStr, Field
from datetime import date, datetime
from typing import Optional, List

# Token schemas
class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    email: Optional[str] = None
    role: Optional[str] = None