from fastapi import FastAPI, HTTPException, APIRouter, Depends
from pydantic import BaseModel, EmailStr

import schemas
from Email.email_utils import generate_otp, send_otp_utils
from Email.query_email import update_password_hash_with_email, update_otp_database, get_otp_in_five_minute
from Oauth import get_password_hash, create_access_token
from sqlalchemy.orm import Session
from Oauth import deps #get current user
from crud import get_user_by_email

from datetime import timedelta

from Email.email_utils import send_otp_email


class EmailRequest(BaseModel):
    email: EmailStr

class OTPVerifyRequest(BaseModel):
    email: EmailStr
    otp: str
    password: str

#hi
router = APIRouter( prefix="", tags=["Chuc nang email"])

#Gui OTP va luu xuong database dang password_hash
@router.post("/forgot-password/send-otp")
def send_otp(request: EmailRequest, db: Session = Depends(deps.get_db)):

    #Lay ket noi database
    #Thuc hien lenh de kiem tra email co ton tai
    #Duoi database neu loi quang ra roi
    result_user = get_user_by_email(db, request.email)

    #Neu email khong ton tai thi bao loi
    if not result_user: #Khong co email duoi database
        raise HTTPException(status_code=404, detail="Email khong ton tai.")

    #Tao OTP va ma hoa
    opt = generate_otp()
    opt_hashed = get_password_hash(opt)

    #Gui otp qua email
    #send_otp_utils(receiver_email=request.email, otp_code=opt)

    send_otp_email(receiver_email=request.email, otp_code=opt)

    #Luu ma OTP da hash vao truong password_hash
    update_password_hash_with_email(db, otp_hash=opt_hashed, email=request.email)

    return {"message": f"Ma OTP da duoc gui toi email: {request.email}"}


#Viet lai ham tao OTP
#Chuc nang reset password
@router.post("/password-reset/request")
def send_password_reset_otp(request: EmailRequest, db: Session = Depends(deps.get_db)):
        # 1. Kiểm tra người dùng tồn tại
        user = get_user_by_email(db, request.email)  # tuple(,)
        if not user:  # query rong ne
            raise HTTPException(status_code=404, detail="User not found")

        user_id = dict(user._mapping).get("user_id")

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
        #send_otp_utils(receiver_email=request.email, otp_code=otp)
        send_otp_email(receiver_email=request.email, otp_code=otp, full_name=dict(user._mapping).get("full_name"))

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
    print(result_infor_otp)
    if not result_infor_otp:
        raise HTTPException(status_code=404, detail="Invalid OTP or OTP has expired. Please check please.")

 #câp nhat mat khau moi
    password_hash = get_password_hash(request.password)
    update_password_hash_with_email(db=db, otp_hash=password_hash, email=request.email)

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