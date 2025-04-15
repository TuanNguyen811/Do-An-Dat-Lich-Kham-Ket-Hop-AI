from fastapi import FastAPI, HTTPException, APIRouter, Depends
from pydantic import BaseModel
from Email.email_utils import generate_otp, send_otp_utils
from Email.query_email import update_password_hash_with_email
from Oauth import get_password_hash
from sqlalchemy.orm import Session
from Oauth import deps #get current user
from crud import get_user_by_email


class EmailRequest(BaseModel):
    email: str

class OTPVerifyRequest(BaseModel):
    email: str
    otp: str

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

    #Ton tai thi tao OTP

    #Tao OTP va ma hoa
    opt = generate_otp()
    opt_hashed = get_password_hash(opt)

    #Gui otp qua email
    send_otp_utils(receiver_email=request.email, otp_code=opt)


    #Luu ma OTP da hash vao truong password_hash
    # update_password_hash_with_email(db, otp_hash=opt_hashed, email=request.email)

    return {"message": f"Ma OTP da duoc gui toi email: {request.email}"}