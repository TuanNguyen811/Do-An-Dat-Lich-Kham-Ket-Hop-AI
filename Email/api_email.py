from fastapi import FastAPI, HTTPException, APIRouter, Depends
from pydantic import BaseModel, EmailStr

import schemas
from Email.email_utils import generate_otp, send_otp_utils, send_otp_email
from Email.query_email import update_password_hash_with_email, update_otp_database, get_otp_in_five_minute
from Oauth import get_password_hash, create_access_token
from sqlalchemy.orm import Session
from Oauth import deps #get current user
from crud import get_user_by_email

from datetime import timedelta

class EmailRequest(BaseModel):
    email: EmailStr

class OTPVerifyRequest(BaseModel):
    email: EmailStr
    otp: str


router = APIRouter( prefix="", tags=["Chức năng với email"])


#Chuc nang reset password
@router.post("/password-reset/request")
def send_password_reset_otp(request: EmailRequest, db: Session = Depends(deps.get_db)):
        # 1. Kiểm tra người dùng tồn tại
        user = get_user_by_email(db, request.email)  # tuple(,)
        if not user:  # query rong ne
            raise HTTPException(status_code=404, detail="User not found")

        user_id = dict(user._mapping).get("user_id")

        #Ten user de gui email
        full_name = dict(user._mapping).get("full_name")
        # 2. Tạo OTP và hash OTP
        otp = generate_otp()
        #otp_hashed = get_password_hash(otp)
        #print(otp_hashed)

        # 3. Cập nhật bảng OTP
        try:
            update_otp_database(db=db, user_id=user_id, otp_hash=otp, email=request.email)
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Lỗi khi lưu OTP {e}")

        # Gửi email với OTP
        send_otp_email(receiver_email=request.email, otp_code=otp, full_name=full_name)


        return {"message": "Password reset OTP sent to your email"}

@router.post("/password-reset/verify-otp", response_model=schemas.Token)
def verify_otp(request: OTPVerifyRequest, db: Session = Depends(deps.get_db)):
    # 1. Kiểm tra người dùng tồn tại
    user = get_user_by_email(db, request.email)  # tuple(,)
    if not user:  # query rong ne
        raise HTTPException(status_code=404, detail="User not found")

    #Chuyển otp của người dùng thành otp_hash
    #otp_hash = get_password_hash(request.otp)
    #print(otp_hash)
    #Lấy OTP với email tương ứng dưới database
    result_infor_otp = get_otp_in_five_minute(db=db, email=request.email, otp_hash=request.otp)
    if not result_infor_otp:
        raise HTTPException(status_code=404, detail="Invalid OTP or OTP has expired. Please check please.")

    otp_infor_dictionary = dict(result_infor_otp._mapping) #Dung thi dung khong dung thi thoi

    #Xac thuc otp thanh cong
    #Tao token cho nguoi dung de dang nhap
    user_dictionary = dict(user._mapping)

    access_token_expires = timedelta(minutes=deps.ACCESS_TOKEN_EXPIRE_MINUTES)

    access_token = create_access_token(
        data={"sub": user_dictionary.get("email"),
              "role": user_dictionary.get("role"),
              "user_id": user_dictionary.get("user_id"),
              "full_name": user_dictionary.get("full_name")},
        expires_delta=access_token_expires
    )
    return {"access_token": access_token,
            "token_type": "bearer",
            "user_id": user_dictionary.get("user_id")
    }

