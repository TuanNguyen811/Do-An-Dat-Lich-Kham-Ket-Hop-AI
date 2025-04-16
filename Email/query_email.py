from sqlalchemy.orm import Session
from sqlalchemy import text

def update_password_hash_with_email(db: Session, otp_hash, email):
    query = text("UPDATE Users SET password_hash = :otp WHERE email = :email")
    db.execute(query, {"otp": otp_hash, "email": email})
    db.commit()

    # Kiểm tra nếu có bản ghi được cập nhật bằng cách lấy bản ghi đầu tiên
    updated_user = db.execute(text("SELECT * FROM Users WHERE email = :email"), {"email": email}).first()

    if updated_user:
        return {"message": "Password updated successfully."}
    else:
        return {"message": "No user found with that email."}

def update_otp_database(db: Session, user_id, otp_hash, email):
    query = text("""INSERT INTO user_otps (email, user_id, otp)
                    VALUES (:email, :user_id, :otp)
                    ON DUPLICATE KEY UPDATE 
                        otp = VALUES(otp),
                        created_at = CURRENT_TIMESTAMP;
""")
    db.execute(query, {"email": email, "otp": otp_hash, "user_id": user_id})
    db.commit()

    #Kiem tra da cap nhat thanh cong hay chua
    updated_user = db.execute(
        text("SELECT 1 FROM user_otps WHERE email = :email"),
        {"email": email}
        ).first()
    if updated_user:
        return {"message": "OTP updated successfully."}
    else:
        return {"message": "No OTP code found with that email."}



def get_otp_in_five_minute(db: Session, email, otp_hash):
    query = text("""SELECT * FROM user_otps
                    WHERE email = :email and otp = :otp and created_at >= NOW() - INTERVAL 5 MINUTE;
    """)
    result_infor_otp = db.execute(query, {"email": email, "otp": otp_hash}).first()
    return result_infor_otp