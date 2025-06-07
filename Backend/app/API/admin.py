from fastapi import FastAPI,APIRouter, Depends, HTTPException, status, File, UploadFile
from fastapi.responses import FileResponse

from sqlalchemy.orm import Session
from datetime import timedelta
from typing import List, Optional, Dict, Any

from starlette.staticfiles import StaticFiles

import crud, schemas

from crud import *
from crud.crud_department import set_avatar_department, get_avatar_department
from crud.crud_user import set_avatar_user, get_avatar_user
from schemas import *
from Oauth import deps
from Oauth.security import create_access_token
from database.database import create_tables

from fastapi import Form

import os
import uuid
import shutil

router = APIRouter(prefix="", tags=["admin"])

AVATAR_DIR = os.path.join(os.getcwd(), "data", "avatars")
os.makedirs(AVATAR_DIR, exist_ok=True)

# Mount static files for serving avatars
router.mount("/avatars", StaticFiles(directory=AVATAR_DIR), name="avatars")

router = APIRouter(prefix="", tags=["admin"])

@router.get("/profile/admin", response_model=schemas.AdminResponse)
def read_admin_me(
        current_user=Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    if current_user["role"] != "Admin":
        raise HTTPException(status_code=403, detail="Not an admin account")

    admin = crud_admin.get_admin(db, admin_id=current_user["user_id"])
    if not admin:
        raise HTTPException(status_code=404, detail="Admin profile not found")

    # Convert row object to dictionary that can be serialized
    admin_dict = {column: getattr(admin, column) for column in admin._mapping.keys()}

    return admin_dict


@router.post("/admin_set_avatar_user", status_code=status.HTTP_200_OK)
async def admin_set_avatar_user(
        file: UploadFile = File(...),
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db),
        user_id: str = Form(...)  # Changed to str and made required
):
    if current_user["role"] != "Admin":
        raise HTTPException(status_code=403, detail="Not an admin account")

    if not file.filename:
        raise HTTPException(status_code=400, detail="File is required.")

    try:
        # Convert user_id to integer, removing any quotes
        user_id = int(user_id.strip('"'))

        # Generate safe file path
        ext = os.path.splitext(file.filename)[1].lower() or ".jpg"
        filename = f"{uuid.uuid4()}{ext}"
        file_path = os.path.join(AVATAR_DIR, filename)

        # Save file
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # Store relative path
        avatar_url = f"/avatars/{filename}"
        set_avatar_user(db, user_id, avatar_url)

        print(f"Avatar uploaded: {filename} (User ID: {user_id})")
        return {"filename": filename, "avatar_url": avatar_url}

    except ValueError:
        raise HTTPException(status_code=400, detail="user_id must be a valid integer")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Upload failed: {str(e)}")

    finally:
        file.file.close()

@router.get("/admin_get_avatar_user")
async def admin_get_avatar_user(
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db),
        user_id: Optional[int] = None
):
    if current_user["role"] != "Admin":
        raise HTTPException(status_code=403, detail="Not an admin account")

    avatar_url = get_avatar_user(db, user_id)

    if not avatar_url:
        raise HTTPException(status_code=404, detail="Avatar not found")

    file_path = os.path.join(AVATAR_DIR, os.path.basename(avatar_url))

    if not os.path.isfile(file_path):
        raise HTTPException(status_code=404, detail="Avatar file not found")

    return FileResponse(file_path)


@router.post("/admin_set_avatar_department", status_code=status.HTTP_200_OK)
async def admin_set_avatar_department(
        file: UploadFile = File(...),
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db),
        department_id: str = Form(...)  # First receive as string
):
    if current_user["role"] != "Admin":
        raise HTTPException(status_code=403, detail="Not an admin account")

    if not file.filename:
        raise HTTPException(status_code=400, detail="File is required.")

    try:
        # Convert department_id to integer, removing any quotes
        department_id = int(department_id.strip('"'))

        # Generate safe file path
        ext = os.path.splitext(file.filename)[1].lower() or ".jpg"
        filename = f"{uuid.uuid4()}{ext}"
        file_path = os.path.join(AVATAR_DIR, filename)

        # Save file
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # Store relative path
        avatar_url = f"/avatars/{filename}"

        set_avatar_department(db, department_id, avatar_url)

        print(f"Avatar uploaded: {filename} (department_id: {department_id})")
        return {"filename": filename, "avatar_url": avatar_url}

    except ValueError:
        raise HTTPException(status_code=400, detail="department_id must be a valid integer")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Upload failed: {str(e)}")

    finally:
        file.file.close()

@router.get("/admin_get_avatar_department")
async def admin_get_avatar_user(
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db),
        department_id: Optional[int] = None
):
    # if current_user["role"] != "Admin" or current_user["role"] != "Doctor":
    #     raise HTTPException(status_code=403, detail="Not an admin account")

    avatar_url = get_avatar_department(db, department_id)

    if not avatar_url:
        raise HTTPException(status_code=404, detail="Avatar not found")

    file_path = os.path.join(AVATAR_DIR, os.path.basename(avatar_url))

    if not os.path.isfile(file_path):
        raise HTTPException(status_code=404, detail="Avatar file not found")

    return FileResponse(file_path)