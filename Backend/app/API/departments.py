from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import Dict, Any, List

import crud
import schemas
from Oauth import deps

router = APIRouter(prefix="", tags=["departments"])


# Department routes
@router.get("/departments", response_model=List[Dict[str, Any]])
def get_departments(
        skip: int = 0,
        limit: int = 100,
        db: Session = Depends(deps.get_db),
):
    departments = crud.get_departments(db, skip=skip, limit=limit)
    return departments


@router.post("/departments", response_model=Dict[str, Any])
def create_department(
        department: schemas.DepartmentCreate,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    if current_user.get("role") != "Admin":
        raise HTTPException(status_code=403, detail="Only admins can create departments")

    result = crud.create_department(db=db, department=department)
    if not result:
        raise HTTPException(status_code=500, detail="Failed to create department")

    return {"message": "Department created successfully", "department_id": result[0]}


@router.put("/departments/{department_id}", response_model=Dict[str, Any])
def update_department(
        department_id: int,
        department_update: schemas.DepartmentUpdate,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    if current_user.get("role") != "Admin":
        raise HTTPException(status_code=403, detail="Only admins can update departments")

    # Convert Pydantic model to dict and remove None values
    update_data = {k: v for k, v in department_update.dict().items() if v is not None}

    # Update the department
    updated_department = crud.update_department(db, department_id=department_id, department_data=update_data)
    if not updated_department:
        raise HTTPException(status_code=404, detail="Department not found")

    return {"message": "Department updated successfully", "department_id": updated_department.department_id}


@router.delete("/departments/{department_id}", response_model=Dict[str, Any])
def delete_department(
        department_id: int,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    if current_user.get("role") != "Admin":
        raise HTTPException(status_code=403, detail="Only admins can delete departments")

    # Delete the department
    deleted_department = crud.delete_department(db, department_id=department_id)
    if not deleted_department:
        raise HTTPException(status_code=404, detail="Department not found")

    return {"message": "Department deleted successfully", "department_id": deleted_department.department_id}
