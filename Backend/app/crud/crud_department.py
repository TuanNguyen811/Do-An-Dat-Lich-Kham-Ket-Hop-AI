from sqlalchemy import text
from sqlalchemy.orm import Session
import schemas
import crud
from typing import Optional, Dict, Any, List


# Department CRUD operations
def get_department_by_name(db: Session, name: str):
    query = text("SELECT * FROM Departments WHERE name = :name")
    result = db.execute(query, {"name": name}).first()
    return result

def get_department(db: Session, department_id: int):
    query = text("SELECT * FROM Departments WHERE department_id = :department_id")
    result = db.execute(query, {"department_id": department_id}).first()
    return result

def get_departments(db: Session, skip: int = 0, limit: int = 100) -> List[dict]:
    query = text("SELECT * FROM Departments LIMIT :limit OFFSET :skip")
    result = db.execute(query, {"skip": skip, "limit": limit}).fetchall()

    departments = [
        {"department_id": row[0], "name": row[1], "description": row[2], "avatar_url": row[3]} for row in result
    ]

    return departments

def create_department(db: Session, department: schemas.DepartmentCreate):
    query = text("""
        INSERT INTO Departments (name, description, avatar_url)
        VALUES (:name, :description, :avatar_url)
    """)

    db.execute(
        query,
        {
            "name": department.name,
            "description": department.description,
            "avatar_url": department.avatar_url
        }
    )
    db.commit()

    return get_department_by_name(db, department.name)

def update_department(db: Session, department_id: int, department_data: Dict[str, Any]):
    # First check if department exists
    department = get_department(db, department_id)
    if not department:
        return None

    # Prepare update parts
    update_parts = []
    params = {"department_id": department_id}

    valid_fields = ["name", "description", "avatar_url"]

    for key, value in department_data.items():
        if key in valid_fields:
            update_parts.append(f"{key} = :{key}")
            params[key] = value

    if not update_parts:
        return department

    # Build and execute update query
    query = text(f"""
        UPDATE Departments
        SET {', '.join(update_parts)}
        WHERE department_id = :department_id
    """)

    db.execute(query, params)
    db.commit()

    return get_department(db, department_id)

def delete_department(db: Session, department_id: int):
    # First get the department to return it
    department = get_department(db, department_id)
    if not department:
        return None

    # Delete the department
    query = text("DELETE FROM Departments WHERE department_id = :department_id")
    db.execute(query, {"department_id": department_id})
    db.commit()

    return department

def set_avatar_department(db: Session, department_id: int, avatar_url: str):
    query = text("""
        UPDATE Departments
        SET avatar_url = :avatar_url
        WHERE department_id = :department_id
    """)

    db.execute(query, {"avatar_url": avatar_url, "department_id": department_id})
    db.commit()

    return get_department(db, department_id)

def get_avatar_department(db: Session, department_id: int):
    # Get the user by ID
    query = text("SELECT avatar_url FROM Departments WHERE department_id = :department_id")
    result = db.execute(query, {"department_id": department_id}).first()

    if not result:
        return None

    # Return the avatar URL
    return result[0]
