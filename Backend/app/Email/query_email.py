from sqlalchemy.orm import Session
from sqlalchemy import text

def update_password_hash_with_email(db: Session, otp_hash, email):
    query = text("UPDATE Users SET password_hash = :otp WHERE email = :email")
    result = db.execute(query, {"otp": otp_hash, "email": email})
    db.commit()

    # Kiểm tra nếu có bản ghi được cập nhật bằng cách lấy bản ghi đầu tiên
    updated_user = db.execute(text("SELECT * FROM Users WHERE email = :email"), {"email": email}).first()

    if updated_user:
        return {"message": "Password updated successfully."}
    else:
        return {"message": "No user found with that email."}


