from pydantic import BaseModel, EmailStr

# Login schema - already using OAuth2PasswordRequestForm in the endpoint
class Login(BaseModel):
    email: EmailStr
    password: str