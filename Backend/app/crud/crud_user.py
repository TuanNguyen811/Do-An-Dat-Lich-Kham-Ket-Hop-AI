from sqlalchemy import text
from sqlalchemy.orm import Session

from Oauth import verify_password, security
import schemas
from typing import Optional, Dict, Any, List


# User CRUD operations
def get_user_by_email(db: Session, email: str):
    query = text("SELECT * FROM Users WHERE email = :email")
    result = db.execute(query, {"email": email}).first()
    return result


def get_user(db: Session, user_id: int):
    query = text("SELECT * FROM Users WHERE user_id = :user_id")
    result = db.execute(query, {"user_id": user_id}).first()
    return result


def get_users(db: Session, skip: int = 0, limit: int = 100) -> List[dict]:
    query = text("SELECT * FROM Users LIMIT :limit OFFSET :skip")
    result = db.execute(query, {"skip": skip, "limit": limit}).fetchall()

    users = [
        {
            "user_id": row[0],
            "email": row[1],
            "password_hash": row[2],
            "role": row[3],
            "full_name": row[4],
            "phone": row[5],
            "date_of_birth": row[6],
            "gender": row[7],
            "address": row[8],
            "avatar_url": row[9]
        } for row in result
    ]

    return users


def create_user(db: Session, user: schemas.UserCreate):
    # In a real application, you would hash the password here
    password_hash = security.get_password_hash(user.password)

    query = text("""
        INSERT INTO Users (email, password_hash, role, full_name, phone, date_of_birth, gender, address, avatar_url)
        VALUES (:email, :password_hash, :role, :full_name, :phone, :date_of_birth, :gender, :address, :avatar_url)
    """)

    db.execute(
        query,
        {
            "email": user.email,
            "password_hash": password_hash,
            "role": user.role,
            "full_name": user.full_name,
            "phone": user.phone,
            "date_of_birth": user.date_of_birth,
            "gender": user.gender,
            "address": user.address,
            "avatar_url": user.avatar_url
        }
    )
    db.commit()

    return get_user_by_email(db, user.email)


def update_user(db: Session, user_id: int, user_data: Dict[str, Any]):
    # First check if user exists
    user = get_user(db, user_id)
    if not user:
        return None

    # Prepare update parts
    update_parts = []
    params = {"user_id": user_id}

    valid_fields = ["email", "role", "full_name", "phone", "date_of_birth", "gender", "address", "avatar_url"]

    # Add password_hash if password is being updated
    if "password" in user_data:
        valid_fields.append("password_hash")
        # In a real application, you would hash the password here
       # password_hash = security.get_password_hash()
        password_hash = security.get_password_hash(user_data["password"])
        user_data["password_hash"] = password_hash

    for key, value in user_data.items():
        if key in valid_fields:
            update_parts.append(f"{key} = :{key}")
            params[key] = value

    if not update_parts:
        return user

    # Build and execute update query
    query = text(f"""
        UPDATE Users
        SET {', '.join(update_parts)}
        WHERE user_id = :user_id
    """)

    db.execute(query, params)
    db.commit()

    return get_user(db, user_id)


def set_avatar_user(db: Session, user_id: int, avatar_url: str):
    query = text("""
        UPDATE Users
        SET avatar_url = :avatar_url
        WHERE user_id = :user_id
    """)

    db.execute(query, {"avatar_url": avatar_url, "user_id": user_id})
    db.commit()

    return get_user(db, user_id)


def get_avatar_user(db: Session, user_id: int):
    # Get the user by ID
    query = text("SELECT avatar_url FROM Users WHERE user_id = :user_id")
    result = db.execute(query, {"user_id": user_id}).first()

    if not result:
        return None

    # Return the avatar URL
    return result[0]

def delete_user(db: Session, user_id: int):
    # First get the user to return it
    user = get_user(db, user_id)
    if not user:
        return None

    # Delete the user
    query = text("DELETE FROM Users WHERE user_id = :user_id")
    db.execute(query, {"user_id": user_id})
    db.commit()

    return user


def authenticate_user(db: Session, email: str, password: str):
    # Get the user by email
    query = text("SELECT * FROM Users WHERE email = :email")
    user = db.execute(query, {"email": email}).first()

    if not user:
        return False

    # Extract the password hash from the user record
    password_hash = user[2]  # Assuming the password_hash is at index 2

    if not verify_password(password, password_hash):
        return False

    # Convert user object to dict
    user_dict = {
        "user_id": user[0],
        "email": user[1],
        "role": user[3],
        "full_name": user[4]
    }

    return user_dict