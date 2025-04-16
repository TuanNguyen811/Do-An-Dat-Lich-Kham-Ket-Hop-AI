from sqlalchemy import text
from sqlalchemy.orm import Session

import schemas
import crud
from typing import Optional, Dict, Any, List

from crud import create_user, update_user


# Admin CRUD operations
def get_admin(db: Session, admin_id: int):
    query = text("""
        SELECT u.*, a.admin_id
        FROM Admins a
        JOIN Users u ON a.admin_id = u.user_id
        WHERE a.admin_id = :admin_id
    """)
    result = db.execute(query, {"admin_id": admin_id}).first()
    return result


def get_admins(db: Session, skip: int = 0, limit: int = 100) -> List[dict]:
    query = text("""
        SELECT u.* 
        FROM Admins a
        JOIN Users u ON a.admin_id = u.user_id
        LIMIT :limit OFFSET :skip
    """)
    result = db.execute(query, {"skip": skip, "limit": limit}).fetchall()

    admins = [
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
            "avatar_url": row[9],
            "admin_id": row[0]  # Same as user_id
        } for row in result
    ]

    return admins

def create_admin(db: Session, admin: schemas.AdminCreate):
    # First create the user
    user = create_user(db, admin)
    if not user:
        return None

    user_id = user[0]  # Get the user_id from the result

    # Then create the admin
    query = text("""
        INSERT INTO Admins (admin_id)
        VALUES (:admin_id)
    """)

    db.execute(
        query,
        {
            "admin_id": user_id
        }
    )
    db.commit()

    return get_admin(db, user_id)

def update_admin(db: Session, admin_id: int, admin_data: Dict[str, Any]):
    # First check if admin exists
    admin = get_admin(db, admin_id)
    if not admin:
        return None

    # Update user data
    update_user(db, admin_id, admin_data)

    return get_admin(db, admin_id)

def delete_admin(db: Session, admin_id: int):
    # First get the admin to verify it exists
    admin = get_admin(db, admin_id)
    if not admin:
        return None

    # Delete from Admins table
    query = text("DELETE FROM Admins WHERE admin_id = :admin_id")
    db.execute(query, {"admin_id": admin_id})

    # Then delete from Users table
    query = text("DELETE FROM Users WHERE user_id = :user_id")
    db.execute(query, {"user_id": admin_id})

    db.commit()

    return admin